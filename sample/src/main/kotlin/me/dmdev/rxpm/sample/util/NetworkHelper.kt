package me.dmdev.rxpm.sample.util

import android.content.Context
import android.net.ConnectivityManager

/**
 * @author Dmitriy Gorbunov
 */
class NetworkHelper(context: Context) {

    private val connectivityManager =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

    fun isOnline() = connectivityManager.activeNetworkInfo?.isConnected ?: false
    fun isOffline() = !isOnline()

}