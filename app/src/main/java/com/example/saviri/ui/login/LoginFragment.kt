package com.example.saviri.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.saviri.data.Resource
import com.example.saviri.databinding.FragmentLoginBinding
import com.example.saviri.util.ProgessDialog
import com.example.saviri.util.ToastUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private val viewModel : LoginViewModel by viewModels { LoginViewModel.Factory  }
    private lateinit  var toastutil: ToastUtil
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater,container,false)
        toastutil = ToastUtil()

        val dialog = ProgessDialog.progressDialog(activity!!)

        binding.apply {

            loginemail.addTextChangedListener {
                viewModel.event(LoginFormEvent.EmailChanged(it.toString()))
            }
            loginpass.addTextChangedListener {
                viewModel.event(LoginFormEvent.PasswordChanged(it.toString()))
            }

            button.setOnClickListener {
                viewModel.event(LoginFormEvent.Submit)
            }
            register.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }

        }

        lifecycleScope.launch {
            viewModel.validationEvents.collect { event ->

                when(event){
                    LoginState.Empty -> {

                    }
                    LoginState.Loading -> {

                    }
                    LoginState.Success -> {

                    }
                    is LoginState.EmailError -> {

                        binding.textInputLayout.apply {
                            isErrorEnabled = true
                            error = event.message
                        }
                    }
                    is LoginState.PasswordError -> {

                        binding.textInputLayout2.apply {
                            isErrorEnabled = true
                            error = event.message
                        }
                    }
                    LoginState.Clear -> {
                        binding.apply {
                            textInputLayout.apply {
                                isErrorEnabled = false
                            }
                            textInputLayout2.apply {
                                isErrorEnabled = false
                            }
                        }
                    }
                }

            }

        }

        lifecycleScope.launch{


            viewModel.loginFlow.collectLatest {
                when(it){
                    is Resource.Failure -> {

                        dialog.dismiss()
                        toastutil.showMessage(it.exception.message,activity)
                    }
                    Resource.Loading -> {

                        dialog.show()

                    }
                    is Resource.Success -> {

                        dialog.dismiss()
                        val action = LoginFragmentDirections.actionLoginFragmentToFragmentCountryList()
                        findNavController().navigate(action)
                        toastutil.showMessage(it.result.email,activity)
                    }
                    null -> {
                    }
                }
            }
        }
        return binding.root
    }


}