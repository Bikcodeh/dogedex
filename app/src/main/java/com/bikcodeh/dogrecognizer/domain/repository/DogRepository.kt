package com.bikcodeh.dogrecognizer.domain.repository

import com.bikcodeh.dogrecognizer.data.remote.dto.DefaultResponse
import com.bikcodeh.dogrecognizer.data.remote.dto.DogApiResponse
import com.bikcodeh.dogrecognizer.domain.common.Result
import com.bikcodeh.dogrecognizer.domain.model.Dog

interface DogRepository {
    suspend fun downloadDogs(): Result<List<Dog>>
    suspend fun addDogToUser(dogId: String): Result<DefaultResponse>
    suspend fun getUserDogs(): Result<List<Dog>>
    suspend fun getRecognizedDog(mlDogId: String): Result<DogApiResponse>
}