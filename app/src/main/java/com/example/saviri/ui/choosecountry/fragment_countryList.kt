package com.example.saviri.ui.choosecountry

import android.os.Build
import android.os.Bundle
import android.service.chooser.ChooserAction
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.saviri.R
import com.example.saviri.databinding.FragmentChooseCountryBinding
import com.example.saviri.network.models.Symbols
import com.example.saviri.ui.home.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log

class fragment_countryList: Fragment() {

    private lateinit var binding: FragmentChooseCountryBinding
    private val viewModel: ChooseCountryViewModel by viewModels { ChooseCountryViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseCountryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.getCountries()
        }


        lifecycleScope.launch {
            viewModel.validationchannel.collectLatest {
                when (it) {
                    is ChooseCountryState.OnDataRecieve -> {
                        Log.i("fragment home", "onViewCreated: ${it.country} ")
                        val supported_currencies = it.country.symbols!!.getList()
                        Log.i("/*/*/*/*/", "onViewCreated: ${supported_currencies::class.java.typeName}")

                        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.drop_down_menu_item,supported_currencies)

                        binding.autoCompleteTextView.setAdapter(arrayAdapter)
                    }
                }
            }
        }
    }
}
