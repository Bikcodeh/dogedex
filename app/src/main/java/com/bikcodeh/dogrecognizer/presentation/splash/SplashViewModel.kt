package com.bikcodeh.dogrecognizer.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.core.domain.repository.DataStoreOperations
import com.bikcodeh.dogrecognizer.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStoreOperations: DataStoreOperations
) : ViewModel() {

    private val _userLogged: MutableStateFlow<User?> = MutableStateFlow(null)
    val userLogged: StateFlow<User?>
        get() = _userLogged.asStateFlow()

    fun getUser() {
        dataStoreOperations.getUser()
            .map {
                _userLogged.value = it
            }
            .flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}