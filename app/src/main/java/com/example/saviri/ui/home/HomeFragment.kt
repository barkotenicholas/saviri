package com.example.saviri.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.saviri.data.ShoppingItem
import com.example.saviri.databinding.HomeBinding
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

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = getList()
        val shoppingAdapter = ShoppingAdapter(items)

        shoppingItem = args.info
        if (shoppingItem != null) {
            items.add(shoppingItem!!)
            var position = items.indexOf(shoppingItem)
            shoppingAdapter.notifyItemInserted(position)
        }

        binding.apply {
            recyclerView.apply {
                adapter = shoppingAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            floatingActionButton.apply {
                setOnClickListener {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragment2ToAddCartFragment2())
                }
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

    fun getList(): MutableList<ShoppingItem> {
        return mutableListOf(
            ShoppingItem("beer", 20.0, 8),
            ShoppingItem("beer", 20.0, 8),
            ShoppingItem("beer", 20.0, 8),
            ShoppingItem("beer", 20.0, 8)
        )
    }

}