package me.dmdev.rxpm.sample

import android.app.Application

import timber.log.Timber

/**
 * @author Dmitriy Gorbunov
 */
class App : Application() {

    companion object {
        private lateinit var app: App
        val INSTANCE get() = app
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        initLogger()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
