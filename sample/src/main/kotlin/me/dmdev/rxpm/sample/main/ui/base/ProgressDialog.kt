package me.dmdev.rxpm.sample.main.ui.base

import android.app.Dialog
import android.os.Bundle
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import me.dmdev.rxpm.sample.R


class ProgressDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(context!!, R.style.ProgressDialogTheme).apply {
            setContentView(ProgressBar(context))
        }
    }
}