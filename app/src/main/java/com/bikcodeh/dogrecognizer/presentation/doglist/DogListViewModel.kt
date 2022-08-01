package com.bikcodeh.dogrecognizer.presentation.doglist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.data.remote.interceptor.ApiServiceInterceptor
import com.bikcodeh.dogrecognizer.data.repository.DogRepositoryImpl
import com.bikcodeh.dogrecognizer.domain.model.Dog
import com.bikcodeh.dogrecognizer.domain.common.fold
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

    private val _dogsLivedata = MutableLiveData<List<Dog>>()
    val dogsLivedata: LiveData<List<Dog>>
        get() = _dogsLivedata

    private val _addDogState: MutableStateFlow<AddDogUiState> = MutableStateFlow(AddDogUiState())
    val addDogState: StateFlow<AddDogUiState>
        get() = _addDogState.asStateFlow()

    init {
        downloadDogs()
    }

    private fun downloadDogs() {
        viewModelScope.launch {
            dogRepositoryImpl.downloadDogs()
                .fold(
                    onSuccess = {
                        _dogsLivedata.value = it
                    },
                    onError = { _, message ->
                        Log.d("ERROR", message.toString())
                    },
                    onException = {
                        Log.d("ERROR", it.message.toString())
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

    data class AddDogUiState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean? = null,
        val error: Int? = null
    )
}