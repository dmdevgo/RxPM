package me.dmdev.rxpm.sample.extensions

/**
 * @author Dmitriy Gorbunov
 */

fun String.onlyDigits() = this.replace("\\D".toRegex(), "")