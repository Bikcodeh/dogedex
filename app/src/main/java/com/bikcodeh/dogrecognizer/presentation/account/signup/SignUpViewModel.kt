package com.bikcodeh.dogrecognizer.presentation.account.signup

import android.text.TextUtils
import android.util.Patterns
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModel
import com.bikcodeh.dogrecognizer.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignUpViewModel : ViewModel() {

    private val _formUIState: MutableStateFlow<FormState> = MutableStateFlow(FormState())
    val formUiState: StateFlow<FormState>
        get() = _formUIState.asStateFlow()

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