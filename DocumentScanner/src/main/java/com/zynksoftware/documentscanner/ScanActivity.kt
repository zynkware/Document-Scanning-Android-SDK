package com.zynksoftware.documentscanner

import com.zynksoftware.documentscanner.ui.scan.InternalScanActivity

abstract class ScanActivity : InternalScanActivity() {

    fun addFragmentContentLayout() {
        addFragmentContentLayoutInternal()
    }
}