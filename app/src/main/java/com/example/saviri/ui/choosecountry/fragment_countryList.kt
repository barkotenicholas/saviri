package com.example.saviri.ui.choosecountry

import android.os.Build
import android.os.Bundle
import android.service.chooser.ChooserAction
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.saviri.R
import com.example.saviri.data.ShoppingItem
import com.example.saviri.databinding.FragmentChooseCountryBinding
import com.example.saviri.network.models.Symbols
import com.example.saviri.ui.home.HomeViewModel
import com.example.saviri.util.Conversion
import com.example.saviri.util.ProgessDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log

class fragment_countryList: Fragment() {

    private lateinit var binding: FragmentChooseCountryBinding
    private val viewModel: ChooseCountryViewModel by viewModels { ChooseCountryViewModel.Factory }


    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.getCountries()
        }
    }
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


        val dialog = ProgessDialog.progressDialog(activity!!)

        binding.choosecountry.apply {
            setOnClickListener {

                val isValid = validateInputs(binding.autoCompleteTextView.text.toString(),binding.autoCompleteTextView1.text.toString(),binding.inputshopinglistname.text.toString())

                if(isValid){
                    var data = Conversion(binding.autoCompleteTextView.text.toString(),binding.autoCompleteTextView1.text.toString())
                    var shopinglistname =binding.inputshopinglistname.text.toString()
                    var action = fragment_countryListDirections.actionFragmentCountryListToHomeFragment2(
                        emptyArray(),null,"",shopinglistname
                    ).setValues(data)
                    findNavController().navigate(action)
                }

            }
        }

        lifecycleScope.launch {
            viewModel.validationchannel.collectLatest {
                when (it) {
                    is ChooseCountryState.OnDataRecieve -> {
                        dialog.dismiss()
                        val supported_currencies = it.country.symbols!!.getList()

                        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.drop_down_menu_item,supported_currencies)

                        binding.autoCompleteTextView.setAdapter(arrayAdapter)
                        binding.autoCompleteTextView1.setAdapter(arrayAdapter)
                    }
                    ChooseCountryState.Loading -> {
                        dialog.show()
                    }
                    is ChooseCountryState.ShoppingNameError -> {

                    }
                }
            }
        }
    }

    fun validateInputs(currency1 : String , currency2: String,name: String): Boolean{

        if(currency1.isBlank() || currency1.isEmpty()){
            binding.spinnnerlayer.apply {
                error= "Please choose currency"
            }

            return false
        }
        if(currency2.isBlank() || currency2.isEmpty()){
            binding.textView5.apply {
                error= "Please choose currency"
            }
            return false
        }
        if(name.isBlank() || name.isEmpty()){
            binding.shopingListName.apply {
                error= "Please choose currency"
            }
            return false
        }
        return true
    }
}
