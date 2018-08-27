package me.dmdev.rxpm.sample.main

import android.app.Application
import me.dmdev.rxpm.sample.main.api.ServerApi
import me.dmdev.rxpm.sample.main.api.ServerApiSimulator
import me.dmdev.rxpm.sample.main.model.AuthModel
import me.dmdev.rxpm.sample.main.model.TokenStorage
import me.dmdev.rxpm.sample.main.util.PhoneUtil
import me.dmdev.rxpm.sample.main.util.ResourceProvider

class MainComponent(private val context: Application) {

    val resourceProvider by lazy { ResourceProvider(context) }
    val phoneUtil by lazy { PhoneUtil() }

    private val serverApi: ServerApi by lazy { ServerApiSimulator(context) }
    private val tokenStorage by lazy { TokenStorage() }

    val authModel by lazy { AuthModel(serverApi, tokenStorage) }

}