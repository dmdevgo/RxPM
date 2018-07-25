package me.dmdev.rxpm.sample.ui.base

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import me.dmdev.rxpm.sample.R



class MessageDialog : DialogFragment() {

    companion object {
        private const val ARG_MESSAGE = "message"
        fun newInstance(message: String) = MessageDialog().apply {
            arguments = Bundle().apply {
                putString(ARG_MESSAGE, message)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
                .setMessage(arguments?.getString(ARG_MESSAGE))
                .setPositiveButton(R.string.ok_button, null)
                .create()
    }

}
