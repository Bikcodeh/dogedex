package com.bikcodeh.dogrecognizer.domain.repository

import com.bikcodeh.dogrecognizer.domain.model.Dog
import com.bikcodeh.dogrecognizer.domain.common.Result

interface DogRepository {
    suspend fun downloadDogs(): Result<List<Dog>>
}