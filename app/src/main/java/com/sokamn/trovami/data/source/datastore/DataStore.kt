package com.sokamn.trovami.data.source.datastore

import kotlinx.coroutines.flow.Flow

interface DataStore {
    suspend fun getCurrentUser() : Flow<String>

    suspend fun saveCurrentUser(user: String)

    suspend fun getCurrentPicture() : Flow<String>

    suspend fun saveCurrentPicture(currentPicture: String)

    suspend fun saveCurrentProfileUID(uid: String)

    suspend fun getCurrentProfileUID() : Flow<String>

    suspend fun clearAllPreferences()
}