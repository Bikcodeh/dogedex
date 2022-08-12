package com.bikcodeh.favoritedata.repository

import com.bikcodeh.dogrecognizer.core_common.Result
import com.bikcodeh.dogrecognizer.core_model.Dog
import com.bikcodeh.dogrecognizer.core_model.dto.DogDTO
import com.bikcodeh.dogrecognizer.core_model.response.DogListApiResponse
import com.bikcodeh.dogrecognizer.core_model.response.DogListResponse
import com.bikcodeh.dogrecognizer.core_network.retrofit.service.DogApiService
import com.bikcodeh.dogrecognizer.core_testing.util.MainDispatcherRule
import com.bikcodeh.favoritedomain.repository.FavoriteRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteRepositoryImplTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    lateinit var favoriteRepository: FavoriteRepository

    @RelaxedMockK
    lateinit var dogApiService: DogApiService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        favoriteRepository = FavoriteRepositoryImpl(
            dogApiService
        )
    }

    @Test
    fun getUserDogsEmptyList() = runTest {
        val response: Response<DogListApiResponse> = mockk()
        val expected = DogListApiResponse(
            message = "OK",
            isSuccess = true,
            DogListResponse(
                listOf()
            )
        )
        every { response.isSuccessful } returns true
        every { response.code() } returns 200
        every { response.body() } returns expected

        coEvery { dogApiService.getUserDogs() } returns response

        val result = favoriteRepository.getUserDogs()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(emptyList<Dog>())
        coVerify(exactly = 1) { dogApiService.getUserDogs() }
    }

    @Test
    fun getUserDogs() = runTest {
        val response: Response<DogListApiResponse> = mockk()
        val expected = DogListApiResponse(
            message = "OK",
            isSuccess = true,
            DogListResponse(
                listOf(DogDTO(
                    id = 1,
                    index = 0,
                    name = "test",
                    type = "",
                    heightFemale = "",
                    heightMale = "",
                    imageUrl = "",
                    lifeExpectancy = "",
                    temperament = "",
                    weightFemale = "",
                    weightMale = ""

                ))
            )
        )
        every { response.isSuccessful } returns true
        every { response.code() } returns 200
        every { response.body() } returns expected

        coEvery { dogApiService.getUserDogs() } returns response

        val result = favoriteRepository.getUserDogs()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data.count()).isEqualTo(1)
        assertThat((result).data.first().name).isEqualTo("test")
        coVerify(exactly = 1) { dogApiService.getUserDogs() }
    }
}