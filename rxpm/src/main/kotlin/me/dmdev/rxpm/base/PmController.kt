package me.dmdev.rxpm.base

import android.content.pm.PackageManager
import android.os.*
import androidx.core.content.ContextCompat
import com.bluelinelabs.conductor.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.delegate.*

/**
 * Predefined [Conductor's Controller][RestoreViewOnCreateController] implementing the [PmView][PmView].
 *
 * Just override the [providePresentationModel] and [onBindPresentationModel] methods and you are good to go.
 *
 * If extending is not possible you can implement [PmView],
 * create a [PmControllerDelegate] and pass the lifecycle callbacks to it.
 * See this class's source code for the example.
 */
abstract class PmController<PM : PresentationModel>(args: Bundle? = null) :
    RestoreViewOnCreateController(args),
    PmView<PM> {

    @Suppress("LeakingThis")
    private val delegate = PmControllerDelegate(this)

    final override val presentationModel get() = delegate.presentationModel

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        delegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, permission) == PackageManager.PERMISSION_GRANTED
    }
}