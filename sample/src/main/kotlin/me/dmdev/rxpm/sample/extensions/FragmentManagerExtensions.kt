@file:Suppress("NOTHING_TO_INLINE")

package me.dmdev.rxpm.sample.extensions

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import me.dmdev.rxpm.sample.R

/**
 * @author Dmitriy Gorbunov
 */

inline fun FragmentManager.openScreen(fragment: Fragment, addToBackStack: Boolean = true) {
    beginTransaction()
            .replace(R.id.container, fragment, fragment.javaClass.name)
            .also { if (addToBackStack) it.addToBackStack(null) }
            .commit()
}

inline val FragmentManager.currentScreen: Fragment?
    get() = this.findFragmentById(R.id.container)

inline fun FragmentManager.back() {
    popBackStackImmediate()
}

inline fun <reified T > FragmentManager.findScreen(): T? {
    return findFragmentByTag(T::class.java.name) as? T
}
