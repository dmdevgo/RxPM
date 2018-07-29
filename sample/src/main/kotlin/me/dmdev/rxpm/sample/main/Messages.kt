package me.dmdev.rxpm.sample.main

import me.dmdev.rxpm.navigation.NavigationMessage
import me.dmdev.rxpm.sample.main.util.Country

class BackMessage : NavigationMessage

class ChooseCountryMessage : NavigationMessage
class CountryChosenMessage(val country: Country) : NavigationMessage
class PhoneSentSuccessfullyMessage(val phone: String) : NavigationMessage
class PhoneConfirmedMessage : NavigationMessage
class LogoutCompletedMessage : NavigationMessage