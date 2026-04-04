package com.proyecto.eventos.core.hardware.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CameraManager {

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var cameraExecutor: ExecutorService? = null
    private var outputDirectory: File? = null
    private var onPictureResult: ((String) -> Unit)? = null
    private var isInitialized = false

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    override fun initialize() {
        // Evitar doble inicialización
        if (isInitialized) return

        cameraExecutor = Executors.newSingleThreadExecutor()
        outputDirectory = getOutputDirectory()
        isInitialized = true
    }

    override fun takePicture(onResult: (String) -> Unit) {
        this.onPictureResult = onResult

        // Verificar que la cámara esté inicializada
        val capture = imageCapture ?: run {
            onResult("")
            return
        }

        val executor = cameraExecutor ?: run {
            onResult("")
            return
        }

        val dir = outputDirectory ?: run {
            onResult("")
            return
        }

        // Crear archivo con nombre único
        val photoFile = File(
            dir,
            SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault())
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(photoFile)
            .build()

        capture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(
                    output: ImageCapture.OutputFileResults
                ) {
                    val savedUri = output.savedUri
                        ?: Uri.fromFile(photoFile)
                    onPictureResult?.invoke(savedUri.toString())
                }

                override fun onError(exc: ImageCaptureException) {
                    // Retornar string vacío para que el ViewModel
                    // sepa que hubo un error sin crashear
                    onPictureResult?.invoke("")
                }
            }
        )
    }

    override fun release() {
        try {
            cameraProvider?.unbindAll()
            cameraExecutor?.shutdown()
        } catch (e: Exception) {
            // Ignorar errores al liberar recursos
        } finally {
            camera = null
            imageCapture = null
            cameraExecutor = null
            isInitialized = false
        }
    }

    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        // Verificar permisos antes de iniciar
        if (!allPermissionsGranted()) return

        // Inicializar si no se ha hecho
        if (!isInitialized) initialize()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(
                        ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
                    )
                    .build()

                // Desvincular casos de uso anteriores
                cameraProvider?.unbindAll()

                // Vincular a ciclo de vida
                camera = cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                // Si falla el inicio de cámara notificar resultado vacío
                onPictureResult?.invoke("")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        return try {
            val mediaDir = context.externalMediaDirs
                .firstOrNull()
                ?.let { File(it, "EventosApp").apply { mkdirs() } }

            if (mediaDir != null && mediaDir.exists()) {
                mediaDir
            } else {
                context.filesDir
            }
        } catch (e: Exception) {

            context.filesDir
        }
    }
}