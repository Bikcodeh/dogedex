package com.bikcodeh.dogrecognizer.data.repository

import com.bikcodeh.dogrecognizer.data.remote.DogApiService
import com.bikcodeh.dogrecognizer.data.remote.dto.DefaultResponse
import com.bikcodeh.dogrecognizer.data.remote.dto.user.AddDogToUserDTO
import com.bikcodeh.dogrecognizer.domain.model.Dog
import com.bikcodeh.dogrecognizer.domain.common.Result
import com.bikcodeh.dogrecognizer.domain.common.fold
import com.bikcodeh.dogrecognizer.domain.common.makeSafeRequest
import com.bikcodeh.dogrecognizer.domain.repository.DogRepository
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

    override suspend fun addDogToUser(dogId: String): Result<DefaultResponse> {
        return makeSafeRequest {
            val addDogToUserDTO = AddDogToUserDTO(dogId)
            dogApiService.addDogToUser(addDogToUserDTO)
        }
    }

    override suspend fun getUserDogs(): Result<List<Dog>> {
        val response = makeSafeRequest {
            dogApiService.getUserDogs()
        }

        return response.fold(
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