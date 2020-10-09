package com.zynksoftware.documentscanner.common.utils

import android.content.Context
import org.opencv.core.Point
import kotlin.math.max
import kotlin.math.sqrt

internal object MathUtils {

    private fun angle(p1: Point, p2: Point, p0: Point): Double {
        val dx1 = p1.x - p0.x
        val dy1 = p1.y - p0.y
        val dx2 = p2.x - p0.x
        val dy2 = p2.y - p0.y
        return (dx1 * dx2 + dy1 * dy2) / sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10)
    }

    fun getDistance(p1: Point, p2: Point): Double {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        return sqrt(dx * dx + dy * dy)
    }

    fun getMaxCosine(maxCosine: Double, approxPoints: Array<Point>): Double {
        var newMaxCosine = maxCosine
        for (i in 2..4) {
            val cosine: Double = Math.abs(
                angle(approxPoints[i % 4], approxPoints[i - 2], approxPoints[i - 1])
            )
            newMaxCosine = max(cosine, newMaxCosine)
        }
        return newMaxCosine
    }
}