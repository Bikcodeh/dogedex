package com.bikcodeh.dogrecognizer.dogspresentation.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.core_common.di.IoDispatcher
import com.bikcodeh.dogrecognizer.core_common.fold
import com.bikcodeh.dogrecognizer.core_common.interceptor.ApiServiceInterceptor
import com.bikcodeh.dogrecognizer.core_model.Dog
import com.bikcodeh.dogrecognizer.core_preferences.domain.repository.DataStoreOperations
import com.bikcodeh.dogrecognizer.dogsdomain.repository.DogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bikcodeh.dogrecognizer.core.R as RC

@HiltViewModel
class DogListViewModel @Inject constructor(
    private val dogRepository: DogRepository,
    private val dataStoreOperations: DataStoreOperations,
    private val apiServiceInterceptor: ApiServiceInterceptor,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
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
        viewModelScope.launch(dispatcher) {
            dogRepository.downloadDogs()
                .fold(
                    onSuccess = {
                        _dogsUiState.update { state -> state.copy(dogs = it, error = null) }
                    },
                    onError = { _, _ ->
                        _dogsUiState.update { state ->
                            state.copy(
                                dogs = null,
                                error = RC.string.error_unknown
                            )
                        }
                    },
                    onException = {
                        _dogsUiState.update { state ->
                            state.copy(
                                dogs = null,
                                error = RC.string.error_connectivity
                            )
                        }
                    }
                )
            setEffect(Effect.IsLoadingDogs(false))
        }
    }

    fun logOut() {
        apiServiceInterceptor.clearToken()
        viewModelScope.launch(dispatcher) {
            dataStoreOperations.deleteUser()
        }
    }

    fun addDogToUser(dogId: String) {
        setEffect(Effect.ShowLoading)
        viewModelScope.launch(dispatcher) {
            dogRepository.addDogToUser(dogId)
                .fold(
                    onSuccess = {
                        if (it.isSuccess) {
                            setEffect(Effect.ShowSnackBar(RC.string.added))
                        } else {
                            setEffect(Effect.ShowSnackBar(RC.string.error_adding_dog))
                        }
                    },
                    onException = {
                        setEffect(Effect.ShowSnackBar(RC.string.error_connectivity))
                    },
                    onError = { _, _ ->
                        setEffect(Effect.ShowSnackBar(RC.string.error_unknown))
                    }
                )
            setEffect(Effect.HideLoading)
        }
    }

    private fun setEffect(uiEffect: Effect) {
        viewModelScope.launch(dispatcher) {
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