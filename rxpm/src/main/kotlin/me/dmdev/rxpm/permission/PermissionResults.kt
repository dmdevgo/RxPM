package me.dmdev.rxpm.permission

// TODO: comments
data class PermissionResult(val permission: String, val type: PermissionResultType)

enum class PermissionResultType {
    UNKNOWN,
    GRANTED,
    RATIONALE_REQUIRED,
    DECLINED,
    PERMANENTLY_DECLINED
}

typealias PermissionResults = List<PermissionResult>

inline val PermissionResults.areGranted: Boolean
    get() = all { it.isGranted }

inline val PermissionResult.isGranted: Boolean
    get() = type == PermissionResultType.GRANTED

inline val PermissionResult.isRationaleRequired: Boolean
    get() = type == PermissionResultType.RATIONALE_REQUIRED

inline val PermissionResult.isPermanentlyDeclined: Boolean
    get() = type == PermissionResultType.PERMANENTLY_DECLINED

inline val PermissionResult.isDeclined: Boolean
    get() = type == PermissionResultType.DECLINED
