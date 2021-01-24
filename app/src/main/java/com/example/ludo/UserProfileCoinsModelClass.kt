package com.example.ludo

import androidx.recyclerview.widget.DiffUtil

data class UserProfileCoinsModelClass(
    var id: String = "",
    var name: String = "",
    var coins: String = "",
    var isplaying: Boolean=false
) {

    companion object {
        var diff = object : DiffUtil.ItemCallback<UserProfileCoinsModelClass>() {
            override fun areItemsTheSame(
                oldItem: UserProfileCoinsModelClass,
                newItem: UserProfileCoinsModelClass
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: UserProfileCoinsModelClass,
                newItem: UserProfileCoinsModelClass
            ): Boolean {
                return oldItem.isplaying == newItem.isplaying && oldItem.name == newItem.name && oldItem.coins == newItem.coins
            }
        }
    }
}