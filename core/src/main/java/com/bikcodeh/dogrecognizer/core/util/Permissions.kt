package com.bikcodeh.dogrecognizer.core.util

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import com.bikcodeh.dogrecognizer.core.R
import com.vmadalin.easypermissions.EasyPermissions

private const val PERMISSION_CAMERA_REQUEST_CODE = 2

object Permissions {

    fun hasCameraPermission(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    fun requestCameraPermission(fragment: Fragment) {
        fragment.context?.getString(R.string.permission_required_description)?.let {
            EasyPermissions.requestPermissions(
                fragment,
                it,
                PERMISSION_CAMERA_REQUEST_CODE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}