package me.dmdev.rxpm.sample.main.ui.confirmation

import me.dmdev.rxpm.action
import me.dmdev.rxpm.bindProgress
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.AppNavigationMessage.PhoneConfirmed
import me.dmdev.rxpm.sample.main.model.AuthModel
import me.dmdev.rxpm.sample.main.ui.base.ScreenPresentationModel
import me.dmdev.rxpm.sample.main.util.ResourceProvider
import me.dmdev.rxpm.sample.main.util.onlyDigits
import me.dmdev.rxpm.skipWhileInProgress
import me.dmdev.rxpm.state
import me.dmdev.rxpm.validation.empty
import me.dmdev.rxpm.validation.formValidator
import me.dmdev.rxpm.validation.input
import me.dmdev.rxpm.validation.minSymbols
import me.dmdev.rxpm.widget.inputControl

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

    private val codeFilled = code.textChanges.observable
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