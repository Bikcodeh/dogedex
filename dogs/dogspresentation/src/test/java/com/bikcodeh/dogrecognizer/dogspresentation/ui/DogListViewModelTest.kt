package com.bikcodeh.dogrecognizer.dogspresentation.ui


import com.bikcodeh.dogrecognizer.core_common.Result
import com.bikcodeh.dogrecognizer.core_common.interceptor.ApiServiceInterceptor
import com.bikcodeh.dogrecognizer.core_model.Dog
import com.bikcodeh.dogrecognizer.core_model.response.DefaultResponse
import com.bikcodeh.dogrecognizer.core_preferences.domain.repository.DataStoreOperations
import com.bikcodeh.dogrecognizer.core_testing.util.MainDispatcherRule
import com.bikcodeh.dogrecognizer.dogsdomain.repository.DogRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.bikcodeh.dogrecognizer.core.R as RC

@ExperimentalCoroutinesApi
class DogListViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var dataStoreOperations: DataStoreOperations

    @RelaxedMockK
    lateinit var dogRepository: DogRepository

    @RelaxedMockK
    lateinit var apiServiceInterceptor: ApiServiceInterceptor

    private lateinit var dogsViewModel: DogListViewModel

    @Before
    fun setUp() {
        coEvery { dogRepository.downloadDogs() } returns Result.Success(emptyList())
        dogsViewModel = DogListViewModel(
            dogRepository,
            dataStoreOperations,
            apiServiceInterceptor,
            UnconfinedTestDispatcher()
        )
    }

    @Test
    fun getDogsUiState() = runTest {
        val resultEffect = arrayListOf<DogListViewModel.Effect>()
        val jobEffect = launch(UnconfinedTestDispatcher()) {
            dogsViewModel.effect.toList(resultEffect)
        }
        assertThat(dogsViewModel.dogsUiState.value.dogs).isNotNull()
        assertThat(dogsViewModel.dogsUiState.value.dogs).isEmpty()
        assertThat(dogsViewModel.dogsUiState.value.error).isNull()
        assertThat(resultEffect.count()).isEqualTo(2)
        assertThat(resultEffect[0]).isInstanceOf(DogListViewModel.Effect.IsLoadingDogs::class.java)
        assertThat((resultEffect[0] as DogListViewModel.Effect.IsLoadingDogs).isLoading).isTrue()
        assertThat((resultEffect[1] as DogListViewModel.Effect.IsLoadingDogs).isLoading).isFalse()
        assertThat(resultEffect.count()).isEqualTo(2)
        coVerify { dogRepository.downloadDogs() }
        jobEffect.cancel()
    }

    @Test
    fun getEffect() {
        assertThat(dogsViewModel.effect).isInstanceOf(Flow::class.java)
        coVerify { dogRepository.downloadDogs() }
    }

    @Test
    fun downloadDogs() = runTest {
        coEvery { dogRepository.downloadDogs() } returns Result.Success(
            listOf(
                Dog(
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
        dogsViewModel = DogListViewModel(
            dogRepository,
            dataStoreOperations,
            apiServiceInterceptor,
            UnconfinedTestDispatcher()
        )
        val resultEffect = arrayListOf<DogListViewModel.Effect>()
        val resultState = arrayListOf<DogListViewModel.DogsUiState>()
        val jobEffect = launch(UnconfinedTestDispatcher()) {
            dogsViewModel.effect.toList(resultEffect)
        }

        val jobState = launch(UnconfinedTestDispatcher()) {
            dogsViewModel.dogsUiState.toList(resultState)
        }
        assertThat(resultEffect.count()).isEqualTo(2)
        assertThat(resultEffect[0]).isInstanceOf(DogListViewModel.Effect.IsLoadingDogs::class.java)
        assertThat((resultEffect[0] as DogListViewModel.Effect.IsLoadingDogs).isLoading).isTrue()
        assertThat((resultEffect[1] as DogListViewModel.Effect.IsLoadingDogs).isLoading).isFalse()
        assertThat(resultState.count()).isEqualTo(1)
        assertThat(resultState.first().dogs?.first()?.name).isEqualTo("dog_test")
        assertThat(resultState.first().dogs?.first()?.id).isEqualTo(1)
        coVerify { dogRepository.downloadDogs() }
        jobEffect.cancel()
        jobState.cancel()
    }

    @Test
    fun logOut() {
        coEvery { dataStoreOperations.deleteUser() } just Runs

        dogsViewModel.logOut()

        coVerify(exactly = 1) { dataStoreOperations.deleteUser() }
        coVerify(exactly = 1) { apiServiceInterceptor.clearToken() }
    }

    @Test
    fun `addDogToUser should add successfully`() = runTest {
        coEvery { dogRepository.addDogToUser("1") } returns Result.Success(
            DefaultResponse(
                message = "OK",
                isSuccess = true
            )
        )
        val resultEffect = arrayListOf<DogListViewModel.Effect>()
        val jobEffect = launch(UnconfinedTestDispatcher()) {
            dogsViewModel.effect.toList(resultEffect)
        }

        dogsViewModel.addDogToUser("1")

        coVerify { dogRepository.addDogToUser("1") }
        assertThat(resultEffect.count()).isEqualTo(5)
        assertThat(resultEffect[2]).isInstanceOf(DogListViewModel.Effect.ShowLoading::class.java)
        assertThat(resultEffect[3]).isInstanceOf(DogListViewModel.Effect.ShowSnackBar::class.java)
        assertThat((resultEffect[3] as DogListViewModel.Effect.ShowSnackBar).resId).isEqualTo(RC.string.added)
        assertThat(resultEffect[4]).isInstanceOf(DogListViewModel.Effect.HideLoading::class.java)
        jobEffect.cancel()
    }

    @Test
    fun `addDogToUser should fail and return a display snackbar effect`() = runTest {
        coEvery { dogRepository.addDogToUser("1") } returns Result.Success(
            DefaultResponse(
                message = "OK",
                isSuccess = false
            )
        )
        val resultEffect = arrayListOf<DogListViewModel.Effect>()
        val jobEffect = launch(UnconfinedTestDispatcher()) {
            dogsViewModel.effect.toList(resultEffect)
        }

        dogsViewModel.addDogToUser("1")

        coVerify { dogRepository.addDogToUser("1") }
        assertThat(resultEffect.count()).isEqualTo(5)
        assertThat(resultEffect[2]).isInstanceOf(DogListViewModel.Effect.ShowLoading::class.java)
        assertThat(resultEffect[3]).isInstanceOf(DogListViewModel.Effect.ShowSnackBar::class.java)
        assertThat((resultEffect[3] as DogListViewModel.Effect.ShowSnackBar).resId).isEqualTo(RC.string.error_adding_dog)
        assertThat(resultEffect[4]).isInstanceOf(DogListViewModel.Effect.HideLoading::class.java)
        jobEffect.cancel()
    }

    @Test
    fun `addDogToUser should fail with an error and return a display snackbar effect`() = runTest {
        coEvery { dogRepository.addDogToUser("1") } returns Result.Error(404, "error")
        val resultEffect = arrayListOf<DogListViewModel.Effect>()
        val jobEffect = launch(UnconfinedTestDispatcher()) {
            dogsViewModel.effect.toList(resultEffect)
        }

        dogsViewModel.addDogToUser("1")

        coVerify { dogRepository.addDogToUser("1") }
        assertThat(resultEffect.count()).isEqualTo(5)
        assertThat(resultEffect[2]).isInstanceOf(DogListViewModel.Effect.ShowLoading::class.java)
        assertThat(resultEffect[3]).isInstanceOf(DogListViewModel.Effect.ShowSnackBar::class.java)
        assertThat((resultEffect[3] as DogListViewModel.Effect.ShowSnackBar).resId).isEqualTo(RC.string.error_unknown)
        assertThat(resultEffect[4]).isInstanceOf(DogListViewModel.Effect.HideLoading::class.java)
        jobEffect.cancel()
    }

    @Test
    fun `addDogToUser should fail with an exception and return a display snackbar effect`() = runTest {
        coEvery { dogRepository.addDogToUser("1") } returns Result.Exception(Exception("error"))
        val resultEffect = arrayListOf<DogListViewModel.Effect>()
        val jobEffect = launch(UnconfinedTestDispatcher()) {
            dogsViewModel.effect.toList(resultEffect)
        }

        dogsViewModel.addDogToUser("1")

        coVerify { dogRepository.addDogToUser("1") }
        assertThat(resultEffect.count()).isEqualTo(5)
        assertThat(resultEffect[2]).isInstanceOf(DogListViewModel.Effect.ShowLoading::class.java)
        assertThat(resultEffect[3]).isInstanceOf(DogListViewModel.Effect.ShowSnackBar::class.java)
        assertThat((resultEffect[3] as DogListViewModel.Effect.ShowSnackBar).resId).isEqualTo(RC.string.error_connectivity)
        assertThat(resultEffect[4]).isInstanceOf(DogListViewModel.Effect.HideLoading::class.java)
        jobEffect.cancel()
    }
}