package com.centinai.app.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.ConsoleMessage
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.centinai.app.utils.Constants
import com.centinai.app.utils.TokenBridge
import com.centinai.app.utils.buildCentinaiWebClient
import com.centinai.app.utils.isConnected
import com.centinai.app.viewmodel.WebLoadViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    modifier: Modifier = Modifier,
    startUrl: String = Constants.APP_WEB_URL,
    viewModel: WebLoadViewModel
) {
    val context = LocalContext.current
    val tokenBridge = remember { TokenBridge(context.applicationContext) }

    LaunchedEffect(Unit) {
        if (!isConnected(context)) {
            viewModel.reportError("Sin conexión a internet")
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
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
                    Log.d("WebViewScreen_Factory", "Creando instancia de WebView.")
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = true // Revisa si es necesario
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true

                        webChromeClient = object : WebChromeClient() {
                            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                consoleMessage?.let {
                                    Log.d(
                                        "WebView_JS_Console",
                                        "${it.message()} -- From line ${it.lineNumber()} of ${it.sourceId()}"
                                    )
                                }
                                return true
                            }
                        }

                        Log.d("WebViewScreen_Setup", "Inyectando TokenBridge como 'Android'")

                        tokenBridge.setWebView(this)
                        Log.d("WebViewScreen_Setup", "Instancia de WebView pasada a TokenBridge.")


                        webViewClient = buildCentinaiWebClient(
                            context = ctx,
                            viewModel = viewModel
                        )

                        Log.d("WebViewScreen_Setup", "🌐 Intentando cargar URL: $startUrl")
                        if (isConnected(ctx)) {
                            loadUrl(startUrl)
                        } else {
                            viewModel.reportError("Sin conexión a internet al intentar cargar URL")
                        }
                    }
                },
            )

            if (!viewModel.isWebLoaded && !viewModel.hasError) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }


    DisposableEffect(Unit) {
        onDispose {
            Log.d("WebViewScreen", "DisposableEffect: Limpiando referencia de WebView en TokenBridge.")
        }
    }
}