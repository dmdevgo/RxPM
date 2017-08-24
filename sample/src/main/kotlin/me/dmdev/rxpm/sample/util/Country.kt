package me.dmdev.rxpm.sample.util

/**
 * @author Dmitriy Gorbunov
 */

class Country(val region: String, val countryCallingCode: Int) {

    companion object {
        val UNKNOWN_REGION = "ZZ"
        val INVALID_COUNTRY_CODE = 0
        val UNKNOWN = Country(UNKNOWN_REGION, INVALID_COUNTRY_CODE)
    }
}
