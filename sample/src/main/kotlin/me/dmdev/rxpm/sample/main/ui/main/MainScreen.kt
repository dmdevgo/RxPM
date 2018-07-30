package me.dmdev.rxpm.sample.main.ui.main

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import kotlinx.android.synthetic.main.screen_main.*
import me.dmdev.rxpm.sample.App
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.ui.base.Screen
import me.dmdev.rxpm.sample.main.ui.main.MainPm.DialogResult.Cancel
import me.dmdev.rxpm.sample.main.ui.main.MainPm.DialogResult.Ok


class MainScreen : Screen<MainPm>() {

    override val screenLayout = R.layout.screen_main

    override fun providePresentationModel() = MainPm(App.component.authModel)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.inflateMenu(R.menu.main)
    }

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

        toolbar.itemClicks()
            .filter { it.itemId == R.id.logoutAction }
            .map { Unit }
            .bindTo(pm.logoutAction)
    }

}