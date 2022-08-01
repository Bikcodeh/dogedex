package com.bikcodeh.dogrecognizer.presentation.util

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.presentation.util.Constants.PERMISSION_CAMERA_REQUEST_CODE
import com.vmadalin.easypermissions.EasyPermissions

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