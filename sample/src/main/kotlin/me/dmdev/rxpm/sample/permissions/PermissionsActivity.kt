package me.dmdev.rxpm.sample.permissions

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.activity_permissions.*
import kotlinx.android.synthetic.main.item_permission.view.*
import me.dmdev.rxpm.Permission.RationaleAction
import me.dmdev.rxpm.base.PmActivity
import me.dmdev.rxpm.bindTo
import me.dmdev.rxpm.permission.PermissionResults
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.widget.DialogControl
import me.dmdev.rxpm.widget.bindTo

class PermissionsActivity : PmActivity<PermissionsPm>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)
    }

    override fun providePresentationModel() = PermissionsPm()

    override fun onBindPresentationModel(pm: PermissionsPm) {
        locationButton.clicks() bindTo pm.locationAction
        cameraButton.clicks() bindTo pm.cameraAction

        pm.locationGranted bindTo { onPermissionsGranted(R.string.location_permissions_granted) }
        pm.cameraGranted bindTo { onPermissionsGranted(R.string.camera_permissions_granted) }

        pm.locationPermission.permanentlyDenied bindTo this::onPermissionsPermanentlyDenied
        pm.cameraPermissions.permanentlyDenied bindTo this::onPermissionsPermanentlyDenied

        pm.cameraPermissions.rationale bindTo this::onPermissionsRationale
    }

    private fun onPermissionsGranted(@StringRes messageRes: Int) {
        val view = findViewById<View>(android.R.id.content)
        Snackbar.make(view, messageRes, Snackbar.LENGTH_SHORT).show()
    }

    private fun onPermissionsPermanentlyDenied(results: PermissionResults) {
        val view = findViewById<View>(android.R.id.content)
        val permissions = results.map { it.permission.permissionName }.toSet()
        val message = resources.getQuantityString(R.plurals.permission_permanently_denied,
                permissions.size,
                permissions.joinToString())
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.settings_button) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        snackbar.show()
    }

    private fun onPermissionsRationale(
        results: PermissionResults,
        dc: DialogControl<PermissionResults, RationaleAction>
    ): AlertDialog {
        val linearLayout = layoutInflater.inflate(R.layout.layout_linear_vertical, null, false) as ViewGroup
        results.distinctBy { it.permission.permissionName }.forEach { result ->
            val view = layoutInflater.inflate(R.layout.item_permission, linearLayout, false)
            result.permission.permissionIcon?.let { view.imageView.setImageResource(it) }
            view.textView.text = result.permission.permissionName
            linearLayout.addView(view)
        }
        return AlertDialog.Builder(this)
            .setTitle(R.string.permissions_rationale_title)
            .setView(linearLayout)
            .setPositiveButton(R.string.allow_button) {  _, _ -> dc.sendResult(RationaleAction.ALLOW) }
            .setNegativeButton(R.string.cancel_button) { _, _ -> dc.sendResult(RationaleAction.CANCEL) }
            .create()
    }

    private val String.permissionName: String
        get() = when (this) {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION -> getString(R.string.permission_location)
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> getString(R.string.permission_storage)
            Manifest.permission.CAMERA -> getString(R.string.permission_camera)
            else -> this
        }

    private val String.permissionIcon: Int?
        get() = when (this) {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION -> R.drawable.ic_location_on_black_24dp
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> R.drawable.ic_folder_black_24dp
            Manifest.permission.CAMERA -> R.drawable.ic_photo_camera_black_24dp
            else -> null
        }

}