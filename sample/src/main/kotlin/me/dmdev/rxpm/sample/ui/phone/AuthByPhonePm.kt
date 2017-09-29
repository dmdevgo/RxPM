package me.dmdev.rxpm.sample.ui.phone

import com.google.i18n.phonenumbers.NumberParseException
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import me.dmdev.rxpm.bindProgress
import me.dmdev.rxpm.sample.ChooseCountryMessage
import me.dmdev.rxpm.sample.PhoneSentSuccessfullyMessage
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.extensions.onlyDigits
import me.dmdev.rxpm.sample.model.AuthModel
import me.dmdev.rxpm.sample.ui.base.ScreenPresentationModel
import me.dmdev.rxpm.sample.util.Country
import me.dmdev.rxpm.sample.util.PhoneUtil
import me.dmdev.rxpm.sample.util.ResourceProvider
import me.dmdev.rxpm.skipWhileInProgress
import me.dmdev.rxpm.widget.clickControl
import me.dmdev.rxpm.widget.inputControl

/**
 * @author Dmitriy Gorbunov
 */

class AuthByPhonePm(
        private val phoneUtil: PhoneUtil,
        private val resourceProvider: ResourceProvider,
        private val authModel: AuthModel
) : ScreenPresentationModel() {

    val chosenCountry = State<Country>()
    val phoneNumber = inputControl()
    val countryCode = inputControl(
            initialText = "+7",
            formatter = {
                val code = "+${it.onlyDigits().take(5)}"
                if (code.length > 5) {
                    try {
                        val number = phoneUtil.parsePhone(code)
                        phoneNumber.requestFocus.consumer.accept(Unit)
                        phoneNumber.textChanges.consumer.accept(number.nationalNumber.toString())
                        "+${number.countryCode}"
                    } catch (e: NumberParseException) {
                        code
                    }
                } else {
                    code
                }
            }
    )

    val inProgress = State(false)

    val doneButton = clickControl(initialEnabled = false)
    val countryClicks = Action<Unit>()
    val chooseCountryAction = Action<Country>()

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
                .subscribe(chosenCountry.consumer)
                .untilDestroy()

        Observable.combineLatest(phoneNumber.textChanges.observable, chosenCountry.observable,
                                 BiFunction<String, Country, String> { number, country ->
                                     phoneUtil.formatPhoneNumber(country, number)
                                 })
                .subscribe(phoneNumber.text.consumer)
                .untilDestroy()


        Observable.combineLatest(phoneNumber.textChanges.observable, chosenCountry.observable,
                                 BiFunction<String, Country, Boolean> { number, country ->
                                     phoneUtil.isValidPhone(country, number)
                                 })
                .subscribe(doneButton.enabled.consumer)
                .untilDestroy()

        countryClicks.observable
                .subscribe {
                    sendMessage(ChooseCountryMessage())
                }
                .untilDestroy()

        chooseCountryAction.observable
                .subscribe {
                    countryCode.textChanges.consumer.accept("+${it.countryCallingCode}")
                    chosenCountry.consumer.accept(it)
                    phoneNumber.requestFocus.consumer.accept(Unit)
                }
                .untilDestroy()

        doneButton.clicks.observable
                .skipWhileInProgress(inProgress.observable)
                .filter { validateForm() }
                .map { "${countryCode.text.value} ${phoneNumber.text.value}" }
                .flatMapCompletable { phone ->
                   authModel.sendPhone(phone)
                           .observeOn(AndroidSchedulers.mainThread())
                           .bindProgress(inProgress.consumer)
                           .doOnComplete { sendMessage(PhoneSentSuccessfullyMessage(phone)) }
                           .doOnError { showError(it.message) }
                }
                .retry()
                .subscribe()
                .untilDestroy()
    }

    private fun validateForm(): Boolean {

        return if (phoneNumber.text.value.isEmpty()) {
            phoneNumber.error.consumer.accept(resourceProvider.getString(R.string.enter_phone_number))
            false
        } else if (!phoneUtil.isValidPhone(chosenCountry.value, phoneNumber.text.value)) {
            phoneNumber.error.consumer.accept(resourceProvider.getString(R.string.invalid_phone_number))
            false
        } else {
            true
        }

    }

}