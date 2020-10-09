package com.zynksoftware.documentscanner.manager

import android.content.Context
import android.graphics.Bitmap
import id.zelory.compressor.extension

internal class SessionManager(context: Context) {

    companion object {
        private const val IMAGE_SIZE_KEY = "IMAGE_SIZE_KEY"
        private const val IMAGE_QUALITY_KEY = "IMAGE_QUALITY_KEY"
        private const val IMAGE_TYPE_KEY = "IMAGE_TYPE_KEY"

        private const val DEFAULT_IMAGE_TYPE = "jpg"
    }
    private val preferences = context.getSharedPreferences("ZDC_Shared_Preferences", Context.MODE_PRIVATE)


    fun getImageSize(): Long {
        return preferences.getLong(IMAGE_SIZE_KEY, -1L)
    }

    fun setImageSize(size: Long) {
        preferences.edit().putLong(IMAGE_SIZE_KEY, size).apply()
    }

    fun getImageQuality(): Int {
        return preferences.getInt(IMAGE_QUALITY_KEY, 100)
    }

    fun setImageQuality(quality: Int) {
        preferences.edit().putInt(IMAGE_QUALITY_KEY, quality).apply()
    }

    fun getImageType(): Bitmap.CompressFormat {
        return compressFormat(preferences.getString(IMAGE_TYPE_KEY, DEFAULT_IMAGE_TYPE)!!)
    }

    fun setImageType(type: Bitmap.CompressFormat) {
        preferences.edit().putString(IMAGE_TYPE_KEY, type.extension()).apply()
    }

    private fun compressFormat(format: String) = when (format.toLowerCase()) {
        "png" -> Bitmap.CompressFormat.PNG
        "webp" -> Bitmap.CompressFormat.WEBP
        else -> Bitmap.CompressFormat.JPEG
    }
}
