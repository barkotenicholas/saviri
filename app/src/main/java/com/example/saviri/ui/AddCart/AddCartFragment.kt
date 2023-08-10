package com.example.saviri.ui.AddCart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.saviri.databinding.CartFragmentBinding
import com.example.saviri.ui.home.CartFormEvent
import com.example.saviri.ui.home.CartState
import com.example.saviri.ui.home.HomeViewModel
import kotlinx.coroutines.launch

class AddCartFragment : Fragment() {

    private lateinit var binding: CartFragmentBinding
    private val viewModel : HomeViewModel by viewModels { HomeViewModel.Factory  }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = CartFragmentBinding.inflate(inflater,container,false)

        binding.apply {

            itemNamId.apply {
                addTextChangedListener {
                    viewModel.event(CartFormEvent.ItemNameChanged(it.toString()))
                }
            }
            itemPriceId.apply {
                addTextChangedListener {
                    viewModel.event(CartFormEvent.ItemPriceChanged(it.toString()))
                }
            }

            saveEdit.apply {
                setOnClickListener {
                    viewModel.event(CartFormEvent.Submit)
                }
            }

        }

        lifecycleScope.launch {
            viewModel.cartValidationChannel.collect{event->
                when(event){
                    CartState.Clear -> {

                        binding.itemName.apply {
                            isErrorEnabled = false
                        }
                        binding.itemPrice.apply {
                            isErrorEnabled = false
                        }

                    }
                    is CartState.ItemNameError -> {
                        Log.d("TAG", "Home: ------")

                        binding.itemName.apply {
                            isErrorEnabled = true

                            error = event.message
                        }
                    }
                    is CartState.ItemPriceError -> {
                        binding.itemPrice.apply {
                            isErrorEnabled = true

                            error = event.message
                        }
                    }
                    is CartState.Success -> {

                        val passData = event.item
                        Log.d("TAG", "onCreateView: $passData")
                        val action = AddCartFragmentDirections.actionAddCartFragment2ToHomeFragment2().setInfo(passData)
                        findNavController().navigate(action)
                    }
                }
            }
        }
        return binding.root
    }
}