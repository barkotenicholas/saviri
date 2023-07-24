package com.example.saviri.ui.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.saviri.R
import com.example.saviri.data.Resource
import com.example.saviri.databinding.FragmentLoginBinding
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

        binding.apply {
            button.setOnClickListener {
                viewModel.login(binding.loginemail.text.toString().trim(),binding.loginpass.text.toString().trim())
            }
        }

        lifecycleScope.launch{

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.loginFlow.collectLatest {
                    when(it){
                        is Resource.Failure -> {
                            Log.d("TAG", "onCreateView: ............................... ${it.exception.message}")
                        }
                        Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            Log.d("TAG", ".............................................................................................................")


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