package me.dmdev.rxpm.sample.main.ui.confirmation

import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.AppNavigationMessage.*
import me.dmdev.rxpm.sample.main.model.*
import me.dmdev.rxpm.sample.main.ui.base.*
import me.dmdev.rxpm.sample.main.util.*
import me.dmdev.rxpm.validation.*
import me.dmdev.rxpm.widget.*

class CodeConfirmationPm(
    private val phone: String,
    private val resourceProvider: ResourceProvider,
    private val authModel: AuthModel
) : ScreenPresentationModel() {

    companion object {
        private const val CODE_LENGTH = 4
    }

    val code = inputControl(
        formatter = { it.onlyDigits().take(CODE_LENGTH) }
    )
    val inProgress = state(false)

    val sendButtonEnabled = state(false) {
        code.text.observable.map { it.length == CODE_LENGTH }
    }

    private val codeFilled = code.text.observable
        .filter { it.length == CODE_LENGTH }
        .distinctUntilChanged()
        .map { Unit }

    val sendClicks = action<Unit> {
        this.mergeWith(codeFilled)
            .skipWhileInProgress(inProgress)
            .map { code.text.value }
            .filter { formValidator.validate() }
            .switchMapCompletable { code ->
                authModel.sendConfirmationCode(phone, code)
                    .bindProgress(inProgress)
                    .doOnComplete { sendMessage(PhoneConfirmed) }
                    .doOnError(errorConsumer)
            }
            .toObservable<Any>()
    }

    private val formValidator = formValidator {
        input(code) {
            empty(resourceProvider.getString(R.string.enter_confirmation_code))
            minSymbols(CODE_LENGTH, resourceProvider.getString(R.string.invalid_confirmation_code))
        }
    }
}