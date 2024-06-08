package com.juanmaGutierrez.carcare.service

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

/**
 * Service class for camera-related functionalities.
 */
class CameraService {

    /**
     * A companion object containing static properties related to image URI and required permissions.
     */
    companion object {
        var image_uri: Uri? = null
        val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.CAMERA).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    /**
     * Checks if all required permissions for camera are granted.
     *
     * @param activity The activity context.
     * @return True if all permissions are granted, false otherwise.
     */
    fun allPermissionGranted(activity: Activity) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(activity.baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Starts the camera to capture an image.
     *
     * @param activity The activity context.
     * @param cameraARL The ActivityResultLauncher to launch the camera intent.
     */
    fun startCamera(activity: Activity, cameraARL: ActivityResultLauncher<Intent>) {
        val values = ContentValues()
        image_uri = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        cameraARL.launch(cameraIntent)
    }
}