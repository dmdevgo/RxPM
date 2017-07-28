package me.dmdev.rxpm.sample

import android.os.Bundle
import me.dmdev.rxpm.base.PmSupportActivity
import timber.log.Timber

class MainActivity : PmSupportActivity<MainPm>() {

    companion object {
        init {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun providePresentationModel(): MainPm {
        Timber.i("providePresentationModel")
        return MainPm()
    }

    override fun onBindPresentationModel() {
        Timber.i("onBindPresentationModel")
    }

    override fun onUnbindPresentationModel() {
        Timber.i("onUnbindPresentationModel")
    }
}
