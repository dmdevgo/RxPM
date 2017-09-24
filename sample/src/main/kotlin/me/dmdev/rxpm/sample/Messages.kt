package me.dmdev.rxpm.sample

import me.dmdev.rxpm.sample.util.Country

/**
 * @author Dmitriy Gorbunov
 */

interface PmMessageHandler {
    fun handleMessage(message: PmMessage): Boolean
}

interface PmMessage

class UpMessage : PmMessage
class BackMessage : PmMessage

class ChooseCountryMessage : PmMessage
class CountryChosenMessage(val country: Country) : PmMessage