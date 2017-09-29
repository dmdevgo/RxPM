package me.dmdev.rxpm.sample

import me.dmdev.rxpm.sample.util.Country

/**
 * @author Dmitriy Gorbunov
 */

interface NavigationMessageHandler {
    fun handleMessage(message: NavigationMessage): Boolean
}

interface NavigationMessage

class UpMessage : NavigationMessage
class BackMessage : NavigationMessage

class ChooseCountryMessage : NavigationMessage
class CountryChosenMessage(val country: Country) : NavigationMessage
class PhoneSentSuccessfullyMessage(val phone: String) : NavigationMessage