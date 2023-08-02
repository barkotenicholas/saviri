package com.example.saviri.ui.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.saviri.R
import com.example.saviri.data.Resource
import com.example.saviri.databinding.FragmentRegisterGrafmentBinding
import com.example.saviri.util.ProgessDialog
import com.example.saviri.util.ToastUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {


    private lateinit var binding: FragmentRegisterGrafmentBinding

    private val viewModel : RegisterViewModel by viewModels{ RegisterViewModel.Factory}
    private lateinit  var toastutil: ToastUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterGrafmentBinding.inflate(inflater,container,false)

        toastutil = ToastUtil()
        val dialog = ProgessDialog.progressDialog(activity!!)


        binding.apply {

            name.addTextChangedListener {
                viewModel.event(RegisterFormEvent.NameChanged(it.toString()))
            }
            email.addTextChangedListener {
                viewModel.event(RegisterFormEvent.EmailChanged(it.toString()))
            }
            password.addTextChangedListener {
                viewModel.event(RegisterFormEvent.PasswordChanged(it.toString()))
            }
            repeatPassword.addTextChangedListener {
                viewModel.event(RegisterFormEvent.RepeatPassword(it.toString()))
            }

            button2.apply {
                setOnClickListener {
                    viewModel.event(RegisterFormEvent.Submit)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.validationEvents.collect{event->
                when(event){
                    RegisterState.Clear -> {
                        binding.apply {
                            textInputLayout3.isErrorEnabled = false
                            textInputLayout4.isErrorEnabled = false
                            textInputLayout5.isErrorEnabled = false
                            textInputLayout6.isErrorEnabled = false
                        }
                    }
                    is RegisterState.EmailError -> {
                        binding.textInputLayout3.apply {
                            error = event.message
                        }
                    }
                    is RegisterState.NameError -> {
                        binding.textInputLayout6.apply {
                            error = event.message
                        }
                    }
                    is RegisterState.PasswordError -> {
                        binding.textInputLayout4.apply {
                            error = event.message
                        }
                    }
                    is RegisterState.RepeatPasswordError -> {
                        binding.textInputLayout5.apply {
                            error = event.message
                        }
                    }
                    RegisterState.Success -> {
                        toastutil.showMessage("Success",activity)
                    }
                }
            }
        }

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.signUpFlow.collectLatest {
                    when (it) {

                        is Resource.Failure -> {
                            dialog.dismiss()
                            toastutil.showMessage("Error ${it.exception}",activity)

                        }
                        Resource.Loading -> {
                            dialog.show()
                        }
                        is Resource.Success -> {
                            dialog.dismiss()
                            toastutil.showMessage("Success",activity)

                            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
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