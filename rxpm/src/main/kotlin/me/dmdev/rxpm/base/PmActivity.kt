package me.dmdev.rxpm.base

import android.content.pm.PackageManager
import android.os.*
import androidx.appcompat.app.*
import androidx.core.content.ContextCompat
import me.dmdev.rxpm.*
import me.dmdev.rxpm.delegate.*
import me.dmdev.rxpm.delegate.PmActivityDelegate.*

/**
 * Predefined [Activity][AppCompatActivity] implementing the [PmView][PmView].
 *
 * Just override the [providePresentationModel] and [onBindPresentationModel] methods and you are good to go.
 *
 * If extending is not possible you can implement [PmView],
 * create a [PmActivityDelegate] and pass the lifecycle callbacks to it.
 * See this class's source code for the example.
 */
abstract class PmActivity<PM : PresentationModel> : AppCompatActivity(), PmView<PM> {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) {
        PmActivityDelegate(this, RetainMode.CONFIGURATION_CHANGES)
    }

    final override val presentationModel get() = delegate.presentationModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delegate.onPostCreate()
    }

    override fun onStart() {
        super.onStart()
        delegate.onStart()
    }

    override fun onResume() {
        super.onResume()
        delegate.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        delegate.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        delegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onPause() {
        delegate.onPause()
        super.onPause()
    }

    override fun onStop() {
        delegate.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        delegate.onDestroy()
        super.onDestroy()
    }
}