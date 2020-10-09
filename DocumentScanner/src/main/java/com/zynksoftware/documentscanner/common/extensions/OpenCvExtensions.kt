package com.zynksoftware.documentscanner.common.extensions

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import java.util.*

internal fun Mat.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(this.cols(), this.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(this, bitmap)
    return bitmap
}

internal fun MatOfPoint2f.scaleRectangle(scale: Double): MatOfPoint2f {
    val originalPoints = this.toList()
    val resultPoints: MutableList<Point> = ArrayList()
    for (point in originalPoints) {
        resultPoints.add(Point(point.x * scale, point.y * scale))
    }
    val result = MatOfPoint2f()
    result.fromList(resultPoints)
    return result
}