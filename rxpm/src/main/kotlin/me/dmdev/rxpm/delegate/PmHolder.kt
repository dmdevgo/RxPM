package me.dmdev.rxpm.delegate

import androidx.lifecycle.ViewModel
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.PresentationModel.Lifecycle

internal class PmHolder : ViewModel() {

    internal var pm: PresentationModel? = null

    override fun onCleared() {
        pm?.lifecycleConsumer?.accept(Lifecycle.DESTROYED)
        pm = null
    }
}