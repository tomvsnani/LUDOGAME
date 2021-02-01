package com.example.ludo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ludo.databinding.CoinsSelectDialogRowLayoutBinding
import com.example.ludo.databinding.StoriesRowLayoutBinding

class StoriesAdapter :
    ListAdapter<StoriesRow, StoriesAdapter.CoinsViewHolder>(
        StoriesRow.diff
    ) {

    inner class CoinsViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var binding = StoriesRowLayoutBinding.bind(view)



    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsViewHolder {
        return CoinsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.stories_row_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CoinsViewHolder, position: Int) {
        var model = currentList[position]

        holder.binding.storiestextview.text=model.text

       Glide.with(holder.binding.root.context).load(Constants.BASEURL+model.image).into( holder.binding.storiesimageview)


    }
}