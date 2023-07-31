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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {


    private lateinit var binding: FragmentRegisterGrafmentBinding

    private val viewModel : RegisterViewModel by viewModels{ RegisterViewModel.Factory}
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterGrafmentBinding.inflate(inflater,container,false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            findNavController().navigateUp()
        }

        binding.apply {
            button2.apply {
                isEnabled = false
            }
            name.addTextChangedListener {
                viewModel.setName(it.toString())
            }
            email.addTextChangedListener {
                viewModel.setEmail(it.toString())
            }
            password.addTextChangedListener {
                viewModel.setPassword(it.toString())
            }
            repeatPassword.addTextChangedListener {
                viewModel.setRepeatPassword(it.toString())
            }

            button2.apply {
                setOnClickListener {
                    viewModel.signUp()
                }
            }
        }

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.signUpFlow.collectLatest {
                    when (it) {

                        is Resource.Failure -> {
                            Log.d("TAG", "onCreateView: errrorrr ...................")
                        }
                        Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                        }
                        null -> {

                        }
                    }
                }

                viewModel.isSubmitEnabled.collect { value ->
                    Log.d("TAG", "onCreateView: ${value}")
                    binding.button2.isEnabled = true
                }

            }

        }



        return binding.root
    }

}