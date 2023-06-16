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


package com.zynksoftware.documentscanner.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.zynksoftware.documentscanner.R
import org.opencv.core.Point

internal class ScanCanvasView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    companion object {
        private const val CLEAR_SHAPE_DELAY_IN_MILLIS = 600L
        private const val POINTER_ANIMATION_DURATION = 300L
    }

    private var paint = Paint()
    private var border = Paint()
    private val handlerClear = Handler(Looper.getMainLooper())

    private var shouldAnimate = true

    private var pointer1: View = View(context)
    private var pointer2: View = View(context)
    private var pointer3: View = View(context)
    private var pointer4: View = View(context)

    init {
        paint.color = ContextCompat.getColor(context, R.color.zdc_white_transparent)
        border.color = ContextCompat.getColor(context, android.R.color.white)
        border.strokeWidth = context.resources.getDimension(R.dimen.zdc_polygon_line_stroke_width)
        border.style = Paint.Style.STROKE
        border.isAntiAlias = true
        paint.isAntiAlias = true

        pointer1.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        pointer2.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        pointer3.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        pointer4.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        clearPointersPosition()

        addView(pointer1)
        addView(pointer2)
        addView(pointer3)
        addView(pointer4)
    }

    private fun clearPointersPosition() {
        pointer1.x = 0F
        pointer1.y = 0F
        pointer2.x = 0F
        pointer2.y = 0F
        pointer3.x = 0F
        pointer3.y = 0F
        pointer4.x = 0F
        pointer4.y = 0F
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        previewWidth?.let { previewWidth ->
            previewHeight?.let { previewHeight ->
                canvas.scale(width / previewWidth, height / previewHeight)
            }
        }

        canvas.drawLine(pointer1.x, pointer1.y, pointer4.x, pointer4.y, border)
        canvas.drawLine(pointer1.x, pointer1.y, pointer2.x, pointer2.y, border)
        canvas.drawLine(pointer3.x, pointer3.y, pointer4.x, pointer4.y, border)
        canvas.drawLine(pointer2.x, pointer2.y, pointer3.x, pointer3.y, border)

        val path = Path()
        path.moveTo(pointer1.x, pointer1.y)
        path.lineTo(pointer2.x, pointer2.y)
        path.lineTo(pointer3.x, pointer3.y)
        path.lineTo(pointer4.x, pointer4.y)
        path.close()

        path.let {
            canvas.drawPath(it, paint)
        }
    }

    private var previewWidth: Float? = null
    private var previewHeight: Float? = null

    fun showShape(previewWidth: Float, previewHeight: Float, points: Array<Point>) {
        this.previewWidth = previewWidth
        this.previewHeight = previewHeight

        val pointer1x = previewWidth - points[0].y.toFloat()
        val pointer1y = points[0].x.toFloat()
        val pointer2x = previewWidth - points[1].y.toFloat()
        val pointer2y = points[1].x.toFloat()
        val pointer3x = previewWidth - points[2].y.toFloat()
        val pointer3y = points[2].x.toFloat()
        val pointer4x = previewWidth - points[3].y.toFloat()
        val pointer4y = points[3].x.toFloat()

        if (pointer1.x == 0F && pointer1.y == 0F) {
            pointer1.x = pointer1x
            pointer1.y = pointer1y
            pointer2.x = pointer2x
            pointer2.y = pointer2y
            pointer3.x = pointer3x
            pointer3.y = pointer3y
            pointer4.x = pointer4x
            pointer4.y = pointer4y
        } else {
            if (shouldAnimate) {
                shouldAnimate = false

                pointer1.animate().translationX(pointer1x).translationY(pointer1y)
                    .setDuration(POINTER_ANIMATION_DURATION).withEndAction {
                        shouldAnimate = true
                    }.start()

                pointer2.animate().translationX(pointer2x).translationY(pointer2y)
                    .setDuration(POINTER_ANIMATION_DURATION).withEndAction {
                        shouldAnimate = true
                    }.start()

                pointer3.animate().translationX(pointer3x).translationY(pointer3y)
                    .setDuration(POINTER_ANIMATION_DURATION).withEndAction {
                        shouldAnimate = true
                    }.start()

                pointer4.animate().translationX(pointer4x).translationY(pointer4y)
                    .setDuration(POINTER_ANIMATION_DURATION).withEndAction {
                        shouldAnimate = true
                    }.start()
            }
        }

        handlerClear.removeCallbacks(runnable)
        invalidate()
    }

    fun clearShape() {
        handlerClear.postDelayed(runnable, CLEAR_SHAPE_DELAY_IN_MILLIS)
    }

    private val runnable = Runnable {
        pointer1.clearAnimation()
        pointer2.clearAnimation()
        pointer3.clearAnimation()
        pointer4.clearAnimation()
        clearPointersPosition()
    }
}