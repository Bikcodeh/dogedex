package com.bikcodeh.dogrecognizer.dogsdomain.repository

import com.bikcodeh.dogrecognizer.core.common.Result
import com.bikcodeh.dogrecognizer.core.model.Dog
import com.bikcodeh.dogrecognizer.core.remote.dto.DefaultResponse
import com.bikcodeh.dogrecognizer.core.remote.dto.DogApiResponse

interface DogRepository {
    suspend fun downloadDogs(): Result<List<Dog>>
    suspend fun addDogToUser(dogId: String): Result<DefaultResponse>
    suspend fun getRecognizedDog(mlDogId: String): Result<DogApiResponse>
}