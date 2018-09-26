package me.dmdev.rxpm.delegate

import androidx.lifecycle.ViewModel
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle

internal class PmHolder(val pm: PresentationModel) : ViewModel() {

    init {
        pm.lifecycleConsumer.accept(Lifecycle.CREATED)
    }

    override fun onCleared() {
        pm.lifecycleConsumer.accept(Lifecycle.DESTROYED)
    }
}