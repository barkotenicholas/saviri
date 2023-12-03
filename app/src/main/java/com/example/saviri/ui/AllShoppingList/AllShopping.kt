package com.example.saviri.ui.AllShoppingList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.saviri.databinding.FragmentAllShoppingBinding



class AllShopping : Fragment() {

    private lateinit var binding: FragmentAllShoppingBinding
    private val viewModel : AllShoppingViewModel by viewModels { AllShoppingViewModel.Factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAllShoppingBinding.inflate(inflater,container,false)



        binding.apply {


            createnewshopinglist.apply {
                setOnClickListener {
                    var action = AllShoppingDirections.actionAllShoppingToFragmentCountryList()
                    findNavController().navigate(action)
                }
            }
        }


        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllShopping().apply {

            }
    }
}