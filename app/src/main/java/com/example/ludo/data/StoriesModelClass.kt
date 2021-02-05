package com.example.ludo.data

import androidx.recyclerview.widget.DiffUtil

data class StoriesModelClass(
    var status: String = "",
    var message: String = "",
    var data: List<StoriesRow>

)
{

}

data class StoriesRow(var text:String,var image:String,  var id:String){
    companion object{
        var diff= object : DiffUtil.ItemCallback<StoriesRow>() {
            override fun areItemsTheSame(
                oldItem: StoriesRow,
                newItem: StoriesRow
            ): Boolean {
                return  oldItem.id==newItem.id
            }

            override fun areContentsTheSame(
                oldItem: StoriesRow,
                newItem: StoriesRow
            ): Boolean {
                return  oldItem==newItem
            }
        }
    }
}