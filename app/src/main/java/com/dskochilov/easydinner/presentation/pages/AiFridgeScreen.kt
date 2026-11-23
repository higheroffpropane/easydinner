package com.dskochilov.easydinner.presentation.pages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dskochilov.easydinner.R
import com.dskochilov.easydinner.presentation.TestTags
import com.dskochilov.easydinner.presentation.navigation.DinnerPage
import androidx.compose.material3.CircularProgressIndicator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AiFridgeScreen(
    isActive: Boolean,
    onSearchIngredients: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var hasCameraPermission by remember {
        mutableStateOf(context.hasCameraPermission())
    }
    var isScreenForeground by remember { mutableStateOf(true) }
    val canAcceptRecognitionResult = isActive && isScreenForeground
    val latestCanAcceptRecognitionResult by rememberUpdatedState(canAcceptRecognitionResult)
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var recognitionJob by remember { mutableStateOf<Job?>(null) }
    var isRecognizing by remember { mutableStateOf(false) }
    var showAiResult by rememberSaveable { mutableStateOf(false) }
    var aiIngredients by rememberSaveable {
        mutableStateOf("eggs, cheese, tomatoes, spinach")
    }

    fun cancelRecognition() {
        recognitionJob?.cancel()
        recognitionJob = null
        isRecognizing = false
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        },
    )

    ObserveAiScreenLifecycle(
        onResume = {
            isScreenForeground = true
            hasCameraPermission = context.hasCameraPermission()
        },
        onStop = {
            isScreenForeground = false
            cancelRecognition()
        },
    )

    LaunchedEffect(isActive, hasCameraPermission) {
        if (!isActive || !hasCameraPermission) {
            imageCapture = null
            cancelRecognition()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cancelRecognition()
        }
    }

    DinnerPage(
        title = stringResource(R.string.screen_ai_title),
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(22.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .then(
                        if (hasCameraPermission) {
                            Modifier
                        } else {
                            Modifier.clickable {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (hasCameraPermission) {
                    CameraPreview(
                        isActive = isActive,
                        onImageCaptureReady = { imageCapture = it },
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    CameraPlaceholder(hasCameraPermission = false)
                }
            }
            val recognizeButtonState = recognizeProductsButtonState(
                hasCameraPermission = hasCameraPermission,
                isCameraReady = imageCapture != null,
                isRecognizing = isRecognizing,
            )
            Button(
                onClick = {
                    val currentImageCapture = imageCapture ?: return@Button
                    isRecognizing = true
                    currentImageCapture.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                image.close()
                                if (!latestCanAcceptRecognitionResult) {
                                    cancelRecognition()
                                    return
                                }

                                recognitionJob?.cancel()
                                recognitionJob = coroutineScope.launch {
                                    try {
                                        aiIngredients = recognizeProductsFromPhoto()
                                        if (latestCanAcceptRecognitionResult) {
                                            showAiResult = true
                                        }
                                    } finally {
                                        isRecognizing = false
                                        recognitionJob = null
                                    }
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                cancelRecognition()
                            }
                        },
                    )
                },
                modifier = Modifier
                    .padding(start = 22.dp, end = 22.dp, bottom = 30.dp)
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag(TestTags.AI_TAKE_PHOTO),
                enabled = recognizeButtonState.enabled,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 18.dp),
            ) {
                RecognizeProductsButtonText(state = recognizeButtonState)
            }
        }
    }

    if (showAiResult) {
        AlertDialog(
            onDismissRequest = { showAiResult = false },
            title = {
                Text(stringResource(R.string.ai_result_title))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.ai_result_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedTextField(
                        value = aiIngredients,
                        onValueChange = { aiIngredients = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        shape = RoundedCornerShape(8.dp),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAiResult = false
                        onSearchIngredients(aiIngredients)
                    },
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(stringResource(R.string.ai_search))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiResult = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun ObserveAiScreenLifecycle(
    onResume: () -> Unit,
    onStop: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, onResume, onStop) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> onResume()
                Lifecycle.Event.ON_STOP -> onStop()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun RecognizeProductsButtonText(state: RecognizeProductsButtonState) {
    if (state.showLoadingDots) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
    } else {
        state.textResId?.let { textResId ->
            Text(stringResource(textResId))
        }
    }
}

@Composable
private fun CameraPreview(
    isActive: Boolean,
    onImageCaptureReady: (ImageCapture?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember(context) {
        ContextCompat.getMainExecutor(context)
    }
    val cameraProviderFuture = remember(context) {
        ProcessCameraProvider.getInstance(context)
    }
    val previewView = remember(context) {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(isActive, lifecycleOwner, previewView) {
        var isDisposed = false

        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                if (isDisposed || !isActive) {
                    cameraProvider.unbindAll()
                    onImageCaptureReady(null)
                    return@addListener
                }

                val preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                runCatching {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture,
                    )
                }.onSuccess {
                    onImageCaptureReady(imageCapture)
                }.onFailure {
                    onImageCaptureReady(null)
                }
            },
            cameraExecutor,
        )

        onDispose {
            isDisposed = true
            cameraProviderFuture.addListener(
                {
                    cameraProviderFuture.get().unbindAll()
                    onImageCaptureReady(null)
                },
                cameraExecutor,
            )
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier,
    )
}

@Composable
private fun CameraPlaceholder(
    hasCameraPermission: Boolean,
    modifier: Modifier = Modifier,
) {
    val state = cameraPlaceholderState(hasCameraPermission)

    Column(
        modifier = modifier.padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(54.dp),
        )
        Text(
            text = stringResource(state.titleResId),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = stringResource(state.descriptionResId),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

internal data class CameraPlaceholderState(
    @param:StringRes val titleResId: Int,
    @param:StringRes val descriptionResId: Int,
)

internal fun cameraPlaceholderState(hasCameraPermission: Boolean): CameraPlaceholderState {
    return if (hasCameraPermission) {
        CameraPlaceholderState(
            titleResId = R.string.ai_fridge_photo,
            descriptionResId = R.string.ai_camera_preview,
        )
    } else {
        CameraPlaceholderState(
            titleResId = R.string.ai_camera_no_permission,
            descriptionResId = R.string.ai_camera_request_permission,
        )
    }
}

internal data class RecognizeProductsButtonState(
    @param:StringRes val textResId: Int?,
    val enabled: Boolean,
    val showLoadingDots: Boolean,
)

internal fun recognizeProductsButtonState(
    hasCameraPermission: Boolean,
    isCameraReady: Boolean,
    isRecognizing: Boolean,
): RecognizeProductsButtonState {
    return if (isRecognizing) {
        RecognizeProductsButtonState(
            textResId = null,
            enabled = false,
            showLoadingDots = true,
        )
    } else {
        RecognizeProductsButtonState(
            textResId = R.string.ai_recognize_products,
            enabled = hasCameraPermission && isCameraReady,
            showLoadingDots = false,
        )
    }
}

private suspend fun recognizeProductsFromPhoto(): String {
    delay(1_500)
    return "eggs, cheese, tomatoes, spinach"
}

private fun Context.hasCameraPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA,
    ) == PackageManager.PERMISSION_GRANTED
}
