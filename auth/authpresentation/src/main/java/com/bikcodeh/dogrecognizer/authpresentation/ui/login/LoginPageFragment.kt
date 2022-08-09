package com.bikcodeh.dogrecognizer.authpresentation.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bikcodeh.dogrecognizer.authpresentation.R
import com.bikcodeh.dogrecognizer.authpresentation.databinding.FragmentLoginPageBinding
import com.bikcodeh.dogrecognizer.authpresentation.ui.viewmodel.AuthViewModel
import com.bikcodeh.dogrecognizer.core.util.extension.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginPageFragment : Fragment() {

    private var _binding: FragmentLoginPageBinding? = null
    private val binding: FragmentLoginPageBinding
        get() = _binding!!

    private var progressDialog: AlertDialog? = null
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        progressDialog = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = context?.createProgressDialog()
        setUpListeners()
        setUpCollectors()
    }

    private fun setUpCollectors() {
        observeFlows { scope ->
            scope.launch {
                authViewModel.formUiState.collect(::handleForm)
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
                        launchSafeActivity("com.bikcodeh.dogrecognizer.MainActivity")
                        activity?.finish()
                    }
                }
            }
        }
    }

    private fun handleForm(formState: AuthViewModel.FormState) {
        with(binding) {
            emailInput.isErrorEnabled = formState.emailHasError
            emailInput.error = requireContext().getStringOrNull(formState.emailErrorMessage)

            passwordInput.isErrorEnabled = formState.passwordHasError
            passwordInput.error =
                requireContext().getStringOrNull(formState.passwordErrorMessage)

            loginButton.isEnabled = formState.isEmailValid && formState.isPassWordValid
        }
    }

    private fun setUpListeners() {

        with(binding) {
            loginButton.setOnClickListener {
                authViewModel.logIn(emailEdit.text.toString(), passwordEdit.text.toString())
            }
            loginRegisterButton.setOnClickListener {
                findNavController().navigate(R.id.action_loginPageFragment_to_signUpFragment)
            }

            emailEdit.onTextChange {
                if (emailEdit.hasFocus())
                    authViewModel.validateEmail(it)
            }

            passwordEdit.onTextChange {
                if (passwordEdit.hasFocus())
                    authViewModel.validatePassword(it)
            }
        }
    }
}