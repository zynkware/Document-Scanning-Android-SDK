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

package com.zynksoftware.documentscanner.common.extensions

import android.graphics.ImageFormat
import androidx.camera.core.ImageProxy
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

internal fun ImageProxy.yuvToRgba(): Mat {
    val rgbaMat = Mat()

    if (format == ImageFormat.YUV_420_888
        && planes.size == 3) {

        val planes: Array<Image.Plane> = image!!.getPlanes()
        val w: Int = image!!.getWidth()
        val h: Int = image!!.getHeight()
        val chromaPixelStride = planes[1].pixelStride


        if (chromaPixelStride == 2) { // Chroma channels are interleaved
            assert(planes[0].pixelStride == 1)
            assert(planes[2].pixelStride == 2)
            val y_plane = planes[0].buffer
            val y_plane_step = planes[0].rowStride
            val uv_plane1 = planes[1].buffer
            val uv_plane1_step = planes[1].rowStride
            val uv_plane2 = planes[2].buffer
            val uv_plane2_step = planes[2].rowStride
            val y_mat = Mat(h, w, CvType.CV_8UC1, y_plane, y_plane_step.toLong())
            val uv_mat1 = Mat(h / 2, w / 2, CvType.CV_8UC2, uv_plane1, uv_plane1_step.toLong())
            val uv_mat2 = Mat(h / 2, w / 2, CvType.CV_8UC2, uv_plane2, uv_plane2_step.toLong())
            val addr_diff = uv_mat2.dataAddr() - uv_mat1.dataAddr()
            if (addr_diff > 0) {
                assert(addr_diff == 1L)
                Imgproc.cvtColorTwoPlane(y_mat, uv_mat1, rgbaMat, Imgproc.COLOR_YUV2RGBA_NV12)
            } else {
                assert(addr_diff == -1L)
                Imgproc.cvtColorTwoPlane(y_mat, uv_mat2, rgbaMat, Imgproc.COLOR_YUV2RGBA_NV21)
            }

        } else { // Chroma channels are not interleaved
            val yuv_bytes = ByteArray(w * (h + h / 2))
            val y_plane = planes[0].buffer
            val u_plane = planes[1].buffer
            val v_plane = planes[2].buffer
            var yuv_bytes_offset = 0
            val y_plane_step = planes[0].rowStride
            if (y_plane_step == w) {
                y_plane[yuv_bytes, 0, w * h]
                yuv_bytes_offset = w * h
            } else {
                val padding = y_plane_step - w
                for (i in 0 until h) {
                    y_plane[yuv_bytes, yuv_bytes_offset, w]
                    yuv_bytes_offset += w
                    if (i < h - 1) {
                        y_plane.position(y_plane.position() + padding)
                    }
                }
                assert(yuv_bytes_offset == w * h)
            }
            val chromaRowStride = planes[1].rowStride
            val chromaRowPadding = chromaRowStride - w / 2
            if (chromaRowPadding == 0) {
                // When the row stride of the chroma channels equals their width, we can copy
                // the entire channels in one go
                u_plane[yuv_bytes, yuv_bytes_offset, w * h / 4]
                yuv_bytes_offset += w * h / 4
                v_plane[yuv_bytes, yuv_bytes_offset, w * h / 4]
            } else {
                // When not equal, we need to copy the channels row by row
                for (i in 0 until h / 2) {
                    u_plane[yuv_bytes, yuv_bytes_offset, w / 2]
                    yuv_bytes_offset += w / 2
                    if (i < h / 2 - 1) {
                        u_plane.position(u_plane.position() + chromaRowPadding)
                    }
                }
                for (i in 0 until h / 2) {
                    v_plane[yuv_bytes, yuv_bytes_offset, w / 2]
                    yuv_bytes_offset += w / 2
                    if (i < h / 2 - 1) {
                        v_plane.position(v_plane.position() + chromaRowPadding)
                    }
                }
            }
            val yuv_mat = Mat(h + h / 2, w, CvType.CV_8UC1)
            yuv_mat.put(0, 0, yuv_bytes)
            Imgproc.cvtColor(yuv_mat, rgbaMat, Imgproc.COLOR_YUV2RGBA_I420, 4)
        }


    }

    return rgbaMat
}