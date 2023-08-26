package com.example.saviri.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saviri.R
import com.example.saviri.data.ShoppingItem
import com.example.saviri.databinding.HomeBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding :HomeBinding
    private val viewModel : HomeViewModel by viewModels { HomeViewModel.Factory  }
    private val args:HomeFragmentArgs by navArgs()
    private  var shoppingItem: ShoppingItem? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = HomeBinding.inflate(inflater,container,false)

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_cart,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.add_Item_cart->{
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragment2ToAddCartFragment2())
                        return true
                    }
                }
            return false
            }

        },viewLifecycleOwner)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shoppingAdapter = ShoppingAdapter(viewModel.stateCartItems.value)

        shoppingItem = args.info
        if (shoppingItem != null) {
            viewModel.addItemsToCart(AddCartEvent.CartChanged(shoppingItem!!))
        }

        binding.apply {
            recyclerView.apply {
                adapter = shoppingAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            currencyRate.apply {
                addTextChangedListener {
                    viewModel.currencyEvent(CurrencyFormEvent.CurrencyChanged(it.toString()))
                }
            }

            buttonsave.apply {
                setOnClickListener {
                    viewModel.currencyEvent(CurrencyFormEvent.submit)
                }
            }
        }
        updateTotal(shoppingAdapter.getTotal())


        val itemTouchHelper by lazy {
            val simpleItemTouchCallback =
                object : ItemTouchHelper.SimpleCallback(0, RIGHT or LEFT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        TODO("Not yet implemented")
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        shoppingAdapter.remove(position)
                    }
                }
            ItemTouchHelper(simpleItemTouchCallback)
        }

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)



        lifecycleScope.launch {
            viewModel.addCartEventChannel.collectLatest {
                when (it){
                    is AddCartState.CartState -> {
                        Log.d("TAG", "onViewCreated:---------------------------------- ")
                        shoppingAdapter.notifyItemInserted(it.position)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.currencyState.collectLatest {
                binding.exchangeRate.text = it.currencyRate
            }
        }
        lifecycleScope.launch {
            viewModel.currencyState.collectLatest {
                binding.textInputLayout7.apply {
                    error = it.currencyRateError
                }
            }
        }

        lifecycleScope.launch {
            viewModel.currencyState.collectLatest {
                if(!it.hasError){
                    binding.textInputLayout7.apply {
                        isErrorEnabled = it.hasError
                    }
                }
            }
        }


    }

    private fun updateTotal(total: Double) {
        binding.totalValue.text = total.toString()
    }

}