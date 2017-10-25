package me.dmdev.rxpm.sample.util

import android.content.Context
import android.support.annotation.StringRes


class ResourceProvider(private val context: Context) {

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.resources.getString(resId, *formatArgs)
    }
}
