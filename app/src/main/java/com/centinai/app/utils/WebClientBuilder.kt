package com.centinai.app.utils

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
    viewModel: WebLoadViewModel,
): WebViewClient {
    return object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            Log.d("WebClient", "🌐 onPageStarted: $url")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            Log.d("WebClient", "✅ onPageFinished ejecutado con URL: $url")

            val token = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .getString("jwt", null)

            token?.let {
                val js = "window.localStorage.setItem('token', '$it');"
                view?.evaluateJavascript(js, null)
                Log.d("WebClient", "✅ Token injected into WebView.")
            } ?: Log.d("WebClient", "⚠️ No token found to inject.")

            viewModel.markAsLoaded()
            Log.d("WebClient", "✅ markAsLoaded llamado")
        }


        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            Log.e("WebClient", "❌ Error loading page: ${error?.description}")
            viewModel.reportError("Error al cargar la página")
        }
    }
}