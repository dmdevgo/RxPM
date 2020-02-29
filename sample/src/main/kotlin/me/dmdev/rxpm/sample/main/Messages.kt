package me.dmdev.rxpm.sample.main

import me.dmdev.rxpm.navigation.*
import me.dmdev.rxpm.sample.main.util.*

sealed class AppNavigationMessage : NavigationMessage {
    object Back : AppNavigationMessage()
    object ChooseCountry : AppNavigationMessage()
    class CountryChosen(val country: Country) : AppNavigationMessage()
    class PhoneSentSuccessfully(val phone: String) : AppNavigationMessage()
    object PhoneConfirmed : AppNavigationMessage()
    object LogoutCompleted : AppNavigationMessage()
}