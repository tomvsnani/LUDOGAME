package com.example.ludo

import androidx.recyclerview.widget.DiffUtil

data class CoinsModelClass(var id:String="",var coinValue:String=""){
    companion object{
        var diff= object : DiffUtil.ItemCallback<CoinsModelClass>() {
            override fun areItemsTheSame(
                oldItem: CoinsModelClass,
                newItem: CoinsModelClass
            ): Boolean {
             return  oldItem.id==newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CoinsModelClass,
                newItem: CoinsModelClass
            ): Boolean {
               return  oldItem.coinValue==newItem.coinValue
            }
        }
    }
}