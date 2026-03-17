package com.scrapeverything.app.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        try {
            createEncryptedPrefs()
        } catch (e: Exception) {
            Log.e("TokenStorage", "EncryptedSharedPreferences 손상, 재생성합니다", e)
            context.deleteSharedPreferences(PREFS_NAME)
            createEncryptedPrefs()
        }
    }

    private fun createEncryptedPrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveJwtToken(token: String) {
        prefs.edit().putString(KEY_JWT, token).apply()
    }

    fun getJwtToken(): String? {
        return prefs.getString(KEY_JWT, null)
    }

    fun saveRefreshToken(token: String) {
        prefs.edit().putString(KEY_REFRESH, token).apply()
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH, null)
    }

    fun setKeepLoggedIn(keep: Boolean) {
        prefs.edit().putBoolean(KEY_KEEP_LOGGED_IN, keep).apply()
    }

    fun isKeepLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_KEEP_LOGGED_IN, false)
    }

    fun clearTokens() {
        prefs.edit()
            .remove(KEY_JWT)
            .remove(KEY_REFRESH)
            .remove(KEY_KEEP_LOGGED_IN)
            .apply()
    }

    fun hasTokens(): Boolean {
        return getJwtToken() != null && getRefreshToken() != null
    }

    companion object {
        private const val PREFS_NAME = "scrap_everything_prefs"
        private const val KEY_JWT = "jwt_token"
        private const val KEY_REFRESH = "refresh_token"
        private const val KEY_KEEP_LOGGED_IN = "keep_logged_in"
    }
}
