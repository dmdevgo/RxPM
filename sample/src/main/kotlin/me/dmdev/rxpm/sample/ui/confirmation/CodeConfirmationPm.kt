package me.dmdev.rxpm.sample.ui.confirmation

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import me.dmdev.rxpm.bindProgress
import me.dmdev.rxpm.sample.PhoneConfirmedMessage
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.extensions.onlyDigits
import me.dmdev.rxpm.sample.model.AuthModel
import me.dmdev.rxpm.sample.ui.base.ScreenPresentationModel
import me.dmdev.rxpm.sample.util.ResourceProvider
import me.dmdev.rxpm.skipWhileInProgress
import me.dmdev.rxpm.widget.clickControl
import me.dmdev.rxpm.widget.inputControl

class CodeConfirmationPm(
        private val phone: String,
        private val resourceProvider: ResourceProvider,
        private val authModel: AuthModel
) : ScreenPresentationModel() {

    companion object {
        private const val CODE_LENGTH = 4
    }

    val code = inputControl(formatter = { it.onlyDigits().take(CODE_LENGTH) })
    val inProgress = State(false)

    val doneButton = clickControl(initialEnabled = false)

    override fun onCreate() {
        super.onCreate()

        Observable.merge(doneButton.clicks.observable,
                         code.textChanges.observable
                                 .filter { it.length == CODE_LENGTH }
                                 .distinctUntilChanged())
                .skipWhileInProgress(inProgress.observable)
                .map { code.text.value }
                .filter { validateForm() }
                .flatMapCompletable { code ->
                    authModel.sendConfirmationCode(phone, code)
                            .observeOn(AndroidSchedulers.mainThread())
                            .bindProgress(inProgress.consumer)
                            .doOnComplete { sendMessage(PhoneConfirmedMessage()) }
                            .doOnError { showError(it.message) }
                }
                .retry()
                .subscribe()
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