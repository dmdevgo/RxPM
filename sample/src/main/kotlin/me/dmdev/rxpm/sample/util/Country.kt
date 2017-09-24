package me.dmdev.rxpm.sample.util

import java.util.*

/**
 * @author Dmitriy Gorbunov
 */

class Country(val region: String, val countryCallingCode: Int) {

    val name = Locale("en", region).getDisplayCountry(Locale.ENGLISH)

    companion object {
        val UNKNOWN_REGION = "ZZ"
        val INVALID_COUNTRY_CODE = 0
        val UNKNOWN = Country(UNKNOWN_REGION, INVALID_COUNTRY_CODE)
    }
}
