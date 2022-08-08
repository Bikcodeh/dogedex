package com.bikcodeh.dogrecognizer.domain.repository

import com.bikcodeh.dogrecognizer.core.model.User
import kotlinx.coroutines.flow.Flow

interface DataStoreOperations {

    suspend fun saveUser(id: Long, email: String, token: String)
    fun getUser(): Flow<User>
    suspend fun deleteUser()
}