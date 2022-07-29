package com.bikcodeh.dogrecognizer.presentation.doglist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.data.repository.DogRepositoryImpl
import com.bikcodeh.dogrecognizer.domain.model.Dog
import com.bikcodeh.dogrecognizer.domain.model.common.fold
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogListViewModel @Inject constructor(
    private val dogRepositoryImpl: DogRepositoryImpl
) : ViewModel() {

    private val _dogsLivedata = MutableLiveData<List<Dog>>()
    val dogsLivedata: LiveData<List<Dog>>
        get() = _dogsLivedata

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
                    onError = { code, message ->
                        Log.d("ERROR", message.toString())
                    },
                    onException = {
                        Log.d("ERROR", it.message.toString())
                    }
                )
        }
    }
}