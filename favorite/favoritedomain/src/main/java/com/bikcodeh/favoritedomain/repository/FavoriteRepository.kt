package com.bikcodeh.favoritedomain.repository

import com.bikcodeh.dogrecognizer.core.common.Result
import com.bikcodeh.dogrecognizer.core.model.Dog

interface FavoriteRepository {
    suspend fun getUserDogs(): Result<List<Dog>>
}