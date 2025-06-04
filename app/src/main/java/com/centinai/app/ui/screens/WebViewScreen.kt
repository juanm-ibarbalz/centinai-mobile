package com.centinai.app.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient // Importar WebChromeClient
import android.webkit.ConsoleMessage // Importar ConsoleMessage
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
    // Crear una única instancia de TokenBridge que será usada por el WebView
    // Es importante que sea la misma instancia a la que se le pasa el WebView
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
                        // Considerar recargar el webview aquí o si el reset lo maneja
                    } else {
                        viewModel.reportError("Sin conexión a internet")
                    }
                }
            )
        } else {
            // Recordar la instancia del WebView para poder pasársela al TokenBridge
            var webViewInstance: WebView? = null

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    Log.d("WebViewScreen_Factory", "Creando instancia de WebView.")
                    WebView(ctx).apply {
                        webViewInstance = this // Guardar la referencia
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = true // Revisa si es necesario
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true

                        // Configurar WebChromeClient para capturar logs de JS
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
                        addJavascriptInterface(tokenBridge, "Android") // Usar la instancia recordada
                        Log.d("WebViewScreen_Setup", "TokenBridge debería estar inyectado.")

                        // Pasar la instancia del WebView al TokenBridge
                        // Esto se podría hacer aquí o en el bloque 'update' si fuera necesario
                        // pero como tokenBridge se crea una vez, hacerlo aquí está bien.
                        tokenBridge.setWebView(this)
                        Log.d("WebViewScreen_Setup", "Instancia de WebView pasada a TokenBridge.")


                        webViewClient = buildCentinaiWebClient(
                            context = ctx,
                            viewModel = viewModel
                            // Ya no necesitamos pasar tokenBridge aquí si no se usa en WebClientBuilder
                        )

                        Log.d("WebViewScreen_Setup", "🌐 Intentando cargar URL: $startUrl")
                        if (isConnected(ctx)) { // Verificar conexión antes de cargar
                            loadUrl(startUrl)
                        } else {
                            viewModel.reportError("Sin conexión a internet al intentar cargar URL")
                        }
                    }
                },
                update = { wv ->
                    // El bloque update se llama si algo en el estado de Compose que afecta a AndroidView cambia.
                    // Podríamos re-setear el webview en el tokenBridge si fuera necesario,
                    // pero con remember debería ser la misma instancia de tokenBridge.
                    // tokenBridge.setWebView(wv)
                    // Log.d("WebViewScreen_Update", "Bloque Update llamado.")
                }
            )

            if (!viewModel.isWebLoaded && !viewModel.hasError) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    // Limpiar la referencia al WebView en TokenBridge cuando la Composable se va
    DisposableEffect(Unit) {
        onDispose {
            Log.d("WebViewScreen", "DisposableEffect: Limpiando referencia de WebView en TokenBridge.")
            // tokenBridge.clearWebViewReference() // Necesitarías añadir este método en TokenBridge
        }
    }
}