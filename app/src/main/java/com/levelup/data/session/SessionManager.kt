package com.levelup.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("levelup_prefs")

@Singleton
class SessionManager @Inject constructor(private val context: Context) {

    companion object {
        val KEY_USER_ID = stringPreferencesKey("key_user_id")
        val KEY_AUTH_TOKEN = stringPreferencesKey("key_auth_token")
    }

    suspend fun saveSession(userId: Long, token: String) {
        android.util.Log.d(
                "SessionManager",
                "Saving session: userId=$userId, token=${token.take(10)}..."
        )
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId.toString()
            prefs[KEY_AUTH_TOKEN] = token
        }
    }

    suspend fun clearSession() {
        android.util.Log.d("SessionManager", "Clearing session")
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_AUTH_TOKEN)
        }
    }

    val userIdFlow: Flow<Long?> =
            context.dataStore.data.map { prefs -> prefs[KEY_USER_ID]?.toLongOrNull() }

    val authTokenFlow: Flow<String?> = context.dataStore.data.map { prefs -> prefs[KEY_AUTH_TOKEN] }
}
