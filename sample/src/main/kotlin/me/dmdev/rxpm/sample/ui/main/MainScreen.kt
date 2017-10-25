package me.dmdev.rxpm.sample.ui.main

import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.screen_main.*
import me.dmdev.rxpm.sample.App
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.ui.base.Screen


class MainScreen : Screen<MainPm>() {

    override val screenLayout = R.layout.screen_main

    override fun providePresentationModel() = MainPm(App.component.authModel)

    override fun onBindPresentationModel(pm: MainPm) {
        super.onBindPresentationModel(pm)
        pm.inProgress.observable.bindTo(progressConsumer)
        logoutButton.clicks().bindTo(pm.logoutAction.consumer)
    }

}