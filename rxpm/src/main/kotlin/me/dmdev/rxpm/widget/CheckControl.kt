package me.dmdev.rxpm.widget

import android.widget.CompoundButton
import com.jakewharton.rxbinding2.view.enabled
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PresentationModel

/**
 * @author Dmitriy Gorbunov
 */
class CheckControl internal constructor(pm: PresentationModel,
                                        initialChecked: Boolean,
                                        initialEnabled: Boolean) {

    val checked = pm.State(initialChecked)
    val enabled = pm.State(initialEnabled)
    val checkedChanges = pm.Action<Boolean>()

    init {
        checkedChanges.relay
                .filter { it != checked.value }
                .subscribe(checked.relay)
    }
}

fun PresentationModel.checkControl(initialChecked: Boolean = false,
                                   initialEnabled: Boolean = true): CheckControl {
    return CheckControl(this, initialChecked, initialEnabled)
}

@Suppress("NOTHING_TO_INLINE")
inline fun CompoundButton.bind(checkControl: CheckControl): Disposable {
    return CompositeDisposable().apply {
        var editing = false
        addAll(
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
                        .subscribe(checkControl.checkedChanges.consumer),

                checkControl.enabled.observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(enabled())
        )
    }
}