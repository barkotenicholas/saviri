package com.example.saviri.ui.AllShoppingList

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saviri.data.HomeShoppingList
import com.example.saviri.databinding.AllshoppingitemBinding
import kotlin.math.log

class  AllShoppingAdapter(
    private var items:MutableList<HomeShoppingList> ,
    var listener:AllShoppingItemListener
    ): RecyclerView.Adapter<AllShoppingAdapter.AllShoppingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllShoppingViewHolder {

        val binding = AllshoppingitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AllShoppingViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: AllShoppingViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item,position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun itemsInserted(newValues:MutableList<HomeShoppingList>){
        items.clear()
        items.addAll(newValues)
        notifyDataSetChanged()
    }

    inner class AllShoppingViewHolder(private val binding: AllshoppingitemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item:HomeShoppingList,position: Int): Unit {

            binding.apply {
                shoppingparent.apply {
                    setOnClickListener {
                        listener.onShoppingItemSelectListener(item)
                    }
                }
                shopingListname.apply {
                    text = item.name

                }
            }
        }
    }
}