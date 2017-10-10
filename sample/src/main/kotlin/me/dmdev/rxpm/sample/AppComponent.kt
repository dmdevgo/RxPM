package me.dmdev.rxpm.sample

import android.app.Application
import me.dmdev.rxpm.sample.api.ServerApi
import me.dmdev.rxpm.sample.api.ServerApiSimulator
import me.dmdev.rxpm.sample.model.AuthModel
import me.dmdev.rxpm.sample.model.TokenStorage
import me.dmdev.rxpm.sample.util.PhoneUtil
import me.dmdev.rxpm.sample.util.ResourceProvider

/**
 * @author Dmitriy Gorbunov
 */

class AppComponent(private val context: Application) {

    val resourceProvider by lazy { ResourceProvider(context) }
    val phoneUtil by lazy { PhoneUtil() }

    private val serverApi: ServerApi by lazy { ServerApiSimulator(context) }
    private val tokenStorage by lazy { TokenStorage() }

    val authModel by lazy { AuthModel(serverApi, tokenStorage) }

}