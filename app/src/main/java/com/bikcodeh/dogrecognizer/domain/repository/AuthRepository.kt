package com.bikcodeh.dogrecognizer.domain.repository

import com.bikcodeh.dogrecognizer.core.model.User
import com.bikcodeh.dogrecognizer.core.common.Result

interface AuthRepository {
    suspend fun signUp(
        email: String,
        password: String,
        confirmPassword: String
    ): Result<User>

    suspend fun signIn(
        email: String,
        password: String
    ): Result<User>
}