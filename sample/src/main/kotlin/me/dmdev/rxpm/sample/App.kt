package me.dmdev.rxpm.sample

import android.app.Application

import timber.log.Timber

/**
 * @author Dmitriy Gorbunov
 */
class App : Application() {

    companion object {
        lateinit var component: AppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()
        component = AppComponent(this)
        initLogger()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
