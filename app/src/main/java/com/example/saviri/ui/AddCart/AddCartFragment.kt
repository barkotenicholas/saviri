package com.example.saviri.ui.AddCart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.saviri.databinding.CartFragmentBinding

class AddCartFragment : Fragment() {

    private lateinit var binding: CartFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = CartFragmentBinding.inflate(inflater,container,false)




        return binding.root
    }
}