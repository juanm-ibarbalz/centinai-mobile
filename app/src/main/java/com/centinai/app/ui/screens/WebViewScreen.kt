package com.centinai.app.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.centinai.app.utils.isConnected


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(modifier: Modifier = Modifier) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf("No se pudo cargar la página") }

    LaunchedEffect(Unit) {
        if (!isConnected(context)) {
            errorMessage = "Sin conexión a internet"
            hasError = true
            isLoading = false
        }
    }


    Box(modifier = modifier) {
        if (hasError) {
            ErrorScreen(
                message = errorMessage,
                onRetry = {
                    if (isConnected(context)) {
                        hasError = false
                        isLoading = true
                    } else {
                        errorMessage = "Sin conexión a internet"
                        hasError = true
                        isLoading = false
                    }
                }
            )
        } else {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: Bitmap?
                            ) {
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                hasError = true
                                isLoading = false
                            }
                        }

                        loadUrl("https://www.google.com") // TEMPORAL: usaremos CentinAI después
                    }
                }
            )
        }
        if (isLoading && !hasError) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}