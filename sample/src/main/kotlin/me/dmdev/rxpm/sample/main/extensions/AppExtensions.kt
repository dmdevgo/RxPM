package me.dmdev.rxpm.sample.main.extensions

fun String.onlyDigits() = this.replace("\\D".toRegex(), "")