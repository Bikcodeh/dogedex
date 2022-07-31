package com.bikcodeh.dogrecognizer.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bikcodeh.dogrecognizer.domain.model.User
import com.bikcodeh.dogrecognizer.domain.repository.DataStoreOperations
import com.bikcodeh.dogrecognizer.presentation.util.Constants.PREFERENCES_NAME
import com.bikcodeh.dogrecognizer.presentation.util.Constants.USER_EMAIL_PREFERENCES_KEY
import com.bikcodeh.dogrecognizer.presentation.util.Constants.USER_ID_PREFERENCES_KEY
import com.bikcodeh.dogrecognizer.presentation.util.Constants.USER_TOKEN_PREFERENCES_KEY
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class DataStoreOperationsImpl @Inject constructor(
    @ApplicationContext context: Context
) : DataStoreOperations {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val id = stringPreferencesKey(USER_ID_PREFERENCES_KEY)
        val email = stringPreferencesKey(USER_EMAIL_PREFERENCES_KEY)
        val token = stringPreferencesKey(USER_TOKEN_PREFERENCES_KEY)
    }

    override suspend fun saveUser(id: Long, email: String, token: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.id] = id.toString()
            preferences[PreferencesKeys.email] = email
            preferences[PreferencesKeys.token] = token
        }
    }

    override fun getUser(): Flow<User> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences: Preferences ->
            val user = User(
                preferences[PreferencesKeys.id]?.toLong() ?: -1L,
                preferences[PreferencesKeys.email] ?: "",
                preferences[PreferencesKeys.token] ?: "",
            )
            user
        }
    }
}