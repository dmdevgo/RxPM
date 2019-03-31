package me.dmdev.rxpm.widget

import android.widget.*
import com.jakewharton.rxbinding2.widget.*
import me.dmdev.rxpm.*

/**
 * Helps to bind a group of properties of a checkable widget to a [presentation model][PresentationModel]
 * and also breaks the loop of two-way data binding to make the work with the check easier.
 *
 * You can bind this to any [CompoundButton] subclass using the [bindTo][bindTo] extension.
 *
 * Instantiate this using the [checkControl] extension function of the presentation model.
 *
 * @see InputControl
 * @see DialogControl
 */
class CheckControl internal constructor(initialChecked: Boolean) : PresentationModel() {

    /**
     * The checked [state][State].
     */
    val checked = state(initialChecked)

    /**
     * The checked state change [events][Action].
     */
    val checkedChanges = action<Boolean>()

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
infix fun CheckControl.bindTo(compoundButton: CompoundButton) {

    var editing = false

    checked bindTo {
        editing = true
        compoundButton.isChecked = it
        editing = false
    }

    compoundButton.checkedChanges()
        .skipInitialValue()
        .filter { !editing }
        .bindTo(checkedChanges)
}