package com.bikcodeh.dogrecognizer.presentation.doglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.data.repository.DogRepositoryImpl
import com.bikcodeh.dogrecognizer.domain.model.Dog
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
            _dogsLivedata.value = dogRepositoryImpl.downloadDogs()
        }
    }
}