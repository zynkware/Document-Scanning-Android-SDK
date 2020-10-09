package com.zynksoftware.documentscannersample

import android.app.Application
import android.graphics.Bitmap
import com.zynksoftware.documentscanner.ui.DocumentScanner

class BaseApp : Application() {
    companion object {
        private const val FILE_SIZE = 1000000L
        private const val FILE_QUALITY = 100
        private val FILE_TYPE = Bitmap.CompressFormat.JPEG
    }

    override fun onCreate() {
        super.onCreate()

        val configuration = DocumentScanner.Configuration()
        configuration.imageQuality = FILE_QUALITY
        configuration.imageType = FILE_TYPE
        DocumentScanner.init(this, configuration)
    }
}