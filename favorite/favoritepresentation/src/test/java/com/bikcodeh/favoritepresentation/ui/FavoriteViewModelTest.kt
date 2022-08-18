package com.bikcodeh.favoritepresentation.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bikcodeh.dogrecognizer.core.R
import com.bikcodeh.dogrecognizer.core_common.Result
import com.bikcodeh.dogrecognizer.core_model.Dog
import com.bikcodeh.dogrecognizer.core_testing.util.MainDispatcherRule
import com.bikcodeh.favoritedomain.repository.FavoriteRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FavoriteViewModelTest {

    lateinit var favoriteViewModel: FavoriteViewModel

    @MockK
    lateinit var favoriteRepository: FavoriteRepository

    private val result = arrayListOf<FavoriteViewModel.FavoriteUiState>()
    private val resultEffect = arrayListOf<FavoriteViewModel.Effect>()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        favoriteViewModel = FavoriteViewModel(favoriteRepository, Dispatchers.Unconfined)
    }

    @Test
    fun getFavoriteDogs() {
        assertThat(favoriteViewModel.favoriteDogs).isInstanceOf(Flow::class.java)
    }

    @Test
    fun getEffect() {
        assertThat(favoriteViewModel.effect).isInstanceOf(Flow::class.java)
    }

    @Test
    fun `getFavoriteDogs should return an empty list of dogs`() = runTest {
        coEvery { favoriteRepository.getUserDogs() } returns Result.Success(emptyList())

        favoriteViewModel.getFavoriteDogs()

        val job = launch(UnconfinedTestDispatcher()) {
            favoriteViewModel.favoriteDogs.toList(result)
        }

        assertThat(result).isNotEmpty()
        assertThat(result.count()).isEqualTo(1)

        coVerify(exactly = 1) { favoriteRepository.getUserDogs() }
        job.cancel()
    }

    @Test
    fun `getFavoriteDogs should return a list of dogs`() = runTest {
        coEvery { favoriteRepository.getUserDogs() } returns Result.Success(
            listOf(
                Dog(
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

        val job = launch(UnconfinedTestDispatcher()) {
            favoriteViewModel.favoriteDogs.toList(result)
        }

        val jobEffect = launch(UnconfinedTestDispatcher()) {
            favoriteViewModel.effect.toList(resultEffect)
        }

        favoriteViewModel.getFavoriteDogs()

        assertThat(result).isNotEmpty()
        assertThat(resultEffect).isNotEmpty()
        assertThat(resultEffect.count()).isEqualTo(2)
        assertThat(resultEffect.first()).isInstanceOf(FavoriteViewModel.Effect.IsLoading::class.java)
        assertThat((resultEffect.first() as FavoriteViewModel.Effect.IsLoading).isLoading).isTrue()
        assertThat((resultEffect[1] as FavoriteViewModel.Effect.IsLoading).isLoading).isFalse()
        assertThat(result.count()).isEqualTo(1)
        assertThat(result.first().dogs).isNotNull()
        assertThat(result.first().dogs?.count()).isEqualTo(1)
        assertThat(result.first().dogs?.first()?.name).isEqualTo("test")
        assertThat(result.first().error).isNull()

        coVerify(exactly = 1) { favoriteRepository.getUserDogs() }
        job.cancel()
        jobEffect.cancel()
    }

    @Test
    fun `getFavoriteDogs should return a error`() = runTest {
        coEvery { favoriteRepository.getUserDogs() } returns Result.Error(404, "error")

        val job = launch(UnconfinedTestDispatcher()) {
            favoriteViewModel.favoriteDogs.toList(result)
        }

        val jobEffect = launch(UnconfinedTestDispatcher()) {
            favoriteViewModel.effect.toList(resultEffect)
        }

        favoriteViewModel.getFavoriteDogs()

        assertThat(result).isNotEmpty()
        assertThat(resultEffect).isNotEmpty()
        assertThat(resultEffect.count()).isEqualTo(2)
        assertThat(resultEffect.first()).isInstanceOf(FavoriteViewModel.Effect.IsLoading::class.java)
        assertThat((resultEffect.first() as FavoriteViewModel.Effect.IsLoading).isLoading).isTrue()
        assertThat((resultEffect[1] as FavoriteViewModel.Effect.IsLoading).isLoading).isFalse()
        assertThat(result.count()).isEqualTo(1)
        assertThat(result.first().dogs).isNull()
        assertThat(result.first().error).isNotNull()
        assertThat(result.first().error).isEqualTo(R.string.error_unknown)

        coVerify(exactly = 1) { favoriteRepository.getUserDogs() }
        job.cancel()
        jobEffect.cancel()
    }

    @Test
    fun `getFavoriteDogs should return a error (exception result)`() = runTest {
        coEvery { favoriteRepository.getUserDogs() } returns Result.Exception(Exception("exception error"))

        val job = launch(UnconfinedTestDispatcher()) {
            favoriteViewModel.favoriteDogs.toList(result)
        }

        val jobEffect = launch(UnconfinedTestDispatcher()) {
            favoriteViewModel.effect.toList(resultEffect)
        }

        favoriteViewModel.getFavoriteDogs()

        assertThat(result).isNotEmpty()
        assertThat(resultEffect).isNotEmpty()
        assertThat(resultEffect.count()).isEqualTo(2)
        assertThat(resultEffect.first()).isInstanceOf(FavoriteViewModel.Effect.IsLoading::class.java)
        assertThat((resultEffect.first() as FavoriteViewModel.Effect.IsLoading).isLoading).isTrue()
        assertThat((resultEffect[1] as FavoriteViewModel.Effect.IsLoading).isLoading).isFalse()
        assertThat(result.count()).isEqualTo(1)
        assertThat(result.first().dogs).isNull()
        assertThat(result.first().error).isNotNull()
        assertThat(result.first().error).isEqualTo(R.string.error_connectivity)

        coVerify(exactly = 1) { favoriteRepository.getUserDogs() }
        job.cancel()
        jobEffect.cancel()
    }
}