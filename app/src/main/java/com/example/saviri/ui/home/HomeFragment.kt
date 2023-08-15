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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                    }


                }
            ItemTouchHelper(simpleItemTouchCallback)
        }

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)


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