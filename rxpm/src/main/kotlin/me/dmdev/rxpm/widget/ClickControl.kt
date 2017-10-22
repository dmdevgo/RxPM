package me.dmdev.rxpm.widget

import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.enabled
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PresentationModel

/**
 * Helps to bind a group of properties of any clickable widget to a [presentation model][PresentationModel].
 *
 * You can bind this to any [View] subclass using the familiar `bindTo` method
 * in the [AndroidPmView].
 *
 * Instantiate this using the [clickControl] extension function of the presentation model.
 *
 * @see InputControl
 * @see CheckControl
 */
class ClickControl internal constructor(pm: PresentationModel,
                                        initialEnabled: Boolean) {

    /**
     * The widget enabled [state][PresentationModel.State].
     */
    val enabled = pm.State(initialEnabled)

    /**
     * The clicks [events][PresentationModel.Action].
     */
    val clicks = pm.Action<Unit>()
}

/**
 * Creates the [ClickControl].
 *
 * @param initialEnabled is clickable widget initially enabled.
 */
fun PresentationModel.clickControl(initialEnabled: Boolean = true): ClickControl {
    return me.dmdev.rxpm.widget.ClickControl(this, initialEnabled)
}

@Suppress("NOTHING_TO_INLINE")
inline internal fun View.bind(clickControl: ClickControl): Disposable {
    return CompositeDisposable().apply {
        addAll(
                clickControl.enabled.observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(enabled()),
                clicks().subscribe(clickControl.clicks.consumer)
        )
    }
}