package com.example.saviri.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saviri.data.ShoppingItem
import com.example.saviri.databinding.HomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding :HomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = HomeBinding.inflate(inflater,container,false)
        val shoppingAdapter = ShoppingAdapter(getList())
        binding.apply {
            recyclerView.apply {
            adapter = shoppingAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
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