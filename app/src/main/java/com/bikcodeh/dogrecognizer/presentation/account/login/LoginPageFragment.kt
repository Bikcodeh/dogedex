package com.bikcodeh.dogrecognizer.presentation.account.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bikcodeh.dogrecognizer.MainActivity
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.databinding.FragmentLoginPageBinding
import com.bikcodeh.dogrecognizer.presentation.account.AuthViewModel
import com.bikcodeh.dogrecognizer.presentation.util.extension.createProgressDialog
import com.bikcodeh.dogrecognizer.presentation.util.extension.getStringOrNull
import com.bikcodeh.dogrecognizer.presentation.util.extension.observeFlows
import com.bikcodeh.dogrecognizer.presentation.util.extension.onTextChange
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
                        startActivity(Intent(activity, MainActivity::class.java))
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

        with (binding) {
            loginButton.setOnClickListener {
                authViewModel.logIn(emailEdit.text.toString(), passwordEdit.text.toString())
            }
            loginRegisterButton.setOnClickListener {
                findNavController().navigate(R.id.action_loginPageFragment_to_signUpFragment)
            }

            emailEdit.onTextChange {
                authViewModel.validateEmail(it)
            }

            passwordEdit.onTextChange {
                authViewModel.validatePassword(it)
            }
        }
    }
}