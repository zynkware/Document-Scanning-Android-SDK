/**
    Copyright 2020 ZynkSoftware SRL

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
    associated documentation files (the "Software"), to deal in the Software without restriction,
    including without limitation the rights to use, copy, modify, merge, publish, distribute,
    sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or
    substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
    INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
    DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
