package com.centinai.app.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class WebLoadViewModel : ViewModel() {
    var isWebLoaded by mutableStateOf(false)
        private set

    var hasError by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun markAsLoaded() {
        isWebLoaded = true
    }

    fun reportError(message: String) {
        hasError = true
        errorMessage = message
    }

    fun reset() {
        isWebLoaded = false
        hasError = false
        errorMessage = null
    }
}
