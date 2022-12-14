package com.bikcodeh.dogrecognizer.authdomain.repository

import com.bikcodeh.dogrecognizer.core_model.User
import com.bikcodeh.dogrecognizer.core_common.Result

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