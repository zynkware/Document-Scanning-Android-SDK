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


package com.zynksoftware.documentscanner.ui.imageprocessing

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zynksoftware.documentscanner.R
import com.zynksoftware.documentscanner.common.extensions.rotateBitmap
import com.zynksoftware.documentscanner.ui.base.BaseFragment
import com.zynksoftware.documentscanner.ui.scan.InternalScanActivity
import kotlinx.android.synthetic.main.fragment_image_processing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class ImageProcessingFragment : BaseFragment() {

    companion object {
        private val TAG = ImageProcessingFragment::class.simpleName
        private const val ANGLE_OF_ROTATION = 90

        fun newInstance(): ImageProcessingFragment {
            return ImageProcessingFragment()
        }
    }

    private var isInverted = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_processing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imagePreview.setImageBitmap(getScanActivity().croppedImage)

        initListeners()
    }

    private fun initListeners() {
        closeButton.setOnClickListener {
            closeFragment()
        }
        confirmButton.setOnClickListener {
            selectFinalScannerResults()
        }
        magicButton.setOnClickListener {
            applyGrayScaleFilter()
        }
        rotateButton.setOnClickListener {
            rotateImage()
        }
    }

    private fun getScanActivity(): InternalScanActivity {
        return (requireActivity() as InternalScanActivity)
    }

    private fun rotateImage() {
        Log.d(TAG, "ZDCrotate starts ${System.currentTimeMillis()}")
        showProgressBar()
        GlobalScope.launch(Dispatchers.IO) {
            if(isAdded) {
                getScanActivity().transformedImage = getScanActivity().transformedImage?.rotateBitmap(ANGLE_OF_ROTATION)
                getScanActivity().croppedImage = getScanActivity().croppedImage?.rotateBitmap(ANGLE_OF_ROTATION)
            }

            if(isAdded) {
                getScanActivity().runOnUiThread {
                    hideProgressBar()
                    if (isInverted) {
                        imagePreview?.setImageBitmap(getScanActivity().transformedImage)
                    } else {
                        imagePreview?.setImageBitmap(getScanActivity().croppedImage)
                    }
                }
            }
            Log.d(TAG, "ZDCrotate ends ${System.currentTimeMillis()}")
        }
    }

    private fun closeFragment() {
        getScanActivity().closeCurrentFragment()
    }

    private fun applyGrayScaleFilter() {
        Log.d(TAG, "ZDCgrayscale starts ${System.currentTimeMillis()}")
        showProgressBar()
        GlobalScope.launch(Dispatchers.IO) {
            if(isAdded) {
                if (!isInverted) {
                    val bmpMonochrome = Bitmap.createBitmap(getScanActivity().croppedImage!!.width, getScanActivity().croppedImage!!.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bmpMonochrome)
                    val ma = ColorMatrix()
                    ma.setSaturation(0f)
                    val paint = Paint()
                    paint.colorFilter = ColorMatrixColorFilter(ma)
                    getScanActivity().croppedImage?.let { canvas.drawBitmap(it, 0f, 0f, paint) }
                    getScanActivity().transformedImage = bmpMonochrome.copy(bmpMonochrome.config, true)
                    getScanActivity().runOnUiThread {
                        hideProgressBar()
                        imagePreview.setImageBitmap(getScanActivity().transformedImage)
                    }
                } else {
                    getScanActivity().runOnUiThread {
                        hideProgressBar()
                        imagePreview.setImageBitmap(getScanActivity().croppedImage)
                    }
                }
                isInverted = !isInverted
                Log.d(TAG, "ZDCgrayscale ends ${System.currentTimeMillis()}")
            }
        }
    }

    private fun selectFinalScannerResults() {
        getScanActivity().finalScannerResult()
    }
}