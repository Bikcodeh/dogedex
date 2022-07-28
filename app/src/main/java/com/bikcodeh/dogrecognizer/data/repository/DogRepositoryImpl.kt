package com.bikcodeh.dogrecognizer.data.repository

import com.bikcodeh.dogrecognizer.data.remote.DogApiService
import com.bikcodeh.dogrecognizer.domain.model.Dog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DogRepositoryImpl @Inject constructor(
    private val dogApiService: DogApiService
) {

    suspend fun downloadDogs(): List<Dog> {
        return withContext(Dispatchers.IO) {
            val response = dogApiService.getAllDogs()
            response.data.dogs
        }
    }
}