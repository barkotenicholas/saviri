package com.example.saviri.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.saviri.data.Resource
import com.example.saviri.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private val viewModel : LoginViewModel by viewModels { LoginViewModel.Factory  }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater,container,false)


        lifecycleScope.launch {
            viewModel.loginFlow.value.let {
                when(it){
                    is Resource.Failure -> TODO()
                    Resource.Loading -> TODO()
                    is Resource.Success -> {
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment2())
                    }
                    null -> TODO()
                }
            }
        }


        return binding.root
    }


}