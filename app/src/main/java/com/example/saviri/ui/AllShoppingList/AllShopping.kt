package com.example.saviri.ui.AllShoppingList

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saviri.data.HomeShoppingList
import com.example.saviri.databinding.FragmentAllShoppingBinding
import com.example.saviri.ui.home.ShoppingAdapter
import com.example.saviri.util.ProgessDialog
import com.example.saviri.util.ToastUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AllShopping : Fragment() ,AllShoppingItemListener{

    private lateinit var binding: FragmentAllShoppingBinding
    private val viewModel : AllShoppingViewModel by viewModels { AllShoppingViewModel.Factory }
    private lateinit var listAdapter: AllShoppingAdapter
    private lateinit  var toastutil: ToastUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAllShoppingBinding.inflate(inflater,container,false)
        toastutil = ToastUtil()

        val dialog = ProgessDialog.progressDialog(activity!!)

        listAdapter = AllShoppingAdapter(viewModel.stateCartItems.value,this)

        lifecycleScope.launch {
            viewModel.loadData()
        }

        binding.apply {

            shoppinglist.apply {

            }

            createnewshopinglist.apply {
                setOnClickListener {
                    var action = AllShoppingDirections.actionAllShoppingToFragmentCountryList()
                    findNavController().navigate(action)
                }
            }

            shoppinglist.apply{
                adapter = listAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)

            }
        }
        lifecycleScope.launch {
            viewModel.loadingEvent.collectLatest {
                when(it){

                    AllShoppingViewModel.AllShoppingState.Loading -> {
                        Log.d("TasdasdasAG", "onCreateView: ****************")
                    }
                    is AllShoppingViewModel.AllShoppingState.Success -> {

                        if(it.loading){
                            Log.d("start dialog", "onCreateView: ")
                            dialog.show()
                        }else{
                            Log.d("dismiss dialog", "onCreateView: ")

                            dialog.dismiss()
                        }

                    }
                    is AllShoppingViewModel.AllShoppingState.ReceivedData -> {
                        Log.d("*********", "recived data ")
                        dialog.dismiss()
                        if(it.mutableList.size>0){
                            listAdapter.itemsInserted(it.mutableList)
                        }else{
                            toastutil.showMessage("You have no Shopping items please add",activity)
                        }
                    }
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

    override fun onShoppingItemSelectListener(item: HomeShoppingList) {

        val action = AllShoppingDirections.actionAllShoppingToHomeFragment2(item.shoppinglist.toTypedArray(),null,item.id,item.name)

        findNavController().navigate(action)

    }
}