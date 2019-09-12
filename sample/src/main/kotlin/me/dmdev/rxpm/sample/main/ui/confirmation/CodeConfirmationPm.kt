package me.dmdev.rxpm.sample.main.ui.confirmation

import io.reactivex.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.*
import me.dmdev.rxpm.sample.main.model.*
import me.dmdev.rxpm.sample.main.ui.base.*
import me.dmdev.rxpm.sample.main.util.*
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
    val sendButtonEnabled = state(false)

    val sendAction = action<Unit>()

    override fun onCreate() {
        super.onCreate()

        val codeFilledAction = code.text.observable
            .filter { it.length == CODE_LENGTH }
            .distinctUntilChanged()

        Observable.merge(sendAction.observable, codeFilledAction)
            .skipWhileInProgress(inProgress)
            .map { code.text.value }
            .filter { validateForm() }
            .switchMapCompletable { code ->
                authModel.sendConfirmationCode(phone, code)
                    .bindProgress(inProgress)
                    .doOnComplete { sendMessage(PhoneConfirmedMessage()) }
                    .doOnError { showError(it.message) }
            }
            .retry()
            .subscribe()
            .untilDestroy()

        code.text.observable
            .map { it.length == CODE_LENGTH }
            .subscribe(sendButtonEnabled)
            .untilDestroy()

    }

    private fun validateForm(): Boolean {

        return when {
            code.text.value.isEmpty() -> {
                code.error.post(resourceProvider.getString(R.string.enter_confirmation_code))
                false
            }
            code.text.value.length < CODE_LENGTH -> {
                code.error.post(resourceProvider.getString(R.string.invalid_confirmation_code))
                false
            }
            else -> true
        }

    }
}