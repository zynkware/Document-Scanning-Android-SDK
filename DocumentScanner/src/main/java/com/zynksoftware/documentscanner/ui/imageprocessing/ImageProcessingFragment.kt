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