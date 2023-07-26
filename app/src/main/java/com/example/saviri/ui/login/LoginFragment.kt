package com.example.saviri.ui.login

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.saviri.R
import com.example.saviri.data.Resource
import com.example.saviri.databinding.FragmentLoginBinding
import com.google.firebase.database.collection.LLRBNode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private val viewModel : LoginViewModel by viewModels { LoginViewModel.Factory  }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater,container,false)
        val state = viewModel.state

        binding.apply {

            loginemail.addTextChangedListener {
                viewModel.event(LoginFormEvent.EmailChanged(it.toString()))
            }
            textInputLayout.apply {
                if(state.emailError != null){
                    error = state.emailError
                }
            }

            textInputLayout2.apply {
                if(state.passwordError != null){
                    error = state.passwordError
                }
            }


            loginpass.addTextChangedListener {
                viewModel.event(LoginFormEvent.PasswordChanged(it.toString()))
            }

            button.setOnClickListener {
                Log.d("TAG", "asdfghjdthghdmghmdghmsedtmghmd: -------------------------------------------$state")
                Log.d("TAG", "asdfghjdthghdmghmdghmsedtmghmd: -------------------------------------------$state")
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
                        Log.d("TAG", "onCreateView: error ..... ${event.message}")
                    }
                    LoginState.Loading -> {

                    }
                    LoginState.Success -> {

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