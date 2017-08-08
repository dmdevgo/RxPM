package me.dmdev.rxpm.widget

import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.enabled
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PresentationModel.Action
import me.dmdev.rxpm.PresentationModel.State

/**
 * @author Dmitriy Gorbunov
 */
class ClickControl(initialEnabled: Boolean = true) {
    val enabled = State(initialEnabled)
    val clicks = Action<Unit>()
}

@Suppress("NOTHING_TO_INLINE")
inline fun View.bind(clickControl: ClickControl): Disposable {
    return CompositeDisposable().apply {
        addAll(
                clickControl.enabled.observable.subscribe(enabled()),
                clicks().subscribe(clickControl.clicks.consumer)
        )
    }
}