package com.example.saviri.ui.login

import android.os.Bundle
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

        lifecycleScope.launch{

            viewModel.validationEvents.collect { event ->

                when(event){
                    LoginState.Empty -> {

                    }
                    is LoginState.Error -> {
                    }
                    LoginState.Loading -> {

                    }
                    LoginState.Success -> {

                    }
                    is LoginState.EmailError -> {
                        toastutil.showMessage("Hello",activity)
                        binding.textInputLayout.apply {
                            error = event.message
                        }
                    }
                    is LoginState.PasswordError -> {
                        binding.textInputLayout2.apply {
                            error = event.message
                        }
                    }
                }

            }
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.loginFlow.collectLatest {
                    when(it){
                        is Resource.Failure -> {
                        }
                        Resource.Loading -> {

                        }
                        is Resource.Success -> {

                            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment2()
                            findNavController().navigate(action)

                        }
                        null -> {

                        }
                    }

                }

                }
        }


        return binding.root
    }


}