package com.example.saviri.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.saviri.R
import com.example.saviri.databinding.FragmentRegisterGrafmentBinding


class RegisterFragment : Fragment() {


    private lateinit var binding: FragmentRegisterGrafmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterGrafmentBinding.inflate(inflater,container,false)

        binding.login.apply {
            setOnClickListener {
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
            }
        }

        return binding.root
    }

}