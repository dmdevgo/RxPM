package me.dmdev.rxpm.sample.main.ui.confirmation

import io.reactivex.Observable
import me.dmdev.rxpm.bindProgress
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.PhoneConfirmedMessage
import me.dmdev.rxpm.sample.main.model.AuthModel
import me.dmdev.rxpm.sample.main.ui.base.ScreenPresentationModel
import me.dmdev.rxpm.sample.main.util.ResourceProvider
import me.dmdev.rxpm.sample.main.util.onlyDigits
import me.dmdev.rxpm.skipWhileInProgress
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
    val inProgress = State(false)
    val sendButtonEnabled = State(false)

    val sendAction = Action<Unit>()

    override fun onCreate() {
        super.onCreate()

        val codeFilledAction = code.text.observable
            .filter { it.length == CODE_LENGTH }
            .distinctUntilChanged()

        Observable.merge(sendAction.observable, codeFilledAction)
            .skipWhileInProgress(inProgress.observable)
            .map { code.text.value }
            .filter { validateForm() }
            .switchMapCompletable { code ->
                authModel.sendConfirmationCode(phone, code)
                    .bindProgress(inProgress.consumer)
                    .doOnComplete { sendMessage(PhoneConfirmedMessage()) }
                    .doOnError { showError(it.message) }
            }
            .retry()
            .subscribe()
            .untilDestroy()

        code.text.observable
            .map { it.length == CODE_LENGTH }
            .subscribe(sendButtonEnabled.consumer)
            .untilDestroy()

    }

    private fun validateForm(): Boolean {

        return when {
            code.text.value.isEmpty() -> {
                code.error.consumer.accept(resourceProvider.getString(R.string.enter_confirmation_code))
                false
            }
            code.text.value.length < CODE_LENGTH -> {
                code.error.consumer.accept(resourceProvider.getString(R.string.invalid_confirmation_code))
                false
            }
            else -> true
        }

    }
}