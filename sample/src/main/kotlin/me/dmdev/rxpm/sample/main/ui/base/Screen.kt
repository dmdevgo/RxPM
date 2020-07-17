package me.dmdev.rxpm.sample.main.ui.base

import android.os.*
import android.view.*
import androidx.appcompat.app.*
import io.reactivex.functions.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.extensions.*
import me.dmdev.rxpm.widget.*


abstract class Screen<PM : ScreenPresentationModel> : PmFragment<PM>(), BackHandler {

    abstract val screenLayout: Int

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(screenLayout, container, false)
    }

    override fun onBindPresentationModel(pm: PM) {
        pm.errorDialog bindTo { message, _ ->
            AlertDialog.Builder(context!!)
                .setMessage(message)
                .setPositiveButton(R.string.ok_button, null)
                .create()
        }
    }

    override fun handleBack(): Boolean {
        Unit passTo presentationModel.backAction
        return true
    }

    val progressConsumer = Consumer<Boolean> {
        if (it) {
            childFragmentManager.showDialog(ProgressDialog())
        } else {
            childFragmentManager
                .findScreen<ProgressDialog>()
                ?.dismiss()
        }
    }
}