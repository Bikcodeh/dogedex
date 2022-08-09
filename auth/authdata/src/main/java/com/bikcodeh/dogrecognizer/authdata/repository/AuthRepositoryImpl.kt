package com.bikcodeh.dogrecognizer.authdata.repository

import com.bikcodeh.dogrecognizer.authdomain.repository.AuthRepository
import com.bikcodeh.dogrecognizer.core.remote.DogApiService
import com.bikcodeh.dogrecognizer.core.remote.dto.auth.LogInDTO
import com.bikcodeh.dogrecognizer.core.remote.dto.auth.SignUpDTO
import com.bikcodeh.dogrecognizer.core.model.User
import com.bikcodeh.dogrecognizer.core.common.Result
import com.bikcodeh.dogrecognizer.core.common.fold
import com.bikcodeh.dogrecognizer.core.common.makeSafeRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.net.HttpURLConnection
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dogApiService: DogApiService
) : AuthRepository {

    override suspend fun signUp(
        email: String,
        password: String,
        confirmPassword: String
    ): Result<User> {
        val response = makeSafeRequest {
            val signUpDTO = SignUpDTO(email, password, confirmPassword)
            dogApiService.signUp(signUpDTO)
        }

        return response.fold(
            onSuccess = {
                if (it.isSuccess) {
                    Result.Success(it.data.user.toDomain())
                } else {
                    Result.Error(HttpURLConnection.HTTP_BAD_REQUEST, it.message)
                }
            },
            onError = { code, message ->
                Result.Error(code, message)
            },
            onException = {
                Result.Exception(it)
            }
        )
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        val response = makeSafeRequest {
            val logInDTO = LogInDTO(email, password)
            dogApiService.logIn(logInDTO)
        }

        return response.fold(
            onSuccess = {
                if (it.isSuccess) {
                    Result.Success(it.data.user.toDomain())
                } else {
                    Result.Exception(
                        HttpException(
                            Response.error<ResponseBody>(
                                HttpURLConnection.HTTP_UNAUTHORIZED,
                                "".toResponseBody("plain/text".toMediaTypeOrNull())
                            )
                        )
                    )
                }
            },
            onError = { code, message ->
                Result.Error(code, message)
            },
            onException = {
                Result.Exception(it)
            }
        )
    }
}