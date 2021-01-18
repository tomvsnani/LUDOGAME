package com.example.ludo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ludo.databinding.CoinsAlertDialogLayoutBinding
import com.example.ludo.databinding.CoinsSelectDialogRowLayoutBinding
import com.example.ludo.databinding.ProfileCoinsRecyclerRowLayoutBinding

class CoinsDialogAdapter :
    ListAdapter<CoinsModelClass, CoinsDialogAdapter.CoinsViewHolder>(
        CoinsModelClass.diff
    ) {

    var mutableLiveDataSelectedCoins:MutableLiveData<String>?=null
    var selectedItemPosition = -1

    inner class CoinsViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var binding = CoinsSelectDialogRowLayoutBinding.bind(view)

        init {

            binding.radioButton.setOnClickListener {
                selectedItemPosition = adapterPosition
                mutableLiveDataSelectedCoins?.value=currentList[adapterPosition].coinValue
                notifyDataSetChanged()
            }
        }

    }

    fun setLiveDataObserver(livedata:MutableLiveData<String>){
        this.mutableLiveDataSelectedCoins=livedata
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsViewHolder {
        return CoinsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.coins_select_dialog_row_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CoinsViewHolder, position: Int) {
        var model = currentList[position]

        holder.binding.radioButton.isChecked =
            selectedItemPosition != -1 && holder.adapterPosition == selectedItemPosition

        holder.binding.coinsvalueTextView.text = model.coinValue


    }
}