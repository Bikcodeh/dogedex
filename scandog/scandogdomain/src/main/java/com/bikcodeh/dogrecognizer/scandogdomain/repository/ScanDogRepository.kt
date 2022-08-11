package com.bikcodeh.dogrecognizer.scandogdomain.repository

import com.bikcodeh.dogrecognizer.core_common.Result
import com.bikcodeh.dogrecognizer.core_model.response.DogApiResponse

interface ScanDogRepository {
    suspend fun getRecognizedDog(mlDogId: String): Result<DogApiResponse>
}