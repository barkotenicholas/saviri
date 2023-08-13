package com.example.saviri.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saviri.data.ShoppingItem
import com.example.saviri.databinding.ShopingItemBinding

class ShoppingAdapter (
    private var items:MutableList<ShoppingItem>,
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
        holder.bind(item,position)
    }

    override fun getItemCount() = items.size

    inner class ShoppingViewHolder(private val binding: ShopingItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingItem,position: Int){
            binding.apply {
                itemName.text = item.name
                itemAmount.text = item.quantity.toString()

                addQuantity.apply {
                    setOnClickListener {

                        items[position] = item.copy(quantity = item.quantity+1)
                        notifyItemChanged(position)

                    }
                }

                minusQuantity.apply {
                    setOnClickListener {

                        items[position] = item.copy(quantity = if(item.quantity == 0) 0 else item.quantity-1)
                        notifyItemChanged(position)

                    }
                }

            }
        }
    }

}