package com.bikcodeh.dogrecognizer.dogsdata.repository

import com.bikcodeh.dogrecognizer.core_common.Result
import com.bikcodeh.dogrecognizer.core_model.dto.AddDogToUserDTO
import com.bikcodeh.dogrecognizer.core_model.dto.DogDTO
import com.bikcodeh.dogrecognizer.core_model.response.DefaultResponse
import com.bikcodeh.dogrecognizer.core_model.response.DogListApiResponse
import com.bikcodeh.dogrecognizer.core_model.response.DogListResponse
import com.bikcodeh.dogrecognizer.core_network.retrofit.service.DogApiService
import com.bikcodeh.dogrecognizer.core_testing.util.MainDispatcherRule
import com.bikcodeh.dogrecognizer.dogsdomain.repository.DogRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class DogRepositoryImplTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var dogApiService: DogApiService

    lateinit var dogRepository: DogRepository

    @Before
    fun setUp() {
        dogRepository = DogRepositoryImpl(dogApiService)
    }

    @Test
    fun downloadDogs() = runTest {
        val response: Response<DogListApiResponse> = mockk()
        every { response.isSuccessful } returns true
        every { response.code() } returns 200
        every { response.body() } returns DogListApiResponse(
            message = "OK",
            isSuccess = true,
            data = DogListResponse(
                dogs = listOf(
                    DogDTO(
                        id = 0,
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
                    )
                )
            )
        )
        coEvery { dogApiService.getAllDogs() } returns response

        val result = dogRepository.downloadDogs()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data.isNotEmpty()).isTrue()
        assertThat((result).data.count()).isEqualTo(1)
        assertThat((result).data.first().name).isEqualTo("test")
        coVerify(exactly = 1) { dogApiService.getAllDogs() }
    }

    @Test
    fun `downloadDogs should return an empty dog list`() = runTest {
        val response: Response<DogListApiResponse> = mockk()
        every { response.isSuccessful } returns true
        every { response.code() } returns 200
        every { response.body() } returns DogListApiResponse(
            message = "OK",
            isSuccess = true,
            data = DogListResponse(
                dogs = emptyList()
            )
        )
        coEvery { dogApiService.getAllDogs() } returns response

        val result = dogRepository.downloadDogs()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data.isEmpty()).isTrue()
        assertThat((result).data.count()).isEqualTo(0)
        coVerify(exactly = 1) { dogApiService.getAllDogs() }
    }

    @Test
    fun `downloadDogs should return an error result`() = runTest {
        val response: Response<DogListApiResponse> = mockk()
        every { response.isSuccessful } returns false
        every { response.code() } returns 404
        every { response.body() } returns null
        every { response.message() } returns "error"
        coEvery { dogApiService.getAllDogs() } returns response

        val result = dogRepository.downloadDogs()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).code).isEqualTo(404)
        assertThat((result).message).isEqualTo("error")
        verifyAll {
            response.code()
            response.isSuccessful
            response.body()
            response.message()
        }
        coVerify(exactly = 1) { dogApiService.getAllDogs() }
    }

    @Test
    fun `downloadDogs should return an exception result`() = runTest {
        coEvery { dogApiService.getAllDogs() } throws Exception("testError")

        val result = dogRepository.downloadDogs()

        assertThat(result).isInstanceOf(Result.Exception::class.java)
        assertThat((result as Result.Exception).exception.message).isEqualTo("testError")
        coVerify(exactly = 1) { dogApiService.getAllDogs() }
    }

    @Test
    fun addDogToUser() = runTest {
        val addDogToUserDTO = AddDogToUserDTO(dogId = "1")
        val response: Response<DefaultResponse> = mockk()
        every { response.isSuccessful } returns true
        every { response.code() } returns 200
        every { response.body() } returns DefaultResponse(
            message = "OK",
            isSuccess = true
        )
        coEvery {
            dogApiService.addDogToUser(addDogToUserDTO)
        } returns response

        dogRepository.addDogToUser("1")

        coVerify { dogApiService.addDogToUser(addDogToUserDTO) }
    }
}