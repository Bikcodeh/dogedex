package com.bikcodeh.dogrecognizer.authpresentation.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bikcodeh.dogrecognizer.authpresentation.databinding.FragmentSignUpBinding
import com.bikcodeh.dogrecognizer.authpresentation.ui.viewmodel.AuthViewModel
import com.bikcodeh.dogrecognizer.core.util.extension.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding!!

    private val authViewModel by viewModels<AuthViewModel>()
    private var progressDialog: AlertDialog? = null

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
        progressDialog = context?.createProgressDialog()
        setUpListeners()
        setUpCollectors()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        progressDialog = null
    }

    private fun setUpCollectors() {
        observeFlows { scope ->
            scope.launch {
                authViewModel.formUiState.collect(::handleFormState)
            }
            scope.launch {
                authViewModel.confirmPassword.collect { enable ->
                    binding.confirmPasswordInput.isEnabled = enable
                }
            }
            scope.launch {
                authViewModel.authUiState.collect { state ->
                    if (state.isLoading) {
                        progressDialog?.show()
                    } else {
                        progressDialog?.dismiss()
                    }

                    state.errorMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }

                    state.errorMessageId?.let {
                        Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
                    }
                    state.user?.let {
                        progressDialog?.dismiss()
                        launchSafeActivity("com.bikcodeh.dogrecognizer.MainActivity")
                        activity?.finish()
                    }
                }
            }
        }
    }

    private fun handleFormState(formState: AuthViewModel.FormState) {
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
                authViewModel.signUp(
                    binding.emailEdit.text.toString(),
                    binding.passwordEdit.text.toString(),
                    binding.confirmPasswordEdit.text.toString()
                )
            }

            emailEdit.onTextChange {
                authViewModel.validateEmail(it)
            }

            passwordEdit.onTextChange {
                authViewModel.validatePassword(it)
            }

            confirmPasswordEdit.onTextChange {
                authViewModel.validateConfirmPassword(passwordEdit.text.toString(), it)
            }
        }
    }
}