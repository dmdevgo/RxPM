package me.dmdev.rxpm.sample.permissions

import android.Manifest
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.action
import me.dmdev.rxpm.command
import me.dmdev.rxpm.permission

class PermissionsPm : PresentationModel() {

    val locationPermission = permission(Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        requestCode = 1001)

    val cameraPermissions = permission(Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        requestCode = 1002,
        useRationaleDialog = true)

    val locationAction = action<Unit>()

    val cameraAction = action<Unit>()

    val locationGranted = command<Unit>()

    val cameraGranted = command<Unit>()

    override fun onCreate() {
        super.onCreate()

        locationAction.observable
            .switchMapSingle { locationPermission.request() }
            .filter { it }
            .subscribe { locationGranted.consumer.accept(Unit) }
            .untilDestroy()

        cameraAction.observable
            .switchMapSingle { cameraPermissions.request() }
            .filter { it }
            .subscribe { cameraGranted.consumer.accept(Unit) }
            .untilDestroy()
    }

}