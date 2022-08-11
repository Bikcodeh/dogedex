package com.bikcodeh.dogrecognizer.dogsdomain.repository

import com.bikcodeh.dogrecognizer.core_model.Dog
import com.bikcodeh.dogrecognizer.core_model.response.DefaultResponse
import com.bikcodeh.dogrecognizer.core_model.response.DogApiResponse
import com.bikcodeh.dogrecognizer.core_common.Result

interface DogRepository {
    suspend fun downloadDogs(): Result<List<Dog>>
    suspend fun addDogToUser(dogId: String): Result<DefaultResponse>
}