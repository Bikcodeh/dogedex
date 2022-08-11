package com.bikcodeh.dogrecognizer.dogsdata.repository

import com.bikcodeh.dogrecognizer.core_common.Result
import com.bikcodeh.dogrecognizer.core_common.fold
import com.bikcodeh.dogrecognizer.core_common.makeSafeRequest
import com.bikcodeh.dogrecognizer.core_model.Dog
import com.bikcodeh.dogrecognizer.core_model.dto.AddDogToUserDTO
import com.bikcodeh.dogrecognizer.core_model.response.DefaultResponse
import com.bikcodeh.dogrecognizer.core_network.retrofit.service.DogApiService
import com.bikcodeh.dogrecognizer.dogsdomain.repository.DogRepository
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
}