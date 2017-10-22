package me.dmdev.rxpm.sample.extensions

fun String.onlyDigits() = this.replace("\\D".toRegex(), "")