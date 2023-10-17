package com.example.saviri.ui.choosecountry

import android.os.Bundle
import android.service.chooser.ChooserAction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.saviri.databinding.FragmentChooseCountryBinding

class fragment_countryList: Fragment() {

    private lateinit var binding: FragmentChooseCountryBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseCountryBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}