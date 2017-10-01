package me.dmdev.rxpm.sample.ui.main

import io.reactivex.android.schedulers.AndroidSchedulers
import me.dmdev.rxpm.bindProgress
import me.dmdev.rxpm.sample.LogoutCompletedMessage
import me.dmdev.rxpm.sample.model.AuthModel
import me.dmdev.rxpm.sample.ui.base.ScreenPresentationModel
import me.dmdev.rxpm.skipWhileInProgress

class MainPm(
        private val authModel: AuthModel
) : ScreenPresentationModel() {

    val inProgress = State(false)

    val logoutAction = Action<Unit>()

    override fun onCreate() {
        super.onCreate()
        logoutAction.observable
                .skipWhileInProgress(inProgress.observable)
                .flatMapCompletable {
                    authModel.logout()
                            .observeOn(AndroidSchedulers.mainThread())
                            .bindProgress(inProgress.consumer)
                            .doOnError { showError(it.message) }
                            .doOnComplete { sendMessage(LogoutCompletedMessage()) }
                }
                .subscribe()
                .untilDestroy()
    }
}