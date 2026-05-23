package com.papeljusto.app.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.papeljusto.app.domain.model.ScannedProductData
import com.papeljusto.app.domain.usecase.ScanProductUseCase
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Composable
fun ScannerOverlay(
    scanUseCase: ScanProductUseCase,
    onResult: (ScannedProductData) -> Unit,
    onDismiss: () -> Unit
)
{
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    var procesando by remember { mutableStateOf(false) }
    var mensajeEstado by remember { mutableStateOf("Apuntá al empaque y presioná capturar") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> tienePermiso = granted }

    LaunchedEffect(Unit)
    {
        if (!tienePermiso) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
        {
            if (tienePermiso)
            {
                val imageCapture = remember { ImageCapture.Builder().build() }

                CameraPreview(
                    imageCapture = imageCapture,
                    modifier = Modifier.fillMaxSize()
                )

                ViewfinderGuide(modifier = Modifier.align(Alignment.Center))

                if (!procesando)
                {
                    BotonCaptura(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 60.dp),
                        onClick = {
                            procesando = true
                            mensajeEstado = "Analizando..."
                            val executor = Executors.newSingleThreadExecutor()
                            imageCapture.takePicture(
                                executor,
                                object : ImageCapture.OnImageCapturedCallback()
                                {
                                    override fun onCaptureSuccess(proxy: ImageProxy)
                                    {
                                        val bitmap = proxy.toBitmap()
                                        proxy.close()
                                        scope.launch {
                                            val result = scanUseCase.execute(bitmap)
                                            if (result != null)
                                            {
                                                onResult(result)
                                            }
                                            else
                                            {
                                                mensajeEstado = "No se detectó texto. Intentá de nuevo."
                                                procesando = false
                                            }
                                        }
                                    }

                                    override fun onError(exc: ImageCaptureException)
                                    {
                                        Log.e("Scanner", "Error al capturar", exc)
                                        mensajeEstado = "Error al capturar. Intentá de nuevo."
                                        procesando = false
                                    }
                                }
                            )
                        }
                    )
                }
                else
                {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 72.dp),
                        color = Color.White
                    )
                }
            }
            else
            {
                Text(
                    text = "Se necesita permiso de cámara para escanear el empaque.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(top = 52.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
            )
            {
                Text(
                    text = mensajeEstado,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
            {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar escáner",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun CameraPreview(imageCapture: ImageCapture, modifier: Modifier = Modifier)
{
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).also { previewView ->
                val future = ProcessCameraProvider.getInstance(ctx)
                future.addListener({
                    val provider = future.get()
                    val preview = Preview.Builder().build()
                        .also { it.surfaceProvider = previewView.surfaceProvider }
                    try
                    {
                        provider.unbindAll()
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                    }
                    catch (e: Exception)
                    {
                        Log.e("Scanner", "Camera bind error", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ViewfinderGuide(modifier: Modifier = Modifier)
{
    Box(
        modifier = modifier
            .size(260.dp)
            .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
    )
}

@Composable
private fun BotonCaptura(onClick: () -> Unit, modifier: Modifier = Modifier)
{
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    )
    {
        IconButton(onClick = onClick, modifier = Modifier.fillMaxSize())
        {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
            )
        }
    }
}
