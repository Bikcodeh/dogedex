package com.bikcodeh.favoritedomain.repository

import com.bikcodeh.dogrecognizer.core_common.Result
import com.bikcodeh.dogrecognizer.core_model.Dog

interface FavoriteRepository {
    suspend fun getUserDogs(): Result<List<Dog>>
}