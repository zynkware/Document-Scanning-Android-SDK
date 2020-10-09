package com.zynksoftware.documentscanner.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.zynksoftware.documentscanner.R

internal class ScanCanvasView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private var shape: Shape? = null
    private var paint = Paint()
    private var border = Paint()

    init {
        paint.color = ContextCompat.getColor(context, R.color.zdc_white_transparent)
        border.color = ContextCompat.getColor(context, android.R.color.white)
        border.strokeWidth = context.resources.getDimension(R.dimen.zdc_polygon_line_stroke_width)
        border.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        shape?.resize(contentWidth.toFloat(), contentHeight.toFloat())
        shape?.draw(canvas, paint)
        shape?.draw(canvas, border)
    }

    fun showShape(shape: Shape) {
        this.shape = shape
    }

    fun clearShape() {
        shape = null
    }
}