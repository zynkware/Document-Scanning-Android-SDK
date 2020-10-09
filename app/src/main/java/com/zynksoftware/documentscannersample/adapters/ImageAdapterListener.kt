package com.zynksoftware.documentscannersample.adapters

import java.io.File

interface ImageAdapterListener {
    fun onSaveButtonClicked(image: File)
}