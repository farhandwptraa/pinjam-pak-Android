package com.example.pinjampak.presentation.camera

import android.Manifest
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CameraCaptureScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    var preview by remember { mutableStateOf<Preview?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var hasPermission by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    LaunchedEffect(true) {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(hasPermission) {
        if (!hasPermission) return@LaunchedEffect

        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            val cameraProvider = cameraProviderFuture.get()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal memulai kamera: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        Button(
            onClick = {
                if (imageCapture == null) {
                    Toast.makeText(context, "Kamera belum siap", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                    .format(System.currentTimeMillis())
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/CameraX")
                    }
                }

                val outputOptions = ImageCapture.OutputFileOptions.Builder(
                    context.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ).build()

                imageCapture?.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            val uri = outputFileResults.savedUri
                            Toast.makeText(context, "Foto disimpan", Toast.LENGTH_SHORT).show()
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("capturedKtpUri", uri.toString())
                            navController.popBackStack()
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Toast.makeText(context, "Gagal ambil foto", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Ambil Foto")
        }
    }
}