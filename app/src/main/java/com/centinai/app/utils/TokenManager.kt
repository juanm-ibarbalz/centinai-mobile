package com.centinai.app.utils
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenManager private constructor(private val appContext: Context) {

    companion object {
        private const val PREF_NAME = "centinai_secure_prefs"
        private const val TOKEN_KEY = "auth_token"

        @Volatile
        private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                val instance = TokenManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    private fun getEncryptedSharedPreferences(): EncryptedSharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKeyAlias,
            appContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    fun saveToken(token: String) {
        val editor = getEncryptedSharedPreferences().edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun getToken(): String? {
        return getEncryptedSharedPreferences().getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        val editor = getEncryptedSharedPreferences().edit()
        editor.remove(TOKEN_KEY)
        editor.apply()
    }
}