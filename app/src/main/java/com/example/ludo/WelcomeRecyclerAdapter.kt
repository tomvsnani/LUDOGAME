package com.example.ludo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ludo.databinding.ProfileCoinsRecyclerRowLayoutBinding
import com.example.ludo.databinding.WelcomeLudoRecyclerRowLayoutBinding

class WelcomeRecyclerAdapter :
    ListAdapter<String, WelcomeRecyclerAdapter.ProfileCoinsViewHolder>(
        object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
             return  oldItem==newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
               return oldItem==newItem
            }
        }
    ) {

    inner class ProfileCoinsViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var binding=WelcomeLudoRecyclerRowLayoutBinding.bind(view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileCoinsViewHolder {
        return ProfileCoinsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.welcome_ludo_recycler_row_layout, parent,false)
        )
    }

    override fun onBindViewHolder(holder: ProfileCoinsViewHolder, position: Int) {
//        var model=currentList[position]
//
//      holder.binding.rowtextview.text=model


    }

    override fun getItemCount(): Int {
        return 3
    }
}