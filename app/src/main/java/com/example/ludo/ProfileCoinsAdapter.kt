package com.example.ludo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ludo.databinding.ProfileCoinsRecyclerRowLayoutBinding

class ProfileCoinsAdapter :
    ListAdapter<UserProfileCoinsModelClass, ProfileCoinsAdapter.ProfileCoinsViewHolder>(
        UserProfileCoinsModelClass.diff
    ) {

    inner class ProfileCoinsViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var binding=ProfileCoinsRecyclerRowLayoutBinding.bind(view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileCoinsViewHolder {
        return ProfileCoinsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.profile_coins_recycler_row_layout, parent,false)
        )
    }

    override fun onBindViewHolder(holder: ProfileCoinsViewHolder, position: Int) {
        var model=currentList[position]

            holder.binding.playorcancelbutton.apply {
                if(model.isplaying) {
                    setBackgroundColor(resources.getColor(R.color.red))
                    text = "Cancel"
                }
                else{
                    setBackgroundColor(resources.getColor(R.color.green))
                    text = "Play"
                }
            }

        holder.binding.nametextview.text=model.name
        holder.binding.availablecoinsTextView.text=model.coins+" Coins"


    }
}