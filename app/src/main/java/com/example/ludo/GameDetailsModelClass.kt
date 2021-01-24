package com.example.ludo

import androidx.recyclerview.widget.DiffUtil

data class GameDetailsModelClass(
    var id: String,
    var host_id: String,
    var entry_fee: String,
    var player_name: String,
    var game_status: String,
    var host_name: String

) {
    companion object {
        var diff = object : DiffUtil.ItemCallback<GameDetailsModelClass>() {
            override fun areItemsTheSame(
                oldItem: GameDetailsModelClass,
                newItem: GameDetailsModelClass
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: GameDetailsModelClass,
                newItem: GameDetailsModelClass
            ): Boolean {
                return oldItem.host_id == newItem.host_id && oldItem.entry_fee == newItem.entry_fee &&
                        oldItem.game_status == newItem.game_status
            }
        }
    }
}