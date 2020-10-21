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

package com.zynksoftware.documentscanner.model

data class DocumentScannerErrorModel(
    var errorMessage: ErrorMessage? = null,
    var throwable: Throwable? = null
) {
    enum class ErrorMessage(val error: String){
        TAKE_IMAGE_FROM_GALLERY_ERROR("TAKE_IMAGE_FROM_GALLERY_ERROR"),
        PHOTO_CAPTURE_FAILED("PHOTO_CAPTURE_FAILED"),
        CAMERA_USE_CASE_BINDING_FAILED("CAMERA_USE_CASE_BINDING_FAILED"),
        DETECT_LARGEST_QUADRILATERAL_FAILED("DETECT_LARGEST_QUADRILATERAL_FAILED"),
        INVALID_IMAGE("INVALID_IMAGE"),
        CAMERA_PERMISSION_REFUSED_WITHOUT_NEVER_ASK_AGAIN("CAMERA_PERMISSION_REFUSED_WITHOUT_NEVER_ASK_AGAIN"),
        CAMERA_PERMISSION_REFUSED_GO_TO_SETTINGS("CAMERA_PERMISSION_REFUSED_GO_TO_SETTINGS"),
        STORAGE_PERMISSION_REFUSED_WITHOUT_NEVER_ASK_AGAIN("STORAGE_PERMISSION_REFUSED_WITHOUT_NEVER_ASK_AGAIN"),
        STORAGE_PERMISSION_REFUSED_GO_TO_SETTINGS("STORAGE_PERMISSION_REFUSED_GO_TO_SETTINGS"),
        CROPPING_FAILED("CROPPING_FAILED");
    }
}