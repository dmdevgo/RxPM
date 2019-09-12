package me.dmdev.rxpm.sample.main.ui.phone

import com.google.i18n.phonenumbers.*
import io.reactivex.*
import io.reactivex.functions.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.*
import me.dmdev.rxpm.sample.main.model.*
import me.dmdev.rxpm.sample.main.ui.base.*
import me.dmdev.rxpm.sample.main.util.*
import me.dmdev.rxpm.widget.*


class AuthByPhonePm(
    private val phoneUtil: PhoneUtil,
    private val resourceProvider: ResourceProvider,
    private val authModel: AuthModel
) : ScreenPresentationModel() {

    val chosenCountry = state<Country>()
    val phoneNumber = inputControl(formatter = null)
    val countryCode = inputControl(
        initialText = "+7",
        formatter = {
            val code = "+${it.onlyDigits().take(5)}"
            if (code.length > 5) {
                try {
                    val number = phoneUtil.parsePhone(code)
                    phoneNumberFocus.post(Unit)
                    phoneNumber.textChanges.post(number.nationalNumber.toString())
                    "+${number.countryCode}"
                } catch (e: NumberParseException) {
                    code
                }
            } else {
                code
            }
        }
    )

    val inProgress = state(false)
    val sendButtonEnabled = state(false)
    val phoneNumberFocus = command<Unit>(bufferSize = 1)

    val sendAction = action<Unit>()
    val countryClicks = action<Unit>()
    val chooseCountryAction = action<Country>()

    override fun onCreate() {
        super.onCreate()

        countryCode.text.observable
            .map {
                val code = it.onlyDigits()
                if (code.isNotEmpty()) {
                    phoneUtil.getCountryForCountryCode(code.onlyDigits().toInt())
                } else {
                    Country.UNKNOWN
                }
            }
            .subscribe(chosenCountry)
            .untilDestroy()

        Observable.combineLatest(phoneNumber.textChanges.observable, chosenCountry.observable,
            BiFunction { number: String, country: Country ->
                phoneUtil.formatPhoneNumber(country, number)
            })
            .subscribe(phoneNumber.text)
            .untilDestroy()


        Observable.combineLatest(phoneNumber.textChanges.observable, chosenCountry.observable,
            BiFunction { number: String, country: Country ->
                phoneUtil.isValidPhone(country, number)
            })
            .subscribe(sendButtonEnabled)
            .untilDestroy()

        countryClicks.observable
            .subscribe {
                sendMessage(ChooseCountryMessage())
            }
            .untilDestroy()

        chooseCountryAction.observable
            .subscribe {
                countryCode.textChanges.post("+${it.countryCallingCode}")
                chosenCountry.post(it)
                phoneNumberFocus.post(Unit)
            }
            .untilDestroy()

        sendAction.observable
            .skipWhileInProgress(inProgress)
            .filter { validateForm() }
            .map { "${countryCode.text.value} ${phoneNumber.text.value}" }
            .switchMapCompletable { phone ->
                authModel.sendPhone(phone)
                    .bindProgress(inProgress)
                    .doOnComplete {
                        sendMessage(PhoneSentSuccessfullyMessage(phone))
                    }
                    .doOnError { showError(it.message) }
            }
            .retry()
            .subscribe()
            .untilDestroy()
    }

    private fun validateForm(): Boolean {

        return if (phoneNumber.text.value.isEmpty()) {
            phoneNumber.error.post(resourceProvider.getString(R.string.enter_phone_number))
            false
        } else if (!phoneUtil.isValidPhone(chosenCountry.value, phoneNumber.text.value)) {
            phoneNumber.error.post(resourceProvider.getString(R.string.invalid_phone_number))
            false
        } else {
            true
        }

    }

}