package com.dskochilov.easydinner.presentation.pages

import com.dskochilov.easydinner.R
import org.junit.Assert.assertEquals
import org.junit.Test

class AiFridgeCameraUiStateTest {

    @Test
    fun cameraPlaceholderUsesPermissionCopyWhenCameraPermissionIsMissing() {
        val state = cameraPlaceholderState(hasCameraPermission = false)

        assertEquals(R.string.ai_camera_no_permission, state.titleResId)
        assertEquals(R.string.ai_camera_request_permission, state.descriptionResId)
    }

    @Test
    fun cameraPlaceholderUsesPreviewCopyWhenCameraPermissionIsGranted() {
        val state = cameraPlaceholderState(hasCameraPermission = true)

        assertEquals(R.string.ai_fridge_photo, state.titleResId)
        assertEquals(R.string.ai_camera_preview, state.descriptionResId)
    }

    @Test
    fun recognizeButtonUsesRecognizeCopyWhenReady() {
        val state = recognizeProductsButtonState(
            hasCameraPermission = true,
            isCameraReady = true,
            isRecognizing = false,
        )

        assertEquals(R.string.ai_recognize_products, state.textResId)
        assertEquals(true, state.enabled)
        assertEquals(false, state.showLoadingDots)
    }

    @Test
    fun recognizeButtonIsDisabledAndShowsLoadingDotsWhileRecognizing() {
        val state = recognizeProductsButtonState(
            hasCameraPermission = true,
            isCameraReady = true,
            isRecognizing = true,
        )

        assertEquals(null, state.textResId)
        assertEquals(false, state.enabled)
        assertEquals(true, state.showLoadingDots)
    }
}
