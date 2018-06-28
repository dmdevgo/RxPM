package me.dmdev.rxpm.widget

import android.widget.CompoundButton
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PresentationModel

/**
 * Helps to bind a group of properties of a checkable widget to a [presentation model][PresentationModel]
 * and also breaks the loop of two-way data binding to make the work with the check easier.
 *
 * You can bind this to any [CompoundButton] subclass using the familiar `bindTo` method
 * in the [AndroidPmView].
 *
 * Instantiate this using the [checkControl] extension function of the presentation model.
 *
 * @see InputControl
 * @see DialogControl
 */
class CheckControl internal constructor(pm: PresentationModel, initialChecked: Boolean) {

    /**
     * The checked [state][PresentationModel.State].
     */
    val checked = pm.State(initialChecked)

    /**
     * The checked state change [events][PresentationModel.Action].
     */
    val checkedChanges = pm.Action<Boolean>()

    init {
        checkedChanges.relay
                .filter { it != checked.value }
                .subscribe(checked.relay)
    }
}

/**
 * Creates the [CheckControl].
 *
 * @param initialChecked initial checked state.
 */
fun PresentationModel.checkControl(initialChecked: Boolean = false): CheckControl {
    return CheckControl(this, initialChecked)
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun CompoundButton.bind(checkControl: CheckControl, compositeDisposable: CompositeDisposable) {

    var editing = false

    compositeDisposable.addAll(
            checkControl.checked.observable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        editing = true
                        isChecked = it
                        editing = false
                    },

            checkedChanges()
                    .skipInitialValue()
                    .filter { !editing }
                    .subscribe(checkControl.checkedChanges.consumer)
    )
}