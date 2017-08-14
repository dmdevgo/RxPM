package me.dmdev.rxpm.widget

import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.enabled
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PresentationModel

/**
 * @author Dmitriy Gorbunov
 */
class ClickControl internal constructor(pm: PresentationModel,
                                        initialEnabled: Boolean = true) {
    val enabled = pm.State(initialEnabled)
    val clicks = pm.Action<Unit>()
}

fun PresentationModel.clickControl(initialEnabled: Boolean = true): ClickControl {
    return me.dmdev.rxpm.widget.ClickControl(this, initialEnabled)
}

@Suppress("NOTHING_TO_INLINE")
inline fun View.bind(clickControl: ClickControl): Disposable {
    return CompositeDisposable().apply {
        addAll(
                clickControl.enabled.observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(enabled()),
                clicks().subscribe(clickControl.clicks.consumer)
        )
    }
}