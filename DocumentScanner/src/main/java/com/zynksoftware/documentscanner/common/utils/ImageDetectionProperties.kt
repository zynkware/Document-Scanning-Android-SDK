package com.zynksoftware.documentscanner.common.utils

import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import kotlin.math.abs

internal class ImageDetectionProperties(
    private val previewWidth: Double, private val previewHeight: Double,
    private val topLeftPoint: Point, private val bottomLeftPoint: Point,
    private val bottomRightPoint: Point, private val topRightPoint: Point,
    private val resultWidth: Int, private val resultHeight: Int
) {

    companion object {
        private const val SMALLEST_ANGLE_COS = 0.25881905 //75 degrees
    }

    fun isNotValidImage(approx: MatOfPoint2f): Boolean {
        return isEdgeTouching || isAngleNotCorrect(approx) || isDetectedAreaBelowLimits()
    }

    private fun isAngleNotCorrect(approx: MatOfPoint2f): Boolean {
        return getMaxCosine(approx) || isLeftEdgeDistorted || isRightEdgeDistorted
    }

    private val isRightEdgeDistorted: Boolean
        get() = abs(topRightPoint.y - bottomRightPoint.y) > 100

    private val isLeftEdgeDistorted: Boolean
        get() = abs(topLeftPoint.y - bottomLeftPoint.y) > 100

    private fun getMaxCosine(approx: MatOfPoint2f): Boolean {
        var maxCosine = 0.0
        val approxPoints = approx.toArray()
        maxCosine = MathUtils.getMaxCosine(maxCosine, approxPoints)
        return maxCosine >= SMALLEST_ANGLE_COS
    }

    private val isEdgeTouching: Boolean
        get() = isTopEdgeTouching || isBottomEdgeTouching || isLeftEdgeTouching || isRightEdgeTouching

    private val isBottomEdgeTouching: Boolean
        get() = bottomLeftPoint.x >= previewHeight - 10 || bottomRightPoint.x >= previewHeight - 10

    private val isTopEdgeTouching: Boolean
        get() = topLeftPoint.x <= 10 || topRightPoint.x <= 10

    private val isRightEdgeTouching: Boolean
        get() = topRightPoint.y >= previewWidth - 10 || bottomRightPoint.y >= previewWidth - 10

    private val isLeftEdgeTouching: Boolean
        get() = topLeftPoint.y <= 10 || bottomLeftPoint.y <= 10

    private fun isDetectedAreaBelowLimits(): Boolean {
        return !(previewWidth / previewHeight >= 1 &&
                resultWidth.toDouble() / resultHeight.toDouble() >= 0.9 &&
                resultHeight.toDouble() >= 0.70 * previewHeight ||
                previewHeight / previewWidth >= 1 &&
                resultHeight.toDouble() / resultWidth.toDouble() >= 0.9 &&
                resultWidth.toDouble() >= 0.70 * previewWidth)
    }
}