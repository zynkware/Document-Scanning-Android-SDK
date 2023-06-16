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


package com.zynksoftware.documentscanner.ui.components.scansurface

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.zynksoftware.documentscanner.R
import com.zynksoftware.documentscanner.common.extensions.yuvToRgba
import com.zynksoftware.documentscanner.common.utils.ImageDetectionProperties
import com.zynksoftware.documentscanner.common.utils.OpenCvNativeBridge
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel.ErrorMessage
import kotlinx.android.synthetic.main.scan_surface_view.view.*
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal class ScanSurfaceView : FrameLayout {

    companion object {
        private val TAG = ScanSurfaceView::class.simpleName

        private const val TIME_POST_PICTURE = 1500L
        private const val DEFAULT_TIME_POST_PICTURE = 1500L
        private const val IMAGE_ANALYSIS_SCALE_WIDTH = 400
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    lateinit var lifecycleOwner: LifecycleOwner
    lateinit var listener: ScanSurfaceListener
    lateinit var originalImageFile: File

    private val nativeClass = OpenCvNativeBridge()
    private var autoCaptureTimer: CountDownTimer? = null
    private var millisLeft = 0L
    private var isAutoCaptureScheduled = false
    private var isCapturing = false

    private var imageAnalysis: ImageAnalysis? = null
    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var previewSize: android.util.Size

    var isAutoCaptureOn: Boolean = true
    private var isFlashEnabled: Boolean = false
    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF

    init {
        LayoutInflater.from(context).inflate(R.layout.scan_surface_view, this, true)
    }

    fun start() {
        viewFinder.post {
            viewFinder.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            previewSize = android.util.Size(viewFinder.width, viewFinder.height)
            openCamera()
        }
    }

    private fun clearAndInvalidateCanvas() {
        scanCanvasView.clearShape()
    }

    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            try {
                bindCamera()
                checkIfFlashIsPresent()
            } catch (exc: Exception) {
                Log.e(TAG, ErrorMessage.CAMERA_USE_CASE_BINDING_FAILED.error, exc)
                listener.onError(DocumentScannerErrorModel(ErrorMessage.CAMERA_USE_CASE_BINDING_FAILED, exc))
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCamera() {
        cameraProvider?.unbindAll()
        camera = null
        setUseCases()
    }

    private fun setImageCapture() {
        if(imageCapture != null && cameraProvider?.isBound(imageCapture!!) == true) {
            cameraProvider?.unbind(imageCapture)
        }

        imageCapture = null
        imageCapture = ImageCapture.Builder()
            .setFlashMode(flashMode)
            .build()
    }

    fun unbindCamera() {
        cameraProvider?.unbind(imageAnalysis)
    }

    private fun setUseCases() {
        preview = Preview.Builder()
            .setTargetResolution(previewSize)
            .build()
            .also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

        setImageCapture()

        val aspectRatio: Float = previewSize.width / previewSize.height.toFloat()
        val width = IMAGE_ANALYSIS_SCALE_WIDTH
        val height = (width / aspectRatio).roundToInt()

        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetResolution(android.util.Size(width, height))
            .build()

        imageAnalysis?.setAnalyzer(ContextCompat.getMainExecutor(context)) { image ->
            if (isAutoCaptureOn) {
                try {
                    val mat = image.yuvToRgba()
                    val originalPreviewSize = mat.size()
                    val largestQuad = nativeClass.detectLargestQuadrilateral(mat)
                    mat.release()
                    if (null != largestQuad) {
                        drawLargestRect(largestQuad.contour, largestQuad.points, originalPreviewSize)
                    } else {
                        clearAndInvalidateCanvas()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, ErrorMessage.DETECT_LARGEST_QUADRILATERAL_FAILED.error, e)
                    listener.onError(DocumentScannerErrorModel(ErrorMessage.DETECT_LARGEST_QUADRILATERAL_FAILED, e))
                    clearAndInvalidateCanvas()
                }
            } else {
                clearAndInvalidateCanvas()
            }
            image.close()
        }

        camera = cameraProvider!!.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis, imageCapture)
    }

    private fun drawLargestRect(approx: MatOfPoint2f, points: Array<Point>, stdSize: Size) {
        // Attention: axis are swapped
        val previewWidth = stdSize.height.toFloat()
        val previewHeight = stdSize.width.toFloat()

        val resultWidth = max(previewWidth - points[0].y.toFloat(), previewWidth - points[1].y.toFloat()) -
                min(previewWidth - points[2].y.toFloat(), previewWidth - points[3].y.toFloat())

        val resultHeight = max(points[1].x.toFloat(), points[2].x.toFloat()) - min(points[0].x.toFloat(), points[3].x.toFloat())

        val imgDetectionPropsObj = ImageDetectionProperties(previewWidth.toDouble(), previewHeight.toDouble(),
            points[0], points[1], points[2], points[3], resultWidth.toInt(), resultHeight.toInt())
        if (imgDetectionPropsObj.isNotValidImage(approx)) {
            scanCanvasView.clearShape()
            cancelAutoCapture()
        } else {
            if (!isAutoCaptureScheduled) {
                scheduleAutoCapture()
            }
            scanCanvasView.showShape(previewWidth, previewHeight, points)
        }
    }

    private fun scheduleAutoCapture() {
        isAutoCaptureScheduled = true
        millisLeft = 0L
        autoCaptureTimer = object : CountDownTimer(DEFAULT_TIME_POST_PICTURE, 100) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished != millisLeft) {
                    millisLeft = millisUntilFinished
                }
            }

            override fun onFinish() {
                isAutoCaptureScheduled = false
                autoCapture()
            }
        }
        autoCaptureTimer?.start()
    }

    private fun autoCapture() {
        if (isCapturing)
            return
        cancelAutoCapture()
        takePicture()
    }

    fun takePicture() {
        Log.d(TAG, "ZDCtakePicture Starts ${System.currentTimeMillis()}")
        listener.scanSurfaceShowProgress()
        isCapturing = true

        val imageCapture = imageCapture ?: return
        val outputOptions = ImageCapture.OutputFileOptions.Builder(originalImageFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    listener.scanSurfaceHideProgress()
                    Log.e(TAG, "${ErrorMessage.PHOTO_CAPTURE_FAILED.error}: ${exc.message}", exc)
                    listener.onError(DocumentScannerErrorModel(ErrorMessage.PHOTO_CAPTURE_FAILED, exc))
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    listener.scanSurfaceHideProgress()

                    unbindCamera()

                    clearAndInvalidateCanvas()
                    listener.scanSurfacePictureTaken()
                    postDelayed({ isCapturing = false }, TIME_POST_PICTURE)
                    Log.d(TAG, "ZDCtakePicture ends ${System.currentTimeMillis()}")
                }
            })
    }

    private fun checkIfFlashIsPresent() {
        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            listener.showFlash()
        } else {
            listener.hideFlash()
        }
    }

    private fun cancelAutoCapture() {
        if (isAutoCaptureScheduled) {
            isAutoCaptureScheduled = false
            autoCaptureTimer?.cancel()
        }
    }

    fun switchFlashState() {
        isFlashEnabled = !isFlashEnabled
        flashMode = if (isFlashEnabled) {
            listener.showFlashModeOn()
            ImageCapture.FLASH_MODE_ON
        } else {
            listener.showFlashModeOff()
            ImageCapture.FLASH_MODE_OFF
        }
        setImageCapture()
        camera = cameraProvider!!.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, imageCapture)
    }
}