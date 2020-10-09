package com.zynksoftware.documentscanner.ui

import android.content.Context
import android.graphics.Bitmap
import com.zynksoftware.documentscanner.manager.SessionManager

object DocumentScanner {

    fun init(context: Context, configuration: Configuration = Configuration()) {
        System.loadLibrary("opencv_java4")
        val sessionManager = SessionManager(context)
        sessionManager.setImageQuality(configuration.imageQuality)
        sessionManager.setImageSize(configuration.imageSize)
        sessionManager.setImageType(configuration.imageType)
    }


    data class Configuration(
        /// TODO: 21/09/2020  add validation or min/max range
        var imageQuality: Int = 100,
        var imageSize: Long = -1,
        var imageType: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ){
    }
}