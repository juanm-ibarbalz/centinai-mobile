package com.centinai.app.utils

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.core.content.edit
import android.widget.Toast

class TokenBridge(private val context: Context) {

    // Obtenemos la instancia de TokenManager aquí.
    // Se usará el applicationContext gracias a la implementación de getInstance
    private val tokenManager = TokenManager.getInstance(context)

    @JavascriptInterface
    fun saveToken(token: String?) {
        if (token != null && token.isNotEmpty()) {
            tokenManager.saveToken(token) // Llama al método de la instancia
            println("CentinaiApp: Token guardado nativamente: $token")
        } else {
            tokenManager.clearToken() // Llama al método de la instancia
            println("CentinaiApp: Token recibido nulo o vacío, limpiando token nativo.")
        }
    }

    @JavascriptInterface
    fun getToken(): String? {
        val token = tokenManager.getToken() // Llama al método de la instancia
        println("CentinaiApp: Web solicitó token, devolviendo: $token")
        return token
    }

    @JavascriptInterface
    fun clearToken() {
        tokenManager.clearToken() // Llama al método de la instancia
        println("CentinaiApp: Web solicitó limpiar token.")
    }

    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

/*
class TokenBridge(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    @JavascriptInterface
    fun saveToken(token: String) {
        Log.d("TokenBridge", "💾 Token recibido: $token")
        sharedPreferences
            .edit {
                putString("jwt", token)
            }
    }

    @JavascriptInterface
    fun logoutToken() {
        sharedPreferences
            .edit {
                remove("jwt")
            }
    }
}
*/
