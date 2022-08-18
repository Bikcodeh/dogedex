package com.bikcodeh.dogrecognizer.scandogdata.repository

import com.bikcodeh.dogrecognizer.core_common.Result
import com.bikcodeh.dogrecognizer.core_model.dto.DogDTO
import com.bikcodeh.dogrecognizer.core_model.response.DogApiResponse
import com.bikcodeh.dogrecognizer.core_model.response.DogResponse
import com.bikcodeh.dogrecognizer.core_network.retrofit.service.DogApiService
import com.bikcodeh.dogrecognizer.core_testing.util.MainDispatcherRule
import com.bikcodeh.dogrecognizer.scandogdomain.repository.ScanDogRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
class ScanDogRepositoryImplTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var dogApiService: DogApiService

    private lateinit var scanDogRepository: ScanDogRepository

    @Before
    fun setUp() {
        scanDogRepository = ScanDogRepositoryImpl(dogApiService)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getRecognizedDog should return a success result`() = runTest {
        val response: Response<DogApiResponse> = mockk()
        every { response.isSuccessful } returns true
        every { response.body() } returns DogApiResponse(
            message = "OK", isSuccess = true, data = DogResponse(
                dog = DogDTO(
                    id = 1,
                    index = 0,
                    name = "dog_test",
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
        coEvery { dogApiService.getDogByMlId("1") } returns response

        val result = scanDogRepository.getRecognizedDog("1")

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data.isSuccess).isTrue()
        assertThat((result).data.message).isEqualTo("OK")
        assertThat((result).data.data).isNotNull()
        assertThat((result).data.data.dog).isNotNull()
        assertThat((result).data.data.dog.name).isEqualTo("dog_test")
        assertThat((result).data.data.dog.id).isEqualTo(1)

        verifyAll {
            response.isSuccessful
            response.body()
        }
        coVerify { dogApiService.getDogByMlId("1") }
    }

    @Test
    fun `getRecognizedDog should return a error result`() = runTest {
        val response: Response<DogApiResponse> = mockk()
        every { response.isSuccessful } returns false
        every { response.body() } returns null
        every { response.code() } returns 400
        every { response.message() } returns "error"
        coEvery { dogApiService.getDogByMlId("1") } returns response

        val result = scanDogRepository.getRecognizedDog("1")

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).code).isEqualTo(400)
        assertThat((result).message).isEqualTo("error")

        verifyAll {
            response.isSuccessful
            response.body()
            response.code()
            response.message()
        }
        coVerify { dogApiService.getDogByMlId("1") }
    }

    @Test
    fun `getRecognizedDog should return a error result (successfully but body null)`() = runTest {
        val response: Response<DogApiResponse> = mockk()
        every { response.isSuccessful } returns true
        every { response.body() } returns null
        every { response.code() } returns 400
        every { response.message() } returns "body null"
        coEvery { dogApiService.getDogByMlId("1") } returns response

        val result = scanDogRepository.getRecognizedDog("1")

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).code).isEqualTo(400)
        assertThat((result).message).isEqualTo("body null")

        verifyAll {
            response.isSuccessful
            response.body()
            response.code()
            response.message()
        }
        coVerify { dogApiService.getDogByMlId("1") }
    }

    @Test
    fun `getRecognizedDog should return a exception result`() = runTest {
        coEvery { dogApiService.getDogByMlId("1") } throws UnknownHostException("exception error")

        val result = scanDogRepository.getRecognizedDog("1")

        assertThat(result).isInstanceOf(Result.Exception::class.java)
        assertThat((result as Result.Exception).exception).isInstanceOf(UnknownHostException::class.java)
        assertThat((result).exception.message).isEqualTo("exception error")

        coVerify { dogApiService.getDogByMlId("1") }
    }
}