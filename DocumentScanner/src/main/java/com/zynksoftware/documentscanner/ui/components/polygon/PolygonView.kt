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


package com.zynksoftware.documentscanner.ui.components.polygon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.zynksoftware.documentscanner.R
import java.util.*

internal class PolygonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var paint: Paint = Paint()
    private var pointer1: ImageView
    private var pointer2: ImageView
    private var pointer3: ImageView
    private var pointer4: ImageView
    private var pointPadding = resources.getDimension(R.dimen.zdc_point_padding).toInt()

    companion object {
        private val TAG = PolygonView::class.simpleName
        private const val HALF = 2
        private const val THREE_PARTS = 3
    }

    init {
        pointer1 = getImageView(0, 0)
        pointer2 = getImageView(width, 0)
        pointer3 = getImageView(0, height)
        pointer4 = getImageView(width, height)

        addView(pointer1)
        addView(pointer2)
        addView(pointer3)
        addView(pointer4)

        paint.color = ContextCompat.getColor(context, android.R.color.white)
        paint.strokeWidth = context.resources.getDimension(R.dimen.zdc_polygon_line_stroke_width)
        paint.isAntiAlias = true
    }

    fun getOrderedValidEdgePoints(tempBitmap: Bitmap, pointFs: List<PointF>): Map<Int, PointF> {
        var orderedPoints: Map<Int, PointF> = getOrderedPoints(pointFs)
        if (!isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap)
        }
        return orderedPoints
    }

    fun setPoints(pointFMap: Map<Int, PointF>) {
        if (pointFMap.size == 4) {
            setPointsCoordinates(pointFMap)
        }
    }

    fun getPoints(): Map<Int, PointF> {
        val points: MutableList<PointF> = ArrayList()
        points.add(PointF(pointer1.x, pointer1.y))
        points.add(PointF(pointer2.x, pointer2.y))
        points.add(PointF(pointer3.x, pointer3.y))
        points.add(PointF(pointer4.x, pointer4.y))
        return getOrderedPoints(points)
    }

    fun isValidShape(pointFMap: Map<Int, PointF>): Boolean {
        return pointFMap.size == 4
    }

    private fun getOutlinePoints(tempBitmap: Bitmap): Map<Int, PointF> {
        val offsetWidth = (tempBitmap.width / THREE_PARTS).toFloat()
        val offsetHeight = (tempBitmap.height / THREE_PARTS).toFloat()
        val screenXCenter = tempBitmap.width / HALF
        val screenYCenter = tempBitmap.height / HALF
        val outlinePoints: MutableMap<Int, PointF> = HashMap()
        outlinePoints[0] = PointF(screenXCenter - offsetWidth, screenYCenter - offsetHeight)
        outlinePoints[1] = PointF(screenXCenter + offsetWidth, screenYCenter - offsetHeight)
        outlinePoints[2] = PointF(screenXCenter - offsetWidth , screenYCenter + offsetHeight)
        outlinePoints[3] = PointF(screenXCenter + offsetWidth, screenYCenter + offsetHeight)
        return outlinePoints
    }

    private fun getOrderedPoints(points: List<PointF>): Map<Int, PointF> {
        val centerPoint = PointF()
        val size = points.size
        for (pointF in points) {
            centerPoint.x += pointF.x / size
            centerPoint.y += pointF.y / size
        }
        val orderedPoints: MutableMap<Int, PointF> = HashMap()
        for (pointF in points) {
            var index = -1
            if (pointF.x < centerPoint.x && pointF.y < centerPoint.y) {
                index = 0
            } else if (pointF.x > centerPoint.x && pointF.y < centerPoint.y) {
                index = 1
            } else if (pointF.x < centerPoint.x && pointF.y > centerPoint.y) {
                index = 2
            } else if (pointF.x > centerPoint.x && pointF.y > centerPoint.y) {
                index = 3
            }
            orderedPoints[index] = pointF
        }
        return orderedPoints
    }

    private fun setPointsCoordinates(pointFMap: Map<Int, PointF>) {
        try {
            pointer1.x = pointFMap.getValue(0).x - pointPadding
            pointer1.y = pointFMap.getValue(0).y - pointPadding

            pointer2.x = pointFMap.getValue(1).x - pointPadding
            pointer2.y = pointFMap.getValue(1).y - pointPadding

            pointer3.x = pointFMap.getValue(2).x - pointPadding
            pointer3.y = pointFMap.getValue(2).y - pointPadding

            pointer4.x = pointFMap.getValue(3).x - pointPadding
            pointer4.y = pointFMap.getValue(3).y - pointPadding
        } catch (exception: NoSuchElementException) {
            // avoid crash if there is no point to add to image
            Log.e(TAG, "", exception)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawLine(
            pointer1.x + pointer1.width / 2, pointer1.y + pointer1.height / 2,
            pointer3.x + pointer3.width / 2, pointer3.y + pointer3.height / 2,
            paint
        )
        canvas.drawLine(
            pointer1.x + pointer1.width / 2, pointer1.y + pointer1.height / 2,
            pointer2.x + pointer2.width / 2, pointer2.y + pointer2.height / 2,
            paint
        )
        canvas.drawLine(
            pointer2.x + pointer2.width / 2, pointer2.y + pointer2.height / 2,
            pointer4.x + pointer4.width / 2, pointer4.y + pointer4.height / 2,
            paint
        )
        canvas.drawLine(
            pointer3.x + pointer3.width / 2, pointer3.y + pointer3.height / 2,
            pointer4.x + pointer4.width / 2, pointer4.y + pointer4.height / 2,
            paint
        )
    }

    private fun getImageView(x: Int, y: Int): ImageView {
        val imageView = PolygonPointImageView(context, this)
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        imageView.layoutParams = layoutParams
        imageView.setImageResource(R.drawable.crop_corner_circle)
        imageView.setPadding(pointPadding, pointPadding, pointPadding, pointPadding)
        imageView.x = x.toFloat()
        imageView.y = y.toFloat()
        return imageView
    }
}