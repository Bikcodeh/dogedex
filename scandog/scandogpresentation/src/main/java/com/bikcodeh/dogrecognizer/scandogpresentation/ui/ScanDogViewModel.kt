package com.bikcodeh.dogrecognizer.scandogpresentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.core.R
import com.bikcodeh.dogrecognizer.core.common.fold
import com.bikcodeh.dogrecognizer.scandogdomain.repository.ScanDogRepository
import com.bikcodeh.dogrecognizer.scandogpresentation.ui.state.Effect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanDogViewModel @Inject constructor(
    private val scanDogRepository: ScanDogRepository
) : ViewModel() {

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    fun recognizeDogById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _effect.send(Effect.IsLoading(true))
            scanDogRepository.getRecognizedDog(id)
                .fold(
                    onSuccess = {
                        _effect.send(Effect.NavigateToDetail(it.data.dog.toDomain()))
                    },
                    onError = { _, _ ->
                        _effect.send(Effect.ShowSnackBar(R.string.error_unknown))
                    },
                    onException = {
                        _effect.send(Effect.ShowSnackBar(R.string.error_connectivity))
                    }
                )
            _effect.send(Effect.IsLoading(false))
        }
    }
}