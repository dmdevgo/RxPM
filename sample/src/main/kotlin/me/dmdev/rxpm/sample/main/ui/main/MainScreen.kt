package me.dmdev.rxpm.sample.main.ui.main

import android.support.v7.app.AlertDialog
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.screen_main.*
import me.dmdev.rxpm.sample.App
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.ui.base.Screen
import me.dmdev.rxpm.sample.main.ui.main.MainPm.DialogResult.Cancel
import me.dmdev.rxpm.sample.main.ui.main.MainPm.DialogResult.Ok


class MainScreen : Screen<MainPm>() {

    override val screenLayout = R.layout.screen_main

    override fun providePresentationModel() = MainPm(App.component.authModel)

    override fun onBindPresentationModel(pm: MainPm) {
        super.onBindPresentationModel(pm)

        pm.logoutDialog bindTo { _, dc ->
            AlertDialog.Builder(context!!)
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("ok") { _, _ -> dc.sendResult(Ok) }
                .setNegativeButton("cancel") { _, _ -> dc.sendResult(Cancel) }
                .create()
        }

        pm.inProgress bindTo progressConsumer

        logoutButton.clicks() bindTo pm.logoutAction
    }

}