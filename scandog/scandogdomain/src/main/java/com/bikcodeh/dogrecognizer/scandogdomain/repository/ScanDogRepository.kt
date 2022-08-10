package com.bikcodeh.dogrecognizer.scandogdomain.repository

import com.bikcodeh.dogrecognizer.core.common.Result
import com.bikcodeh.dogrecognizer.core.remote.dto.DogApiResponse

interface ScanDogRepository {
    suspend fun getRecognizedDog(mlDogId: String): Result<DogApiResponse>
}