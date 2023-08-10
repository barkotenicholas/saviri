package com.example.saviri.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saviri.data.ShoppingItem
import com.example.saviri.databinding.HomeBinding
import com.example.saviri.ui.AddCart.AddCartFragment
import com.example.saviri.ui.login.LoginViewModel


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
        val shoppingAdapter = ShoppingAdapter(getList())

        shoppingItem = args.info

        Log.d("TAG", "onCreateView:------------------------------$shoppingItem")

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



        }


        return binding.root

    }


    fun getList():List<ShoppingItem>{
        return listOf(
            ShoppingItem("beer",20.0,8),
            ShoppingItem("beer",20.0,8),
            ShoppingItem("beer",20.0,8),
            ShoppingItem("beer",20.0,8)


        )
    }

}