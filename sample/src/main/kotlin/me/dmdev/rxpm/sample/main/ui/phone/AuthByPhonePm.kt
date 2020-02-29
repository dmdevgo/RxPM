package me.dmdev.rxpm.sample.main.ui.phone

import com.google.i18n.phonenumbers.*
import io.reactivex.rxkotlin.Observables.combineLatest
import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.*
import me.dmdev.rxpm.sample.main.AppNavigationMessage.*
import me.dmdev.rxpm.sample.main.model.*
import me.dmdev.rxpm.sample.main.ui.base.*
import me.dmdev.rxpm.sample.main.util.*
import me.dmdev.rxpm.validation.*
import me.dmdev.rxpm.widget.*


class AuthByPhonePm(
    private val phoneUtil: PhoneUtil,
    private val resourceProvider: ResourceProvider,
    private val authModel: AuthModel
) : ScreenPresentationModel() {

    val phoneNumber = inputControl(formatter = null)
    val countryCode = inputControl(
        initialText = "+7",
        formatter = {
            val code = "+${it.onlyDigits().take(5)}"
            if (code.length > 5) {
                try {
                    val number = phoneUtil.parsePhone(code)
                    phoneNumber.focus.accept(true)
                    phoneNumber.textChanges.accept(number.nationalNumber.toString())
                    "+${number.countryCode}"
                } catch (e: NumberParseException) {
                    code
                }
            } else {
                code
            }
        }
    )
    val chosenCountry = state<Country> {
        countryCode.text.observable
            .map {
                val code = it.onlyDigits()
                if (code.isNotEmpty()) {
                    phoneUtil.getCountryForCountryCode(code.onlyDigits().toInt())
                } else {
                    Country.UNKNOWN
                }
            }
    }

    val inProgress = state(false)

    val sendButtonEnabled = state(false) {
        combineLatest(
            phoneNumber.textChanges.observable,
            chosenCountry.observable
        ) { number: String, country: Country ->
            phoneUtil.isValidPhone(country, number)
        }
    }

    val countryClicks = action<Unit> {
        this.map { AppNavigationMessage.ChooseCountry }
            .doOnNext(navigationMessages.consumer)
    }

    val chooseCountry = action<Country> {
        this.doOnNext {
            countryCode.textChanges.accept("+${it.countryCallingCode}")
            chosenCountry.accept(it)
            phoneNumber.focus.accept(true)
        }
    }

    val sendClicks = action<Unit> {
        this.skipWhileInProgress(inProgress)
            .filter { formValidator.validate() }
            .map { "${countryCode.text.value} ${phoneNumber.text.value}" }
            .switchMapCompletable { phone ->
                authModel.sendPhone(phone)
                    .bindProgress(inProgress)
                    .doOnComplete { sendMessage(PhoneSentSuccessfully(phone)) }
                    .doOnError(errorConsumer)
            }
            .toObservable<Any>()
    }

    override fun onCreate() {
        super.onCreate()

        combineLatest(
            phoneNumber.textChanges.observable,
            chosenCountry.observable
        ) { number: String, country: Country ->
            phoneUtil.formatPhoneNumber(country, number)
        }
            .subscribe(phoneNumber.text)
            .untilDestroy()
    }

    private val formValidator = formValidator {
        input(phoneNumber) {
            empty(resourceProvider.getString(R.string.enter_phone_number))
            valid(
                validation = { phoneNumber ->
                    phoneUtil.isValidPhone(chosenCountry.value, phoneNumber)
                },
                errorMessage = resourceProvider.getString(R.string.invalid_phone_number)
            )
        }
    }
}