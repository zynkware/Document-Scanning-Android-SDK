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


package com.zynksoftware.documentscanner.ui

import android.content.Context
import android.graphics.Bitmap
import com.zynksoftware.documentscanner.manager.SessionManager

object DocumentScanner {

    fun init(context: Context, configuration: Configuration = Configuration()) {
        System.loadLibrary("opencv_java4")
        val sessionManager = SessionManager(context)
        if(configuration.imageQuality in 1..100) {
            sessionManager.setImageQuality(configuration.imageQuality)
        }
        sessionManager.setImageSize(configuration.imageSize)
        sessionManager.setImageType(configuration.imageType)
    }


    data class Configuration(
        var imageQuality: Int = 100,
        var imageSize: Long = -1,
        var imageType: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ){
    }
}