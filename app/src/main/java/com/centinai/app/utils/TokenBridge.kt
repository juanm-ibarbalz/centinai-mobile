package com.centinai.app.utils

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.core.content.edit

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