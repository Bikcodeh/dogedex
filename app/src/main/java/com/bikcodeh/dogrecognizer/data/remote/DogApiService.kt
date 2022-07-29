package com.bikcodeh.dogrecognizer.data.remote

import com.bikcodeh.dogrecognizer.data.remote.dto.doglist.DogListApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface DogApiService {

    @GET("dogs")
    suspend fun getAllDogs(): Response<DogListApiResponse>
}