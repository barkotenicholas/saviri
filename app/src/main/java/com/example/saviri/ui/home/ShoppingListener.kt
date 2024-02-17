package com.example.saviri.ui.home

import com.example.saviri.data.ShoppingItem

interface ShoppingListener {

    fun onItemAdd(shoppingItems:Array<ShoppingItem>)

    fun onItemQuantityAdd(shoppingItems:Array<ShoppingItem>)

    fun onItemClickEdit(shoppingItems:Array<ShoppingItem>,index:Int)
}