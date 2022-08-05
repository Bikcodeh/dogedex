package com.bikcodeh.dogrecognizer.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.domain.common.fold
import com.bikcodeh.dogrecognizer.domain.model.Dog
import com.bikcodeh.dogrecognizer.domain.repository.DogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
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

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    fun getFavoriteDogs() {
        setEffect(Effect.IsLoading(true))
        viewModelScope.launch(Dispatchers.IO) {
            dogRepository.getUserDogs().fold(
                onSuccess = {
                    _favoriteDogs.update { state ->
                        state.copy(
                            dogs = it,
                            error = null
                        )
                    }
                },
                onError = { _, _ ->
                    _favoriteDogs.update { state ->
                        state.copy(
                            dogs = emptyList(),
                            error = R.string.error_unknown
                        )
                    }
                },
                onException = {
                    _favoriteDogs.update { state ->
                        state.copy(
                            dogs = emptyList(),
                            error = R.string.error_connectivity
                        )
                    }
                }
            )
            _effect.send(Effect.IsLoading(false))
        }
    }

    private fun setEffect(uiEffect: Effect) {
        viewModelScope.launch(Dispatchers.IO) {
            _effect.send(uiEffect)
        }
    }

    sealed class Effect {
        data class IsLoading(val isLoading: Boolean) : Effect()
    }

    data class FavoriteUiState(
        val dogs: List<Dog> = emptyList(),
        val error: Int? = null
    )
}