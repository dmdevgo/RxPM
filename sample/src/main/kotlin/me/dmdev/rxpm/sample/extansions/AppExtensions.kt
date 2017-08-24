package me.dmdev.rxpm.sample.extansions

/**
 * @author Dmitriy Gorbunov
 */

fun String.onlyDigits() = this.replace("\\D".toRegex(), "")