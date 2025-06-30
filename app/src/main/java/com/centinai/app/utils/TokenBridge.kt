package com.centinai.app.utils

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast

class TokenBridge(private val appContext: Context) {

    private val tokenManager = TokenManager.getInstance(appContext)
    private var webViewInstance: WebView? = null
    private var pendingTokenToInject: String? = null
    private var isWebViewReadyForToken = false

    // Método para que el WebView (o quien lo gestione) registre la instancia del WebView
    fun setWebView(webView: WebView) {
        Log.d("TokenBridge", "setWebView llamado. WebView hasWindowFocus: ${webView.hasWindowFocus()}")
        this.webViewInstance = webView
        if (isWebViewReadyForToken && pendingTokenToInject != null) {
            injectPendingToken()
        }
    }

    private fun injectPendingToken() {
        pendingTokenToInject?.let { token ->
            webViewInstance?.let { wv ->
                val escapedToken = token.replace("\\", "\\\\").replace("'", "\\'")
                val script = "javascript:if(typeof window.handleNativeToken === 'function') { window.handleNativeToken('$escapedToken'); console.log('CentinaiApp (JS): handleNativeToken fue llamada con token (pendiente).'); } else { console.warn('CentinaiApp (JS WARN Late): window.handleNativeToken no está definido en la web.'); }"
                Log.d("TokenBridge", "Inyectando token pendiente a webView: $token")
                wv.post {
                    wv.evaluateJavascript(script, null)
                }
                pendingTokenToInject = null // Limpiar token pendiente
            } ?: Log.w("TokenBridge", "injectPendingToken: webViewInstance es null.")
        }
    }


    @JavascriptInterface
    fun saveToken(token: String?) {
        Log.d("TokenBridge", "saveToken llamado desde JS con token: ${if (token.isNullOrEmpty()) "null o vacío" else "presente"}")
        if (token != null && token.isNotEmpty()) {
            tokenManager.saveToken(token)
            Log.d("TokenBridge", "Token guardado nativamente vía TokenManager: $token")
        } else {
            tokenManager.clearToken()
            Log.d("TokenBridge", "Token recibido nulo o vacío en saveToken, limpiando token nativo.")
        }
    }

    @JavascriptInterface
    fun getToken(): String? {
        val token = tokenManager.getToken()
        Log.d("TokenBridge", "getToken llamado desde JS. Devolviendo: $token")
        return token
    }

    @JavascriptInterface
    fun clearToken() {
        Log.d("TokenBridge", "clearToken llamado desde JS.")
        tokenManager.clearToken()
        Log.d("TokenBridge", "Token limpiado nativamente vía TokenManager.")
    }

    @JavascriptInterface
    fun showToast(message: String) {
        Log.d("TokenBridge", "showToast llamado desde JS con mensaje: $message")
        webViewInstance?.post {
            Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
        }
    }

    @JavascriptInterface
    fun webViewReadyForToken() {
        Log.d("TokenBridge", "JS notificó webViewReadyForToken.")
        isWebViewReadyForToken = true
        val storedToken = tokenManager.getToken()
        Log.d("TokenBridge", "Token recuperado de TokenManager para webViewReady: $storedToken")

        if (storedToken != null) {
            if (webViewInstance != null) {
                val escapedToken = storedToken.replace("\\", "\\\\").replace("'", "\\'")
                val script = "javascript:if(typeof window.handleNativeToken === 'function') { window.handleNativeToken('$escapedToken'); console.log('CentinaiApp (JS): handleNativeToken fue llamada con token (vía webViewReady).'); } else { console.warn('CentinaiApp (JS WARN): window.handleNativeToken no definido al llamar desde webViewReady.'); }"
                Log.d("TokenBridge", "Inyectando token a webView (vía webViewReady): $storedToken")
                webViewInstance?.post {
                    webViewInstance?.evaluateJavascript(script, null)
                }
            } else {
                Log.w("TokenBridge", "webViewReadyForToken: webViewInstance es null. Guardando token como pendiente.")
                pendingTokenToInject = storedToken // Guardar para inyectar cuando setWebView sea llamado
            }
        } else {
            Log.d("TokenBridge", "webViewReadyForToken: No hay token almacenado para inyectar.")
        }
    }
}