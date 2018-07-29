package me.dmdev.rxpm.sample.main.api

import io.reactivex.Completable
import io.reactivex.Single


interface ServerApi {
    fun sendPhone(phone: String): Completable
    fun sendConfirmationCode(phone: String, code: String): Single<TokenResponse>
    fun logout(token: String): Completable
}

class WrongConfirmationCode(message: String) : Throwable(message)
class ServerError(message: String) : Throwable(message)

class TokenResponse(val token: String)