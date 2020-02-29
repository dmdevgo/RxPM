package me.dmdev.rxpm.sample.main.util

import com.google.i18n.phonenumbers.*
import java.util.*

private const val MAX_PHONE_LENGTH = 17

fun onlyPhone(phoneNumberString: String): String {
    return "+${phoneNumberString.onlyDigits()}"
}

fun String.onlyDigits() = this.replace("\\D".toRegex(), "")

class PhoneUtil {

    private val countriesMap = HashMap<String, Country>()
    private val phoneNumberUtil = PhoneNumberUtil.getInstance()

    init {
        for (region in phoneNumberUtil.supportedRegions) {
            val country = Country(region, phoneNumberUtil.getCountryCodeForRegion(region))
            countriesMap[region] = country
        }
    }

    @Throws(NumberParseException::class)
    fun parsePhone(phone: String): Phonenumber.PhoneNumber {
        return phoneNumberUtil.parse(phone, PhoneNumberUtil.REGION_CODE_FOR_NON_GEO_ENTITY)
    }

    fun formatPhoneNumber(country: Country, phoneNumber: String): String {

        if (country === Country.UNKNOWN) return phoneNumber.onlyDigits()

        val code = "+${country.countryCallingCode}"
        var formattedPhone = code + phoneNumber.onlyDigits()

        val asYouTypeFormatter = phoneNumberUtil.getAsYouTypeFormatter(country.region)

        for (ch in (code + phoneNumber.onlyDigits()).toCharArray()) {
            formattedPhone = asYouTypeFormatter.inputDigit(ch)
        }

        return formattedPhone.replace(code, "").trim()
    }

    fun formatPhoneNumber(phoneNumberString: String): String {

        val phoneNumber = onlyPhone(phoneNumberString).take(MAX_PHONE_LENGTH)
        var formattedPhone: String = phoneNumber

        val asYouTypeFormatter =
            phoneNumberUtil.getAsYouTypeFormatter(PhoneNumberUtil.REGION_CODE_FOR_NON_GEO_ENTITY)

        for (ch in phoneNumber.toCharArray()) {
            formattedPhone = asYouTypeFormatter.inputDigit(ch)
        }

        return formattedPhone.trim()
    }

    fun isValidPhone(phoneNumber: String): Boolean {
        return try {
            phoneNumberUtil.isValidNumber(
                phoneNumberUtil.parse(onlyPhone(phoneNumber), null)
            )
        } catch (e: Exception) {
            false
        }
    }

    fun isValidPhone(country: Country, phoneNumber: String): Boolean {
        return try {
            val number = Phonenumber.PhoneNumber().apply {
                countryCode = country.countryCallingCode
                nationalNumber = phoneNumber.onlyDigits().toLong()
            }
            phoneNumberUtil.isValidNumberForRegion(number, country.region)
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun getCountryForCountryCode(code: Int): Country {
        return countriesMap[phoneNumberUtil.getRegionCodeForCountryCode(code)] ?: Country.UNKNOWN
    }

    fun countries(): List<Country> {
        return countriesMap.values.toList()
    }
}