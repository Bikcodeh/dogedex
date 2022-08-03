package com.bikcodeh.dogrecognizer.presentation.doglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.data.remote.interceptor.ApiServiceInterceptor
import com.bikcodeh.dogrecognizer.data.repository.DogRepositoryImpl
import com.bikcodeh.dogrecognizer.domain.common.fold
import com.bikcodeh.dogrecognizer.domain.model.Dog
import com.bikcodeh.dogrecognizer.domain.repository.DataStoreOperations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogListViewModel @Inject constructor(
    private val dogRepositoryImpl: DogRepositoryImpl,
    private val dataStoreOperations: DataStoreOperations
) : ViewModel() {

    private val _dogsUiState: MutableStateFlow<DogsUiState> = MutableStateFlow(DogsUiState())
    val dogsUiState: StateFlow<DogsUiState>
        get() = _dogsUiState.asStateFlow()

    private val _addDogState: MutableStateFlow<AddDogUiState> = MutableStateFlow(AddDogUiState())
    val addDogState: StateFlow<AddDogUiState>
        get() = _addDogState.asStateFlow()

    init {
        downloadDogs()
    }

    fun downloadDogs() {
        _dogsUiState.update { state ->
            state.copy(
                isLoading = true,
                dogs = null,
                error = null
            )
        }
        viewModelScope.launch {
            dogRepositoryImpl.downloadDogs()
                .fold(
                    onSuccess = {
                        _dogsUiState.update { state ->
                            state.copy(
                                isLoading = false,
                                dogs = it,
                                error = null
                            )
                        }
                    },
                    onError = { _, _ ->
                        _dogsUiState.update { state ->
                            state.copy(
                                isLoading = false,
                                dogs = null,
                                error = R.string.error_unknown
                            )
                        }
                    },
                    onException = {
                        _dogsUiState.update { state ->
                            state.copy(
                                isLoading = false,
                                dogs = null,
                                error = R.string.error_connectivity
                            )
                        }
                    }
                )
        }
    }

    fun logOut() {
        ApiServiceInterceptor.clearToken()
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreOperations.deleteUser()
        }
    }

    fun addDogToUser(dogId: String) {
        _addDogState.update { state ->
            state.copy(
                isLoading = true,
                isSuccess = null,
                error = null
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            dogRepositoryImpl.addDogToUser(dogId)
                .fold(
                    onSuccess = {
                        if (it.isSuccess) {
                            _addDogState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    isSuccess = true,
                                    error = null
                                )
                            }
                        } else {
                            _addDogState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    isSuccess = false,
                                    error = R.string.error_adding_dog
                                )
                            }
                        }
                    },
                    onException = {
                        _addDogState.update { state ->
                            state.copy(
                                isLoading = false,
                                isSuccess = null,
                                error = R.string.error_connectivity
                            )
                        }
                    },
                    onError = { _, _ ->
                        _addDogState.update { state ->
                            state.copy(
                                isLoading = false,
                                isSuccess = null,
                                error = R.string.error_unknown
                            )
                        }
                    }
                )
        }
    }

    fun onDogAdded() {
        _addDogState.update { state ->
            state.copy(
                isLoading = null,
                isSuccess = null,
                error = null
            )
        }
    }

    data class AddDogUiState(
        val isLoading: Boolean? = null,
        val isSuccess: Boolean? = null,
        val error: Int? = null
    )

    data class DogsUiState(
        val isLoading: Boolean = false,
        val dogs: List<Dog>? = null,
        val error: Int? = null
    )
}