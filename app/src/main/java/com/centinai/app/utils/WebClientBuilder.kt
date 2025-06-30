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
    context: Context,
    viewModel: WebLoadViewModel
): WebViewClient {
    return object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.d("CentinaiApp_WebClient", "🌐 onPageStarted para URL: $url")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.d("CentinaiApp_WebClient", "✅ onPageFinished para URL: $url")
            viewModel.markAsLoaded()
            Log.d("CentinaiApp_WebClient", "✅ viewModel.markAsLoaded() llamado desde onPageFinished.")
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            if (request?.isForMainFrame == true) {
                val errorMessage = error?.description?.toString() ?: "Error desconocido al cargar la página principal"
                val failingUrl = request.url?.toString() ?: "URL desconocida"
                Log.e(
                    "CentinaiApp_WebClient",
                    "❌ Error en Main Frame ($failingUrl): $errorMessage (Code: ${error?.errorCode})"
                )
                viewModel.reportError(errorMessage)
            } else {
                val failingUrl = request?.url?.toString() ?: "URL de sub-recurso desconocida"
                Log.w(
                    "CentinaiApp_WebClient",
                    "⚠️ Error en sub-recurso ($failingUrl): ${error?.description} (Code: ${error?.errorCode})"
                )
            }
        }
    }
}