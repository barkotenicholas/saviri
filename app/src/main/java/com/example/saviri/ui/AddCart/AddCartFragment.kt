package com.example.saviri.ui.AddCart

import android.annotation.SuppressLint
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
import com.example.saviri.data.ShoppingItem
import com.example.saviri.databinding.CartFragmentBinding
import com.example.saviri.ui.home.CartFormEvent
import com.example.saviri.ui.home.CartState
import com.example.saviri.ui.home.HomeViewModel
import kotlinx.coroutines.launch

class AddCartFragment : Fragment() {

    private lateinit var binding: CartFragmentBinding
    private val viewModel : HomeViewModel by viewModels { HomeViewModel.Factory  }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = CartFragmentBinding.inflate(inflater,container,false)

        var args = AddCartFragmentArgs.fromBundle(arguments!!)
        val shoppnglist = args.shoppinglist
        val conversion = args.converstion
        val shoppingListId = args.shoppinglistid
        val shoppinglistname = args.shopinglistname
        val shoppingIndex = args.shoppingIndex
        var item: ShoppingItem? = null
        if(shoppingIndex >= 0 ){
         item = shoppnglist.get(shoppingIndex)


            Log.d("TasfeaAG", "onCreateView: $item ")
        }

        Log.d("TAG", "onCreateView: ${args.shoppinglist.size} ")
        Log.d("TAG", "onCreateView: ${args.converstion.convertFrom} ")

        if(item != null){
            binding.apply {


                itemNamId.apply {
                    setText(item.name)
                    viewModel.event(CartFormEvent.ItemNameChanged(item.name))

                }
                itemPriceId.apply {

                    setText(item.price.toInt().toString())
                    viewModel.event(CartFormEvent.ItemPriceChanged(item.price.toInt().toString()))

                }

                textView2.apply{
                    text = "Edit Cart"
                }
                saveEdit.apply {
                    text = "Edit"
                }

            }
        }
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
                        val x = passData.copy(key = item?.key)
                        Log.d("TAG", "onCreateView: $passData")
                        val action = AddCartFragmentDirections.actionAddCartFragment2ToHomeFragment2(conversion,shoppnglist,x,shoppingListId,shoppinglistname,shoppingIndex)
                        findNavController().navigate(action)
                    }
                }
            }
        }
        return binding.root
    }
}