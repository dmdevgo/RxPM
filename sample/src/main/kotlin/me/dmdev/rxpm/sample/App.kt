package me.dmdev.rxpm.sample

import android.app.Application
import me.dmdev.rxpm.sample.main.MainComponent
import timber.log.Timber


class App : Application() {

    companion object {
        lateinit var component: MainComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()
        component = MainComponent(this)
        initLogger()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
