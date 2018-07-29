package me.dmdev.rxpm.sample.main.model

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import me.dmdev.rxpm.sample.main.api.ServerApi
import me.dmdev.rxpm.sample.main.util.onlyDigits


class AuthModel(
    private val api: ServerApi,
    private val tokenStorage: TokenStorage
) {

    fun isAuth() = tokenStorage.getToken().isNotEmpty()

    fun sendPhone(phone: String): Completable {
        return api.sendPhone(phone.onlyDigits())
            .subscribeOn(Schedulers.io())
    }

    fun sendConfirmationCode(phone: String, code: String): Completable {
        return api.sendConfirmationCode(phone.onlyDigits(), code.onlyDigits())
            .subscribeOn(Schedulers.io())
            .doOnSuccess { tokenStorage.saveToken(it.token) }
            .ignoreElement()
    }

    fun logout(): Completable {
        return api.logout(tokenStorage.getToken())
            .subscribeOn(Schedulers.io())
            .doOnComplete { tokenStorage.clear() }
    }
}