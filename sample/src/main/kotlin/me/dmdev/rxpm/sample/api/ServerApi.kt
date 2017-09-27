package me.dmdev.rxpm.sample.api

import io.reactivex.Completable
import io.reactivex.Single

/**
 * @author Dmitriy Gorbunov
 */
interface ServerApi {
    fun sendPhone(phone: String): Completable
    fun sendConfirmationCode(phone: String, code: String): Single<TokenResponse>
    fun logout(token: String): Completable
}

class WrongConfirmationCode : Throwable()
class ServerError(message: String) : Throwable(message)

class TokenResponse(val token: String)