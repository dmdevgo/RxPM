package me.dmdev.demo

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.widget.dialogControl

class MainPm : PresentationModel() {

    val dialogControl = dialogControl<String, DialogResult>()

    val toastMessages = Command<String>()

    val showAlertClicks = Action<Unit>()

    override fun onCreate() {
        super.onCreate()

        showAlertClicks.observable
                .switchMapMaybe {
                    dialogControl.show("Select an action")
                            .doOnSuccess {
                                toastMessages.consumer.accept(
                                        when (it) {
                                            is DialogResult.Yes -> "Yes"
                                            is DialogResult.No -> "No"
                                            is DialogResult.Close -> "Close"
                                            is DialogResult.Cancel -> "Cancel"
                                            is DialogResult.SelectedItem -> "Selected item is ${it.index + 1}"
                                        }
                                )
                            }
                }
                .subscribe()
                .untilDestroy()
    }

    sealed class DialogResult {
        object Yes : DialogResult()
        object No : DialogResult()
        object Close : DialogResult()
        object Cancel : DialogResult()
        class SelectedItem(val index: Int) : DialogResult()
    }
}