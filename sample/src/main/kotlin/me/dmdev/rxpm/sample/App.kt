package me.dmdev.rxpm.sample

import android.app.Application
import me.dmdev.rxpm.sample.dagger.AppComponent
import me.dmdev.rxpm.sample.dagger.AppModule
import me.dmdev.rxpm.sample.dagger.DaggerAppComponent

import timber.log.Timber

/**
 * @author Dmitriy Gorbunov
 */
class App : Application() {

    companion object {
        private lateinit var app: App
        val component: AppComponent by lazy {
            DaggerAppComponent.builder()
                    .appModule(AppModule(app))
                    .build()
        }
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
