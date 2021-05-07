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


package com.zynksoftware.documentscanner.ui.imagecrop

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.zynksoftware.documentscanner.R
import com.zynksoftware.documentscanner.common.extensions.scaledBitmap
import com.zynksoftware.documentscanner.common.utils.OpenCvNativeBridge
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel
import com.zynksoftware.documentscanner.ui.base.BaseFragment
import com.zynksoftware.documentscanner.ui.scan.InternalScanActivity
import id.zelory.compressor.determineImageRotation
import kotlinx.android.synthetic.main.fragment_image_crop.*

internal class ImageCropFragment : BaseFragment() {

    companion object {
        private val TAG = ImageCropFragment::class.simpleName

        fun newInstance(): ImageCropFragment {
            return ImageCropFragment()
        }
    }

    private val nativeClass = OpenCvNativeBridge()

    private var selectedImage: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_crop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sourceBitmap = BitmapFactory.decodeFile(getScanActivity().originalImageFile.absolutePath)
        if (sourceBitmap != null) {
            selectedImage = determineImageRotation(getScanActivity().originalImageFile, sourceBitmap)
        } else {
            Log.e(TAG, DocumentScannerErrorModel.ErrorMessage.INVALID_IMAGE.error)
            onError(DocumentScannerErrorModel(DocumentScannerErrorModel.ErrorMessage.INVALID_IMAGE))
            Handler(Looper.getMainLooper()).post{
                closeFragment()
            }
        }
        holderImageView.post {
            initializeCropping()
        }

        initListeners()
    }

    private fun initListeners() {
        closeButton.setOnClickListener {
            closeFragment()
        }
        confirmButton.setOnClickListener {
            onConfirmButtonClicked()
        }
    }

    private fun getScanActivity(): InternalScanActivity {
        return (requireActivity() as InternalScanActivity)
    }

    private fun initializeCropping() {
        if(selectedImage != null && selectedImage!!.width > 0 && selectedImage!!.height > 0) {
            val scaledBitmap: Bitmap = selectedImage!!.scaledBitmap(holderImageCrop.width, holderImageCrop.height)
            imagePreview.setImageBitmap(scaledBitmap)
            val tempBitmap = (imagePreview.drawable as BitmapDrawable).bitmap
            val pointFs = getEdgePoints(tempBitmap)
            Log.d(TAG, "ZDCgetEdgePoints ends ${System.currentTimeMillis()}")
            polygonView.setPoints(pointFs)
            polygonView.visibility = View.VISIBLE
            val padding = resources.getDimension(R.dimen.zdc_polygon_dimens).toInt()
            val layoutParams = FrameLayout.LayoutParams(tempBitmap.width + padding, tempBitmap.height + padding)
            layoutParams.gravity = Gravity.CENTER
            polygonView.layoutParams = layoutParams
        }
    }

    private fun onError(error: DocumentScannerErrorModel) {
        if (isAdded) {
            getScanActivity().onError(error)
        }
    }

    private fun onConfirmButtonClicked() {
        getCroppedImage()
        startImageProcessingFragment()
    }

    private fun getEdgePoints(tempBitmap: Bitmap): Map<Int, PointF> {
        Log.d(TAG, "ZDCgetEdgePoints Starts ${System.currentTimeMillis()}")
        val pointFs: List<PointF> = nativeClass.getContourEdgePoints(tempBitmap)
        return polygonView.getOrderedValidEdgePoints(tempBitmap, pointFs)
    }

    private fun getCroppedImage() {
        if(selectedImage != null) {
            try {
                Log.d(TAG, "ZDCgetCroppedImage starts ${System.currentTimeMillis()}")
                val points: Map<Int, PointF> = polygonView.getPoints()
                val xRatio: Float = selectedImage!!.width.toFloat() / imagePreview.width
                val yRatio: Float = selectedImage!!.height.toFloat() / imagePreview.height
                val pointPadding = requireContext().resources.getDimension(R.dimen.zdc_point_padding).toInt()
                val x1: Float = (points.getValue(0).x + pointPadding) * xRatio
                val x2: Float = (points.getValue(1).x + pointPadding) * xRatio
                val x3: Float = (points.getValue(2).x + pointPadding) * xRatio
                val x4: Float = (points.getValue(3).x + pointPadding) * xRatio
                val y1: Float = (points.getValue(0).y + pointPadding) * yRatio
                val y2: Float = (points.getValue(1).y + pointPadding) * yRatio
                val y3: Float = (points.getValue(2).y + pointPadding) * yRatio
                val y4: Float = (points.getValue(3).y + pointPadding) * yRatio
                getScanActivity().croppedImage = nativeClass.getScannedBitmap(selectedImage!!, x1, y1, x2, y2, x3, y3, x4, y4)
                Log.d(TAG, "ZDCgetCroppedImage ends ${System.currentTimeMillis()}")
            } catch (e: java.lang.Exception) {
                Log.e(TAG, DocumentScannerErrorModel.ErrorMessage.CROPPING_FAILED.error, e)
                onError(DocumentScannerErrorModel(DocumentScannerErrorModel.ErrorMessage.CROPPING_FAILED, e))
            }
        } else {
            Log.e(TAG, DocumentScannerErrorModel.ErrorMessage.INVALID_IMAGE.error)
            onError(DocumentScannerErrorModel(DocumentScannerErrorModel.ErrorMessage.INVALID_IMAGE))
        }
    }

    private fun startImageProcessingFragment() {
        getScanActivity().showImageProcessingFragment()
    }

    private fun closeFragment() {
        getScanActivity().closeCurrentFragment()
    }
}