package me.dmdev.rxpm.sample.recyclerview

interface Identifiable<out T: Any> {
    val id: T
}