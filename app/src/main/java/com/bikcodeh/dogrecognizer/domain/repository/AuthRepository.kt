package com.bikcodeh.dogrecognizer.domain.repository

import com.bikcodeh.dogrecognizer.domain.model.User
import com.bikcodeh.dogrecognizer.domain.model.common.Result

interface AuthRepository {
    suspend fun signUp(
        email: String,
        password: String,
        confirmPassword: String
    ): Result<User>
}