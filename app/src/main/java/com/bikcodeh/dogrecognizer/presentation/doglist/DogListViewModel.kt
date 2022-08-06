package com.bikcodeh.dogrecognizer.presentation.doglist

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.data.remote.interceptor.ApiServiceInterceptor
import com.bikcodeh.dogrecognizer.domain.common.fold
import com.bikcodeh.dogrecognizer.domain.model.Dog
import com.bikcodeh.dogrecognizer.domain.repository.DataStoreOperations
import com.bikcodeh.dogrecognizer.domain.repository.DogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogListViewModel @Inject constructor(
    private val dogRepository: DogRepository,
    private val dataStoreOperations: DataStoreOperations
) : ViewModel() {

    private val _dogsUiState: MutableStateFlow<DogsUiState> = MutableStateFlow(DogsUiState())
    val dogsUiState: StateFlow<DogsUiState>
        get() = _dogsUiState.asStateFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        downloadDogs()
    }

    fun downloadDogs() {
        setEffect(Effect.IsLoadingDogs(true))
        viewModelScope.launch {
            dogRepository.downloadDogs()
                .fold(
                    onSuccess = {
                        _dogsUiState.update { state -> state.copy(dogs = it, error = null) }
                    },
                    onError = { _, _ ->
                        _dogsUiState.update { state ->
                            state.copy(
                                dogs = null,
                                error = R.string.error_unknown
                            )
                        }
                    },
                    onException = {
                        _dogsUiState.update { state ->
                            state.copy(
                                dogs = null,
                                error = R.string.error_connectivity
                            )
                        }
                    }
                )
            setEffect(Effect.IsLoadingDogs(false))
        }
    }

    fun logOut() {
        ApiServiceInterceptor.clearToken()
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreOperations.deleteUser()
        }
    }

    fun addDogToUser(dogId: String) {
        setEffect(Effect.ShowLoading)
        viewModelScope.launch(Dispatchers.IO) {
            dogRepository.addDogToUser(dogId)
                .fold(
                    onSuccess = {
                        if (it.isSuccess) {
                            setEffect(Effect.ShowSnackBar(R.string.added))
                        } else {
                            setEffect(Effect.ShowSnackBar(R.string.error_adding_dog))
                        }
                    },
                    onException = {
                        setEffect(Effect.ShowSnackBar(R.string.error_connectivity))
                    },
                    onError = { _, _ ->
                        setEffect(Effect.ShowSnackBar(R.string.error_unknown))
                    }
                )
            setEffect(Effect.HideLoading)
        }
    }

    fun recognizeDogById(id: String) {
        setEffect(Effect.ShowLoading)
        viewModelScope.launch(Dispatchers.IO) {
            dogRepository.getRecognizedDog(id)
                .fold(
                    onSuccess = {
                        setEffect(Effect.NavigateToDetail(it.data.dog.toDomain()))
                    },
                    onError = { _, _ ->
                        setEffect(Effect.ShowSnackBar(R.string.error_unknown))
                    },
                    onException = {
                        setEffect(Effect.ShowSnackBar(R.string.error_connectivity))
                    }
                )
            setEffect(Effect.HideLoading)
        }
    }

    private fun setEffect(uiEffect: Effect) {
        viewModelScope.launch(Dispatchers.IO) {
            _effect.send(uiEffect)
        }
    }

    data class DogsUiState(
        val dogs: List<Dog>? = null,
        val error: Int? = null
    )

    sealed class Effect {
        data class NavigateToDetail(val dog: Dog) : Effect()
        data class ShowSnackBar(@StringRes val resId: Int) : Effect()
        data class IsLoadingDogs(val isLoading: Boolean) : Effect()
        object ShowLoading : Effect()
        object HideLoading : Effect()
    }
}