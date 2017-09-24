package me.dmdev.rxpm.sample.util

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import me.dmdev.rxpm.sample.extansions.onlyDigits
import java.util.*

/**
 * @author Dmitriy Gorbunov
 */
class PhoneUtil {

    private val countriesMap = HashMap<String, Country>()
    private val phoneNumberUtil = PhoneNumberUtil.getInstance()

    init {
        for (region in phoneNumberUtil.supportedRegions) {
            val country = Country(region, phoneNumberUtil.getCountryCodeForRegion(region))
            countriesMap.put(region, country)
        }
    }

    @Throws(NumberParseException::class)
    fun parsePhone(phone: String): Phonenumber.PhoneNumber {
        return phoneNumberUtil.parse(phone, PhoneNumberUtil.REGION_CODE_FOR_NON_GEO_ENTITY)
    }

    fun formatPhone(phone: String): String {
        return try {
            val phoneNumber = parsePhone("+${phone.onlyDigits()}")
            phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        } catch (e: NumberParseException) {
            phone
        }
    }

    fun formatPhoneNumber(country: Country, phoneNumber: String): String {

        if (country === Country.UNKNOWN) return phoneNumber.onlyDigits()

        val code = "+${country.countryCallingCode}"
        var formattedPhone = code + phoneNumber.onlyDigits()

        val asYouTypeFormatter = phoneNumberUtil.getAsYouTypeFormatter(country.region)
        asYouTypeFormatter.clear()

        for (ch in (code + phoneNumber.onlyDigits()).toCharArray()) {
            formattedPhone = asYouTypeFormatter.inputDigit(ch)
        }

        return formattedPhone.replace(code, "").trim()
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

    fun getCountryForRegion(region: String) = countriesMap[region] ?: Country.UNKNOWN

    fun countries(): List<Country> {
        return countriesMap.values.toList()
    }
}
