package me.dmdev.rxpm.sample.main.ui.base

import android.app.*
import android.os.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import me.dmdev.rxpm.sample.*


class ProgressDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.ProgressDialogTheme).apply {
            setContentView(ProgressBar(context))
        }
    }
}