package com.example.saviri.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saviri.data.ShoppingItem
import com.example.saviri.databinding.ShopingItemBinding

class ShoppingAdapter (
    var items:List<ShoppingItem>,
    ) : RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingAdapter.ShoppingViewHolder {
        val binding = ShopingItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ShoppingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingAdapter.ShoppingViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    class ShoppingViewHolder(private val binding: ShopingItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingItem){
            binding.apply {
                itemName.text = item.name
                itemAmount.text = item.quantity.toString()
            }
        }
    }

}