package me.dmdev.demo

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_main.*
import me.dmdev.demo.MainPm.DialogResult.*
import me.dmdev.rxpm.base.PmSupportActivity

class MainActivity : PmSupportActivity<MainPm>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun providePresentationModel() = MainPm()

    override fun onBindPresentationModel(pm: MainPm) {

        pm.toastMessages.observable bindTo {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        pm.dialogControl.bindTo { data, dc ->
            AlertDialog.Builder(this)
                    .setTitle(data)
                    .setPositiveButton("yes", { _, _ -> dc.sendResult(Yes) })
                    .setNegativeButton("no", { _, _ -> dc.sendResult(No) })
                    .setNeutralButton("close", { _, _ -> dc.sendResult(Close) })
                    .setOnCancelListener { dc.sendResult(Cancel) }
                    .setItems(arrayOf("One", "Two", "Three"), { _, which ->
                        dc.sendResult(SelectedItem(which))
                    })
                    .create()
        }

        showAlertButton.clicks() bindTo pm.showAlertClicks.consumer

    }
}

