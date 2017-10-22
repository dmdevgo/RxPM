package me.dmdev.rxpm.sample

import android.annotation.SuppressLint
import android.app.Application

import timber.log.Timber


class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
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
