package com.example.ludo

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ludo.databinding.CoinsAlertDialogLayoutBinding
import com.example.ludo.databinding.FragmentCoinsBinding


class CoinsFragment : Fragment(R.layout.fragment_coins) {

    lateinit var binding: FragmentCoinsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCoinsBinding.bind(view)
        var adapter = ProfileCoinsAdapter()
        binding.playercoinsrecycler.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = adapter

        }
        adapter.submitList(
            listOf(
                UserProfileCoinsModelClass("1", "ramu", "525", true)
                ,
                UserProfileCoinsModelClass("1", "rebba", "425", false)
            )
        )

        binding.selectcoinsbutton.setOnClickListener {
            var dialog = AlertDialog.Builder(context).create()
            var view1 = dialog.layoutInflater.inflate(
                R.layout.coins_alert_dialog_layout,
                null,
                false
            )
            dialog.setView(
                view1

            )
            var binding = CoinsAlertDialogLayoutBinding.bind(view1)

            var livedataListenerForSelectedCoin = MutableLiveData<String>()
            livedataListenerForSelectedCoin.observe(viewLifecycleOwner,
                Observer<String> {
                    this.binding.apply {
                        cardView.setCardBackgroundColor(resources.getColor(R.color.purple_200))
                        selectcoinsbutton.text = it+" Coins"
                    }

                })
            binding.selectcoinbutton.setOnClickListener {

                dialog.dismiss()
            }
            binding.coinsselectrecycler.apply {
                var coinsAdapter = CoinsDialogAdapter()
                coinsAdapter.setLiveDataObserver(livedataListenerForSelectedCoin)
                layoutManager = LinearLayoutManager(context)
                this.adapter = coinsAdapter
                coinsAdapter.submitList(
                    listOf(
                        CoinsModelClass("1", "350"),
                        CoinsModelClass("2", "450"),
                        CoinsModelClass("11", "350"),
                        CoinsModelClass("21", "450"),
                        CoinsModelClass("12", "350"),
                        CoinsModelClass("23", "450"),
                        CoinsModelClass("14", "350")

                    )
                )


            }
            dialog.window?.decorView?.rootView?.apply {
                setBackgroundColor(Color.TRANSPARENT)

                viewTreeObserver.addOnGlobalLayoutListener {
                    layoutParams?.width = binding?.root?.width - 500
                }
            }


            dialog.show()
        }
        super.onViewCreated(view, savedInstanceState)
    }


}