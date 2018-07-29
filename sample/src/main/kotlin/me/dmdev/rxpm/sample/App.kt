package me.dmdev.rxpm.sample

import android.app.Application
import me.dmdev.rxpm.sample.main.AppComponent
import timber.log.Timber


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
