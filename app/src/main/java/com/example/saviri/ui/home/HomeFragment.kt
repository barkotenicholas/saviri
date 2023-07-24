package com.example.saviri.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.saviri.databinding.HomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding :HomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = HomeBinding.inflate(inflater,container,false)



        return binding.root

    }

}