package com.bikcodeh.dogrecognizer.data.remote

import com.bikcodeh.dogrecognizer.data.remote.dto.auth.AuthApiResponse
import com.bikcodeh.dogrecognizer.data.remote.dto.auth.LogInDTO
import com.bikcodeh.dogrecognizer.data.remote.dto.auth.SignUpDTO
import com.bikcodeh.dogrecognizer.data.remote.dto.doglist.DogListApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DogApiService {

    @GET("dogs")
    suspend fun getAllDogs(): Response<DogListApiResponse>

    @POST("sign_up")
    suspend fun signUp(@Body signUpDTO: SignUpDTO): Response<AuthApiResponse>

    @POST("sign_in")
    suspend fun logIn(@Body logInDTO: LogInDTO): Response<AuthApiResponse>
}