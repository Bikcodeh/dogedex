package com.bikcodeh.dogrecognizer.core_network.retrofit.service

import com.bikcodeh.dogrecognizer.core_common.interceptor.ApiServiceInterceptor
import com.bikcodeh.dogrecognizer.core_model.dto.AddDogToUserDTO
import com.bikcodeh.dogrecognizer.core_model.dto.LogInDTO
import com.bikcodeh.dogrecognizer.core_model.dto.SignUpDTO
import com.bikcodeh.dogrecognizer.core_model.response.AuthApiResponse
import com.bikcodeh.dogrecognizer.core_model.response.DefaultResponse
import com.bikcodeh.dogrecognizer.core_model.response.DogApiResponse
import com.bikcodeh.dogrecognizer.core_model.response.DogListApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Query

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