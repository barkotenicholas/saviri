package com.example.saviri.ui.home

import android.annotation.SuppressLint
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
import com.example.saviri.util.Conversion
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.RoundingMode

class HomeFragment : Fragment() , ShoppingListener{

    private lateinit var binding :HomeBinding
    private val viewModel : HomeViewModel by viewModels { HomeViewModel.Factory  }
    private val args:HomeFragmentArgs by navArgs()
    private  var shoppingItem: ShoppingItem? = null
    private var conversion:Conversion? = null
    private lateinit var shoppingAdapter:ShoppingAdapter
    private lateinit var shoppingId:String
    private lateinit var shoppingListName:String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if(this::binding.isInitialized.not()){
            binding = HomeBinding.inflate(inflater,container,false)
            shoppingAdapter = ShoppingAdapter(viewModel.stateCartItems.value,this)
        }


        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        shoppingItem = args.item
        val shoppingItems = args.shoplist
        shoppingId = args.shoppinglistid
        val shopingIndex = args.shopingindex
        conversion = args.values
        shoppingListName = args.shopingListName
        Log.d("TAGTAGTAG", "onViewCreated: $conversion ")
        if(conversion != null){
            viewModel.getLatestCurrency(conversion!!,shoppingAdapter.getTotal(),shoppingId,shoppingListName)
        }

        if (shopingIndex != -1 ){
            Log.d("TAG", "asdasdasdasdsadasdasdewr: $shopingIndex  --- ${shoppingItems.get(shopingIndex)}")
            Log.d("TAG", "sdf: $shopingIndex  --- ${shoppingItem}")
            if(shoppingItem == shoppingItems.get(shopingIndex)){
                Log.d("***********************************", "onViewCreated: same object no need to upgrade")

            }else{
                viewModel.addItemsToCart(AddCartEvent.EditCartITem(shoppingItem!!))
                shoppingItems.set(shopingIndex, shoppingItem);

                viewModel.addItemsToCart(AddCartEvent.AddAllItems(shoppingItems))

            }
        }
        if(shopingIndex == -1){
            viewModel.addItemsToCart(AddCartEvent.AddAllItems(shoppingItems))
        }




        if (shoppingItem != null && shopingIndex == -1) {
            viewModel.addItemsToCart(AddCartEvent.CartChanged(shoppingItem!!))
            updateTotal(shoppingAdapter.getTotal())
        }


        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_cart,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.add_Item_cart->{
                        val list = viewModel.stateCartItems.value.toTypedArray()
                        val shopinglist = viewModel.shoppinglistid.value
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragment2ToAddCartFragment2(
                            list,
                            conversion!!,
                            shopinglist,
                            shoppingListName
                        ,-1
                        ))
                        return true
                    }
                }
                return false
            }

        },viewLifecycleOwner)

        lifecycleScope.launch {
            viewModel.stateCartItems.collect{ items->
                Log.d("}}}}}}}", "onViewCreated: ${items.size} ")
            }

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
                    Log.d("qwerty", "onViewCreated: ${viewModel.stateCartItems.value} ")
                }
            }
        }


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
                        updateTotal(shoppingAdapter.getTotal())

                    }
                }
            ItemTouchHelper(simpleItemTouchCallback)
        }

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        lifecycleScope.launch {
            viewModel.currencyvalidationEvents.collect{
                when(it){
                    CurrencyState.Clear -> {

                    }
                    is CurrencyState.CurrencyConverted -> {
                        val currencyData = it.convertedData.toBigDecimal().setScale(2,RoundingMode.DOWN)
                        binding.textView7.apply {
                            text = currencyData.toString()
                        }
                    }
                    is CurrencyState.CurrencyError -> {

                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.addCartEventChannel.collectLatest {
                when (it){
                    is AddCartState.CartState -> {
                        shoppingAdapter.notifyItemInserted(it.position)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.currencyState.collectLatest {
                binding.exchangeRate.text = it.currencyRate
                var a = ""

                var stringnumber = it.currencyRate

                var foreignMoneyToEuro = it.foreignCurrencyToEuro
                var localCurrencyToEuro = it.homeCurrencyToEuro

                if(stringnumber.isBlank()){
                    a = "0"
                }else{
                    a = stringnumber
                }
                var doublecurrency = a.toDouble()
                val homecurrency = doublecurrency * shoppingAdapter.getTotal()
                binding.textView7.text = homecurrency.toString()


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


    override fun onItemAdd(shoppingItems: Array<ShoppingItem>) {
        updateTotal(shoppingAdapter.getTotal())
        viewModel.getLatestCurrency(
            conversion!!,
            shoppingAdapter.getTotal(),
            shoppingId,
            shoppingListName
        )

    }

    override fun onItemQuantityAdd(shoppingItems: Array<ShoppingItem>) {
        updateTotal(shoppingAdapter.getTotal())
    }

    override fun onItemClickEdit(shoppingItems: Array<ShoppingItem>, index: Int) {
        val shopinglist = viewModel.shoppinglistid.value
        findNavController().navigate(HomeFragmentDirections.actionHomeFragment2ToAddCartFragment2(
            shoppingItems,
            conversion!!,
            shopinglist,
            shoppingListName,
            index
        ))
    }

}