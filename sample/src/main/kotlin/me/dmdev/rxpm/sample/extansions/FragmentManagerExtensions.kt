package me.dmdev.rxpm.sample.extansions

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import me.dmdev.rxpm.sample.R

/**
 * @author Dmitriy Gorbunov
 */

fun FragmentManager.openScreen(fragment: Fragment, addToBackStack: Boolean = true) {
    beginTransaction()
            .replace(R.id.container, fragment)
            .also { if (addToBackStack) it.addToBackStack(null) }
            .commit()
}