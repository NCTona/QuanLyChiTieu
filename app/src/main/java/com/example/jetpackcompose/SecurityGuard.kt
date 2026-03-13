package com.example.jetpackcompose.security

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Debug
import java.security.MessageDigest

object SecurityGuard {

    // SHA-256 fingerprint của keystore release
    private const val EXPECTED_SHA256 =
        "D6:1C:7E:B9:2E:EE:28:B8:2E:E4:DC:7B:74:16:78:55:" +
                "D2:F9:E4:63:03:DF:1B:16:72:CD:36:7A:C8:EB:F6:5A"

    private const val EXPECTED_PACKAGE = "com.example.jetpackcompose"

    fun isAppSecure(context: Context): Boolean {
        return checkSignature(context) &&
                checkPackageName(context)
                !isDebuggerAttached() &&
                !isDebuggable(context)
    }

    // Check chữ ký APK (anti re-sign)
    @SuppressLint("NewApi")
    private fun checkSignature(context: Context): Boolean {
        val pkgInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_SIGNING_CERTIFICATES
        )

        val signatures = pkgInfo.signingInfo?.apkContentsSigners
        val certBytes = signatures?.get(0)?.toByteArray()

        val sha256 = MessageDigest.getInstance("SHA-256")
            .digest(certBytes)
            .joinToString(":") { "%02X".format(it) }

        return sha256 == EXPECTED_SHA256
    }

    // Check package name (anti clone)
    private fun checkPackageName(context: Context): Boolean =
        context.packageName == EXPECTED_PACKAGE

    // Anti-debug
    private fun isDebuggerAttached(): Boolean =
        Debug.isDebuggerConnected() || Debug.waitingForDebugger()

    // Chặn build debuggable
    private fun isDebuggable(context: Context): Boolean =
        (context.applicationInfo.flags and
                ApplicationInfo.FLAG_DEBUGGABLE) != 0
}
