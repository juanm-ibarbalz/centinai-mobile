package com.centinai.app.utils // O el paquete donde esté tu archivo

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.centinai.app.viewmodel.WebLoadViewModel

fun buildCentinaiWebClient(
    context: Context, // El contexto pasado desde WebViewScreen
    viewModel: WebLoadViewModel
): WebViewClient {
    return object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.d("CentinaiApp_WebClient", "🌐 onPageStarted para URL: $url")
            // Puedes considerar si viewModel.reset() o alguna indicación de "cargando"
            // debe ir aquí si la página se recarga o navega internamente.
            // Por ahora, asumimos que isWebLoaded se maneja principalmente para la carga inicial.
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.d("CentinaiApp_WebClient", "✅ onPageFinished para URL: $url")

            // YA NO SE INYECTA EL TOKEN DIRECTAMENTE DESDE AQUÍ.
            // Esta responsabilidad ahora la tiene TokenBridge cuando JavaScript llame a
            // window.Android.webViewReadyForToken().

            // Lo único que hacemos aquí es marcar que la carga de la página por parte del WebView ha finalizado.
            // La aplicación web (React) es la que determinará cuándo está realmente lista
            // para interactuar o recibir datos adicionales.
            viewModel.markAsLoaded()
            Log.d("CentinaiApp_WebClient", "✅ viewModel.markAsLoaded() llamado desde onPageFinished.")
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            // Es importante verificar si el error es para la URL principal
            // para no reportar errores de recursos secundarios como errores de carga de página completa.
            if (request?.isForMainFrame == true) {
                val errorMessage = error?.description?.toString() ?: "Error desconocido al cargar la página principal"
                val failingUrl = request.url?.toString() ?: "URL desconocida"
                Log.e(
                    "CentinaiApp_WebClient",
                    "❌ Error en Main Frame ($failingUrl): $errorMessage (Code: ${error?.errorCode})"
                )
                viewModel.reportError(errorMessage)
            } else {
                // Loguear errores de sub-recursos pero no necesariamente tratarlos como un error de carga de página
                val failingUrl = request?.url?.toString() ?: "URL de sub-recurso desconocida"
                Log.w(
                    "CentinaiApp_WebClient",
                    "⚠️ Error en sub-recurso ($failingUrl): ${error?.description} (Code: ${error?.errorCode})"
                )
            }
        }
    }
}