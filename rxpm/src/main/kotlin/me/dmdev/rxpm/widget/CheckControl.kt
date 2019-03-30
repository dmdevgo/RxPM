@file:Suppress("NOTHING_TO_INLINE")

package me.dmdev.rxpm.widget

import android.widget.CompoundButton
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import me.dmdev.rxpm.AndroidPmView
import me.dmdev.rxpm.PmView
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
class CheckControl internal constructor(initialChecked: Boolean) : PresentationModel() {

    /**
     * The checked [state][PresentationModel.State].
     */
    val checked = State(initialChecked)

    /**
     * The checked state change [events][PresentationModel.Action].
     */
    val checkedChanges = Action<Boolean>()

    override fun onCreate() {
        super.onCreate()
        checkedChanges.observable
            .filter { it != checked.value }
            .subscribe(checked.consumer)
            .untilDestroy()
    }
}

/**
 * Creates the [CheckControl].
 *
 * @param initialChecked initial checked state.
 */
fun PresentationModel.checkControl(initialChecked: Boolean = false): CheckControl {
    return CheckControl(initialChecked).apply {
        attachToParent(this@checkControl)
    }
}

/**
 * Binds the [CheckControl] to the [CompoundButton][compoundButton], use it ONLY in [PmView.onBindPresentationModel].
 *
 * @since 2.0
 */
inline infix fun CheckControl.bindTo(compoundButton: CompoundButton) {

    var editing = false

    checked.observable
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            editing = true
            compoundButton.isChecked = it
            editing = false
        }
        .untilUnbind()

    compoundButton.checkedChanges()
        .skipInitialValue()
        .filter { !editing }
        .subscribe(checkedChanges.consumer)
        .untilUnbind()
}