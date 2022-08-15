package com.bikcodeh.dogrecognizer.dogspresentation.ui


import com.bikcodeh.dogrecognizer.core_common.Result
import com.bikcodeh.dogrecognizer.core_common.interceptor.ApiServiceInterceptor
import com.bikcodeh.dogrecognizer.core_preferences.domain.repository.DataStoreOperations
import com.bikcodeh.dogrecognizer.core_testing.util.MainDispatcherRule
import com.bikcodeh.dogrecognizer.dogsdomain.repository.DogRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock

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

    @Test
    fun getDogsUiState() = runTest {
        coEvery { dogRepository.downloadDogs() } returns Result.Success(emptyList())
        val resultEffect = arrayListOf<DogListViewModel.Effect>()
        dogsViewModel = DogListViewModel(
            dogRepository,
            dataStoreOperations,
            apiServiceInterceptor,
            Dispatchers.IO
        )
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
    }

    @Test
    fun downloadDogs() {
    }

    @Test
    fun logOut() {
    }

    @Test
    fun addDogToUser() {
    }
}