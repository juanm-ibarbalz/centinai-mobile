package com.centinai.app.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.centinai.app.utils.Constants
import com.centinai.app.utils.isConnected
import com.centinai.app.utils.TokenBridge
import com.centinai.app.utils.buildCentinaiWebClient
import com.centinai.app.viewmodel.WebLoadViewModel


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    modifier: Modifier = Modifier,
    startUrl: String = Constants.APP_WEB_URL,
    viewModel: WebLoadViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!isConnected(context)) {
            viewModel.reportError("Sin conexión a internet")
        }
    }

    Box(modifier = modifier) {
        if (viewModel.hasError) {
            ErrorScreen(
                message = viewModel.errorMessage ?: "Error al cargar la página",
                onRetry = {
                    if (isConnected(context)) {
                        viewModel.reset()
                    } else {
                        viewModel.reportError("Sin conexión a internet")
                    }
                }
            )
        } else {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        settings.builtInZoomControls = false
                        settings.displayZoomControls = false
                        settings.userAgentString =
                            "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.101 Mobile Safari/537.36"

                        addJavascriptInterface(TokenBridge(context), "Android")

                        webViewClient = buildCentinaiWebClient(
                            context = context,
                            viewModel = viewModel
                        )

                        Log.d("WebViewScreen", "🌐 Intentando cargar URL: $startUrl")
                        loadUrl(startUrl)
                    }
                }
            )

            if (!viewModel.isWebLoaded) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
