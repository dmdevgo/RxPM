package me.dmdev.rxpm.sample.main.ui.main

import android.os.*
import android.view.*
import androidx.appcompat.app.*
import com.jakewharton.rxbinding3.appcompat.*
import kotlinx.android.synthetic.main.screen_main.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.ui.base.*
import me.dmdev.rxpm.sample.main.ui.main.MainPm.DialogResult.*
import me.dmdev.rxpm.widget.*


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
            .bindTo(pm.logoutClicks)
    }

}