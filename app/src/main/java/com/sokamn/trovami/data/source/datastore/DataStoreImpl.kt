package com.sokamn.trovami.data.source.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sokamn.trovami.utils.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = DataStoreConstants.DATA_PREFERENCES)

class DataStoreImpl @Inject constructor(
    private val context: Context
) : DataStore {
    override suspend fun getCurrentUser(): Flow<String> {
        return context.dataStore.data.catch {
            emit(emptyPreferences())
        }.map {
            it[currentUserKey] ?: "{}"
        }
    }

    override suspend fun saveCurrentUser(user: String) {
        context.dataStore.edit { preferences ->
            preferences[currentUserKey] = user
        }
    }

    override suspend fun getCurrentPicture(): Flow<String> {
        return context.dataStore.data.catch {
            emit(emptyPreferences())
        }.map {
            it[currentPictureKey] ?: ""
        }
    }

    override suspend fun saveCurrentPicture(currentPicture: String) {
        context.dataStore.edit { preferences ->
            preferences[currentPictureKey] = currentPicture
        }
    }

    override suspend fun saveCurrentProfileUID(uid: String) {
        context.dataStore.edit { preferences ->
            preferences[currentProfileUID] = uid
        }
    }

    override suspend fun getCurrentProfileUID(): Flow<String> {
        return context.dataStore.data.catch {
            emit(emptyPreferences())
        }.map {
            it[currentProfileUID] ?: "currentUser"
        }
    }

    override suspend fun clearAllPreferences() {
        context.dataStore.edit {
            it.clear()
        }
    }

    companion object{
        val currentPictureKey = stringPreferencesKey(DataStoreConstants.PROFILE_PICTURE_KEY_PREFS)
        val currentUserKey = stringPreferencesKey(DataStoreConstants.USER_KEY_PREFS)
        val currentProfileUID = stringPreferencesKey(DataStoreConstants.CURRENT_PROFILE_KEY_PREFS)
        val currentUserUID = stringPreferencesKey(DataStoreConstants.CURRENT_USER_UID_KEY_PREFS)
    }
}