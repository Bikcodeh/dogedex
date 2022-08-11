package com.bikcodeh.dogrecognizer.scandogdata.repository

import com.bikcodeh.dogrecognizer.core_common.Result
import com.bikcodeh.dogrecognizer.core_common.makeSafeRequest
import com.bikcodeh.dogrecognizer.core_model.response.DogApiResponse
import com.bikcodeh.dogrecognizer.core_network.retrofit.service.DogApiService
import com.bikcodeh.dogrecognizer.scandogdomain.repository.ScanDogRepository
import javax.inject.Inject

class ScanDogRepositoryImpl @Inject constructor(
    private val dogApiService: DogApiService
): ScanDogRepository {

    override suspend fun getRecognizedDog(mlDogId: String): Result<DogApiResponse> {
        return makeSafeRequest { dogApiService.getDogByMlId(mlDogId) }
    }
}