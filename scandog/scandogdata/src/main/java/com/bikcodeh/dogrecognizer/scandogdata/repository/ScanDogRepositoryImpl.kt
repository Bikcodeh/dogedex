package com.bikcodeh.dogrecognizer.scandogdata.repository

import com.bikcodeh.dogrecognizer.core.common.Result
import com.bikcodeh.dogrecognizer.core.common.makeSafeRequest
import com.bikcodeh.dogrecognizer.core.remote.DogApiService
import com.bikcodeh.dogrecognizer.core.remote.dto.DogApiResponse
import com.bikcodeh.dogrecognizer.scandogdomain.repository.ScanDogRepository
import javax.inject.Inject

class ScanDogRepositoryImpl @Inject constructor(
    private val dogApiService: DogApiService
): ScanDogRepository {

    override suspend fun getRecognizedDog(mlDogId: String): Result<DogApiResponse> {
        return makeSafeRequest { dogApiService.getDogByMlId(mlDogId) }
    }
}