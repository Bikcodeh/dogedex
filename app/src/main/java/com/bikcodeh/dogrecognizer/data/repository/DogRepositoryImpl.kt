package com.bikcodeh.dogrecognizer.data.repository

import com.bikcodeh.dogrecognizer.data.remote.DogApiService
import com.bikcodeh.dogrecognizer.data.remote.dto.doglist.DogListApiResponse
import com.bikcodeh.dogrecognizer.domain.model.Dog
import com.bikcodeh.dogrecognizer.domain.model.common.Result
import com.bikcodeh.dogrecognizer.domain.model.common.fold
import com.bikcodeh.dogrecognizer.domain.model.common.makeSafeRequest
import com.bikcodeh.dogrecognizer.domain.repository.DogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DogRepositoryImpl @Inject constructor(
    private val dogApiService: DogApiService
) : DogRepository {

    override suspend fun downloadDogs(): Result<List<Dog>> {
        val data = makeSafeRequest {
            dogApiService.getAllDogs()
        }
        return data.fold(
            onSuccess = {
                Result.Success(it.data.dogs.map { dogDTO -> dogDTO.toDomain() })
            },
            onError = { code, message ->
                Result.Error(code, message)
            },
            onException = {
                Result.Exception(it)
            }
        )
    }
}