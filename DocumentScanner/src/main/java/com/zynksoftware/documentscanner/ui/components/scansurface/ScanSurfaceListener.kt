package com.zynksoftware.documentscanner.ui.components.scansurface

import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel

internal interface ScanSurfaceListener {
    fun scanSurfacePictureTaken()
    fun scanSurfaceShowProgress()
    fun scanSurfaceHideProgress()
    fun onError(error: DocumentScannerErrorModel)

    fun showFlash()
    fun hideFlash()
    fun showFlashModeOn()
    fun showFlashModeOff()
}