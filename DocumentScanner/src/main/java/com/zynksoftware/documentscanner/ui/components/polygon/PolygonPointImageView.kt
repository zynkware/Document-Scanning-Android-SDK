package com.zynksoftware.documentscanner.ui.components.polygon

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.zynksoftware.documentscanner.R

internal class PolygonPointImageView @JvmOverloads constructor(
    context: Context,
    private val polygonView: PolygonView? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var downPoint = PointF()
    private var startPoint = PointF()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        if (polygonView != null) {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val mv = PointF(event.x - downPoint.x, event.y - downPoint.y)
                    if (startPoint.x + mv.x + width < polygonView.width &&
                        startPoint.y + mv.y + height < polygonView.height &&
                        startPoint.x + mv.x > 0 && startPoint.y + mv.y > 0
                    ) {
                        x = startPoint.x + mv.x
                        y = startPoint.y + mv.y
                        startPoint = PointF(x, y)
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    downPoint.x = event.x
                    downPoint.y = event.y
                    startPoint = PointF(x, y)
                }
                MotionEvent.ACTION_UP -> {
                    performClick()
                }
            }
            polygonView.invalidate()
        }
        return true
    }

    // Because we call this from onTouchEvent, this code will be executed for both
    // normal touch events and for when the system calls this using Accessibility
    override fun performClick(): Boolean {
        super.performClick()

        val color = if (polygonView?.isValidShape(polygonView.getPoints()) == true) {
            ContextCompat.getColor(context, android.R.color.white)
        } else {
            ContextCompat.getColor(context, R.color.zdc_red)
        }
        polygonView?.paint?.color = color

        return true
    }

}