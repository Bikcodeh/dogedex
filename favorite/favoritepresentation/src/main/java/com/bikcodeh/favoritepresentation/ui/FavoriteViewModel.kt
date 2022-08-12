package com.bikcodeh.favoritepresentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.core.R
import com.bikcodeh.dogrecognizer.core_common.di.IoDispatcher
import com.bikcodeh.dogrecognizer.core_common.fold
import com.bikcodeh.dogrecognizer.core_model.Dog
import com.bikcodeh.favoritedomain.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val dogRepository: FavoriteRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _favoriteDogs: Channel<FavoriteUiState> =
        Channel()
    val favoriteDogs: Flow<FavoriteUiState>
        get() = _favoriteDogs.receiveAsFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    fun getFavoriteDogs() {
        setEffect(Effect.IsLoading(true))
        viewModelScope.launch(dispatcher) {
            dogRepository.getUserDogs().fold(
                onSuccess = {
                    _favoriteDogs.send(
                        FavoriteUiState(
                            dogs = it.sortedBy { dog -> dog.id },
                            error = null
                        )
                    )
                },
                onError = { _, _ ->
                    _favoriteDogs.send(
                        FavoriteUiState(
                            dogs = null,
                            error = R.string.error_unknown
                        )
                    )
                },
                onException = {
                    _favoriteDogs.send(
                        FavoriteUiState(
                            dogs = null,
                            error = R.string.error_connectivity
                        )
                    )
                }
            )
            setEffect(Effect.IsLoading(false))
        }
    }

    private fun setEffect(effect: Effect){
        viewModelScope.launch(dispatcher) {
            _effect.send(effect)
        }
    }

    sealed class Effect {
        data class IsLoading(val isLoading: Boolean) : Effect()
    }

    data class FavoriteUiState(
        val dogs: List<Dog>? = null,
        val error: Int? = null
    )
}