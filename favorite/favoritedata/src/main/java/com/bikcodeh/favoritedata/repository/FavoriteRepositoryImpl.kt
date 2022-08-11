package com.bikcodeh.favoritedata.repository

import com.bikcodeh.dogrecognizer.core_common.makeSafeRequest
import com.bikcodeh.dogrecognizer.core_common.Result
import com.bikcodeh.dogrecognizer.core_common.fold
import com.bikcodeh.dogrecognizer.core_model.Dog
import com.bikcodeh.dogrecognizer.core_network.retrofit.service.DogApiService
import com.bikcodeh.favoritedomain.repository.FavoriteRepository
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val dogApiService: DogApiService
): FavoriteRepository {

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