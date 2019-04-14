package me.dmdev.rxpm.delegate

import me.dmdev.rxpm.*

internal object PmStore {

    private val pmMap = mutableMapOf<String, PresentationModel>()

    fun getPm(key: String, pmProvider: () -> PresentationModel): PresentationModel {
        return pmMap[key] ?: pmProvider().also { pmMap[key] = it }
    }

    fun removePm(key: String): PresentationModel? {
        return pmMap.remove(key)
    }
}