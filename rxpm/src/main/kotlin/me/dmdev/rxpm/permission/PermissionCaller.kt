package me.dmdev.rxpm.permission

// TODO: comments
interface PermissionCaller {
    fun requestPermissions(permissions: Array<out String>, requestCode: Int)
    fun shouldShowRequestPermissionRationale(permission: String): Boolean
    fun isPermissionGranted(permission: String): Boolean
}