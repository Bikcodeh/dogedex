package com.bikcodeh.dogrecognizer.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.domain.common.fold
import com.bikcodeh.dogrecognizer.domain.model.Dog
import com.bikcodeh.dogrecognizer.domain.repository.DogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val dogRepository: DogRepository
) : ViewModel() {

    private val _favoriteDogs: MutableStateFlow<FavoriteUiState> =
        MutableStateFlow(FavoriteUiState())
    val favoriteDogs: StateFlow<FavoriteUiState>
        get() = _favoriteDogs.asStateFlow()

    fun getFavoriteDogs() {
        _favoriteDogs.update { state ->
            state.copy(
                isLoading = true,
                dogs = emptyList(),
                error = null
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            dogRepository.getUserDogs().fold(
                onSuccess = {
                    _favoriteDogs.update { state ->
                        state.copy(
                            isLoading = false,
                            dogs = it,
                            error = null
                        )
                    }
                },
                onError = { _, _ ->
                    _favoriteDogs.update { state ->
                        state.copy(
                            isLoading = false,
                            dogs = emptyList(),
                            error = R.string.error_unknown
                        )
                    }
                },
                onException = {
                    _favoriteDogs.update { state ->
                        state.copy(
                            isLoading = false,
                            dogs = emptyList(),
                            error = R.string.error_connectivity
                        )
                    }
                }
            )
        }
    }

    data class FavoriteUiState(
        val isLoading: Boolean = false,
        val dogs: List<Dog> = emptyList(),
        val error: Int? = null
    )
}