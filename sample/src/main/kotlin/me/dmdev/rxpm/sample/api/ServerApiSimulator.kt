package me.dmdev.rxpm.sample.api

import android.app.NotificationManager
import android.content.Context
import android.support.v7.app.NotificationCompat
import io.reactivex.Completable
import io.reactivex.Single
import me.dmdev.rxpm.sample.R
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author Dmitriy Gorbunov
 */
class ServerApiSimulator(private val context: Context) : ServerApi {

    companion object {
        private const val DELAY_IN_SECONDS = 3L
    }

    private val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var phone: String? = null
    private var code: String? = null
    private val random = Random(System.currentTimeMillis())

    override fun sendPhone(phone: String): Completable {

        return Completable.complete()
                .delay(DELAY_IN_SECONDS, TimeUnit.SECONDS)
                .doOnComplete {
                    maybeServerError()
                    this.phone = phone
                    code = generateRandomCode().toString()
                    showNotification(code!!)
                }
    }

    override fun sendConfirmationCode(phone: String, code: String): Single<TokenResponse> {

        return Single.just(TokenResponse(UUID.randomUUID().toString()))
                .delay(DELAY_IN_SECONDS, TimeUnit.SECONDS)
                .doOnSuccess {
                    if (this.code != code) {
                        throw WrongConfirmationCode()
                    } else {
                        maybeServerError()
                    }
                }
    }

    override fun logout(token: String): Completable {
        return Completable.complete()
                .delay(DELAY_IN_SECONDS, TimeUnit.SECONDS)
                .doOnComplete {
                    maybeServerError()
                    phone = null
                    code = null
                }
    }

    private fun maybeServerError() {
        if (random.nextInt(100) >= 70) {
            throw ServerError("Service is unavailable. Please try again.")
        }
    }

    private fun generateRandomCode(): Int {
        var c = random.nextInt(10_000)
        if (c < 1000) {
            c = generateRandomCode()
        }
        return c
    }

    private fun showNotification(code: String) {

        notificationManager
                .notify(1111,
                        NotificationCompat.Builder(context)
                                .setContentTitle("RxPM Sample")
                                .setContentText("Confirmation code $code")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setDefaults(NotificationCompat.DEFAULT_SOUND
                                                     or NotificationCompat.DEFAULT_LIGHTS)
                                .build()
                )
    }
}

