package com.bikcodeh.dogrecognizer.presentation.account.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bikcodeh.dogrecognizer.databinding.FragmentSignUpBinding
import com.bikcodeh.dogrecognizer.presentation.util.extension.getStringOrNull
import com.bikcodeh.dogrecognizer.presentation.util.extension.observeFlows
import com.bikcodeh.dogrecognizer.presentation.util.extension.onTextChange
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding!!

    private val signUpViewModel by viewModels<SignUpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        activity?.setActionBar(binding.signUpToolbar)
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.signUpToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
        setUpObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpObservers() {
        observeFlows { scope ->
            scope.launch {
                signUpViewModel.formUiState.collect(::handleFormState)
            }
            scope.launch {
                signUpViewModel.confirmPassword.collect { enable ->
                    binding.confirmPasswordInput.isEnabled = enable
                }
            }
        }
    }

    private fun handleFormState(formState: SignUpViewModel.FormState) {
        with(binding) {
            emailInput.isErrorEnabled = formState.emailHasError
            emailInput.error = requireContext().getStringOrNull(formState.emailErrorMessage)

            passwordInput.isErrorEnabled = formState.passwordHasError
            passwordInput.error =
                requireContext().getStringOrNull(formState.passwordErrorMessage)
            confirmPasswordInput.isErrorEnabled = !formState.confirmPasswordHasError
            confirmPasswordInput.error =
                requireContext().getStringOrNull(formState.confirmPasswordErrorMessage)
            signUpButton.isEnabled =
                formState.isEmailValid && formState.isConfirmPasswordValid && formState.isConfirmPasswordValid
        }
    }


    private fun setUpListeners() {
        with(binding) {
            signUpButton.setOnClickListener {

            }

            emailEdit.onTextChange {
                signUpViewModel.validateEmail(it)
            }

            passwordEdit.onTextChange {
                signUpViewModel.validatePassword(it)
            }

            confirmPasswordEdit.onTextChange {
                signUpViewModel.validateConfirmPassword(passwordEdit.text.toString(), it)
            }
        }
    }
}