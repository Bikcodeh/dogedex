package com.bikcodeh.dogrecognizer.data.remote

import com.bikcodeh.dogrecognizer.data.remote.dto.DefaultResponse
import com.bikcodeh.dogrecognizer.data.remote.dto.DogApiResponse
import com.bikcodeh.dogrecognizer.data.remote.dto.auth.AuthApiResponse
import com.bikcodeh.dogrecognizer.data.remote.dto.auth.LogInDTO
import com.bikcodeh.dogrecognizer.data.remote.dto.auth.SignUpDTO
import com.bikcodeh.dogrecognizer.data.remote.dto.doglist.DogListApiResponse
import com.bikcodeh.dogrecognizer.data.remote.dto.user.AddDogToUserDTO
import com.bikcodeh.dogrecognizer.data.remote.interceptor.ApiServiceInterceptor
import retrofit2.Response
import retrofit2.http.*

interface DogApiService {

    @GET("dogs")
    suspend fun getAllDogs(): Response<DogListApiResponse>

    @POST("sign_up")
    suspend fun signUp(@Body signUpDTO: SignUpDTO): Response<AuthApiResponse>

    @POST("sign_in")
    suspend fun logIn(@Body logInDTO: LogInDTO): Response<AuthApiResponse>

    @Headers("${ApiServiceInterceptor.NEEDS_AUTH_HEADER_KEY}: true")
    @POST("add_dog_to_user")
    suspend fun addDogToUser(@Body addDogToUserDTO: AddDogToUserDTO): Response<DefaultResponse>

    @Headers("${ApiServiceInterceptor.NEEDS_AUTH_HEADER_KEY}: true")
    @GET("get_user_dogs")
    suspend fun getUserDogs(): Response<DogListApiResponse>

    @GET("find_dog_by_ml_id")
    suspend fun getDogByMlId(@Query("ml_id") mlId: String): Response<DogApiResponse>

}