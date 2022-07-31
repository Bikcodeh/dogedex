package com.bikcodeh.dogrecognizer.presentation.account

import android.text.TextUtils
import android.util.Patterns
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.domain.model.User
import com.bikcodeh.dogrecognizer.domain.common.Error
import com.bikcodeh.dogrecognizer.domain.common.fold
import com.bikcodeh.dogrecognizer.domain.common.toError
import com.bikcodeh.dogrecognizer.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _formUIState: MutableStateFlow<FormState> = MutableStateFlow(FormState())
    val formUiState: StateFlow<FormState>
        get() = _formUIState.asStateFlow()


    private val _authUiState: MutableStateFlow<AuthUiState> = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState>
        get() = _authUiState.asStateFlow()

    private val _confirmPassword: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val confirmPassword: StateFlow<Boolean>
        get() = _confirmPassword.asStateFlow()

    fun validatePassword(password: String) {
        if (TextUtils.isEmpty(password)) {
            _formUIState.update { currentState ->
                currentState.copy(
                    passwordHasError = true,
                    passwordErrorMessage = R.string.required,
                    isPassWordValid = false
                )
            }
            _confirmPassword.value = false
        } else {
            _formUIState.update { currentState ->
                currentState.copy(
                    passwordHasError = false,
                    passwordErrorMessage = null,
                    isPassWordValid = true
                )
            }
            _confirmPassword.value = true
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String) {
        if (confirmPassword.isEmpty()) {
            _formUIState.update { currentState ->
                currentState.copy(
                    confirmPasswordHasError = true,
                    confirmPasswordErrorMessage = R.string.required,
                    isConfirmPasswordValid = false
                )
            }
        } else if (password != confirmPassword) {
            _formUIState.update { currentState ->
                currentState.copy(
                    confirmPasswordHasError = true,
                    confirmPasswordErrorMessage = R.string.password_not_match,
                    isConfirmPasswordValid = false
                )
            }
        } else {
            _formUIState.update { currentState ->
                currentState.copy(
                    confirmPasswordHasError = false,
                    confirmPasswordErrorMessage = null,
                    isConfirmPasswordValid = true
                )
            }
        }
    }

    fun validateEmail(email: String) {
        if (email.isEmpty()) {
            _formUIState.update { currentState ->
                currentState.copy(
                    emailHasError = true,
                    emailErrorMessage = R.string.email_required,
                    isEmailValid = false
                )
            }
        } else if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _formUIState.update { currentState ->
                currentState.copy(
                    emailHasError = true,
                    emailErrorMessage = R.string.email_not_valid,
                    isEmailValid = false
                )
            }
        } else {
            _formUIState.update { currentState ->
                currentState.copy(
                    emailHasError = false,
                    emailErrorMessage = null,
                    isEmailValid = true
                )
            }
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        _authUiState.update { state -> state.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.signUp(email, password, confirmPassword)
                .fold(
                    onSuccess = {
                        _authUiState.update { state ->
                            state.copy(
                                isLoading = false,
                                user = it,
                                errorMessage = null,
                                errorMessageId = null
                            )
                        }
                    },
                    onError = { code, message ->
                        _authUiState.update { state ->
                            state.copy(
                                isLoading = false,
                                user = null,
                                errorMessage = message,
                                errorMessageId = null
                            )
                        }
                    }, onException = {
                        when (it.toError()) {
                            Error.Connectivity -> {
                                _authUiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        user = null,
                                        errorMessage = null,
                                        errorMessageId = R.string.error_connectivity
                                    )
                                }
                            }
                            is Error.Server -> {
                                _authUiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        user = null,
                                        errorMessage = null,
                                        errorMessageId = R.string.error_server
                                    )
                                }
                            }
                            is Error.Unknown -> {
                                _authUiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        user = null,
                                        errorMessage = null,
                                        errorMessageId = R.string.error_unknown
                                    )
                                }
                            }
                        }
                    }
                )
        }
    }

    fun logIn(email: String, password: String){
        _authUiState.update { state -> state.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.signIn(email, password)
                .fold(
                    onSuccess = {
                        _authUiState.update { state ->
                            state.copy(
                                isLoading = false,
                                user = it,
                                errorMessage = null,
                                errorMessageId = null
                            )
                        }
                    },
                    onError = { code, message ->
                        _authUiState.update { state ->
                            state.copy(
                                isLoading = false,
                                user = null,
                                errorMessage = message,
                                errorMessageId = null
                            )
                        }
                    }, onException = {
                        when (it.toError()) {
                            Error.Connectivity -> {
                                _authUiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        user = null,
                                        errorMessage = null,
                                        errorMessageId = R.string.error_connectivity
                                    )
                                }
                            }
                            is Error.Server -> {
                                _authUiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        user = null,
                                        errorMessage = null,
                                        errorMessageId = R.string.error_server
                                    )
                                }
                            }
                            is Error.Unknown -> {
                                _authUiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        user = null,
                                        errorMessage = null,
                                        errorMessageId = R.string.error_unknown
                                    )
                                }
                            }
                        }
                    }
                )
        }
    }

    data class AuthUiState(
        val isLoading: Boolean = false,
        val user: User? = null,
        @StringRes val errorMessageId: Int? = null,
        val errorMessage: String? = null
    )

    data class FormState(
        val emailHasError: Boolean = false,
        @IdRes val emailErrorMessage: Int? = null,
        val isEmailValid: Boolean = false,
        val passwordHasError: Boolean = false,
        @IdRes val passwordErrorMessage: Int? = null,
        val isPassWordValid: Boolean = false,
        val confirmPasswordHasError: Boolean = false,
        @IdRes val confirmPasswordErrorMessage: Int? = null,
        val isConfirmPasswordValid: Boolean = false
    )
}