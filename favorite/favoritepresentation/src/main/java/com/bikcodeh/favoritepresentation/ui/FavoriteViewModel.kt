package com.bikcodeh.favoritepresentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.core.R
import com.bikcodeh.dogrecognizer.core_common.fold
import com.bikcodeh.dogrecognizer.core_model.Dog
import com.bikcodeh.favoritedomain.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val dogRepository: FavoriteRepository
) : ViewModel() {

    private val _favoriteDogs: Channel<FavoriteUiState> =
        Channel()
    val favoriteDogs: Flow<FavoriteUiState>
        get() = _favoriteDogs.receiveAsFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    fun getFavoriteDogs() {
        viewModelScope.launch(Dispatchers.IO) {
            _effect.send(Effect.IsLoading(true))
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
            _effect.send(Effect.IsLoading(false))
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