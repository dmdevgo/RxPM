package me.dmdev.rxpm.sample.main.extensions

import android.content.*
import android.view.*
import android.view.inputmethod.*
import androidx.annotation.*


fun View.visible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun ViewGroup.inflate(@LayoutRes layoutId: Int): View {
    return LayoutInflater.from(this.context).inflate(layoutId, this, false)
}

fun View.showKeyboard() {
    val function = {
        if (requestFocus()) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, 0)
        }
    }

    function.invoke()
    post {
        function.invoke()
    }
}

fun View.hideKeyboard() {
    val function = {
        clearFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    function.invoke()
    post {
        function.invoke()
    }
}