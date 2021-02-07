package com.example.ludo.ui.fragments.coinsFragment

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ludo.*
import com.example.ludo.adapters.CoinsDialogAdapter
import com.example.ludo.adapters.ProfileCoinsAdapter
import com.example.ludo.data.*
import com.example.ludo.databinding.CoinsAlertDialogLayoutBinding
import com.example.ludo.databinding.FragmentCoinsBinding
import com.example.ludo.ui.activities.MainActivity
import com.example.ludo.ui.activities.SharedViewModel
import com.example.ludo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CoinsFragment : Fragment(R.layout.fragment_coins) {

    lateinit var binding: FragmentCoinsBinding
    var selectedCoins = 0
    var availableCoins = 0
    var gameType = ""
    private lateinit var profilecoinsAdapter: ProfileCoinsAdapter
    lateinit var dialog: AlertDialog

    val viewModel by viewModels<CoinsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {


            binding = FragmentCoinsBinding.bind(view)

            gameType = arguments?.getString("type", "")!!

            val viewmodelActivity by activityViewModels<SharedViewModel>()

            getUserCoins(viewmodelActivity)

            changeBgBasedOnGameType()

            profilecoinsAdapter = ProfileCoinsAdapter(requireActivity())

            profilecoinsAdapter.setViewModel(viewModel,this)

            setUpRecyclerView()

            getGamesList()

            binding.selectcoinsbutton.setOnClickListener {
                createAlertDialogForCoins()
            }

            super.onViewCreated(view, savedInstanceState)

        } catch (e: Exception) {
            Log.d("exceptionincoinsfr", e.toString())
        }
    }

    fun getGamesList() {


        getGameListBasedOnType()


    }


    private fun setUpRecyclerView() {
        binding.playercoinsrecycler.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = profilecoinsAdapter

        }
    }

    private fun changeBgBasedOnGameType() {
        if ((activity as MainActivity).gameType == Constants.SNAKEGAMETYPE) {
            (activity as MainActivity).binding.root.background =
                ResourcesCompat.getDrawable(resources, R.drawable.snakebg, null)
            (activity as MainActivity).setToolBarText("Snake Game")
        } else {
            (activity as MainActivity).binding.root.background =
                ResourcesCompat.getDrawable(resources, R.drawable.ludobg, null)
            (activity as MainActivity).setToolBarText("Ludo Game")
        }
    }

    private fun getUserCoins(
        viewmodelActivity: SharedViewModel
    ) {
        viewmodelActivity.getCoins((activity as MainActivity).getUserId())
            .observe(viewLifecycleOwner, Observer {


                when (it) {
                    is ResultSealedClass.Success -> {

                        if (it.data?.data != null) {

                            binding.mycoinstextview.text = "${it.data.data[0].wallet}  coins"
                            availableCoins = it.data.data[0].wallet.toInt()

                        } else {
                            binding.mycoinstextview.text = "0 Coins"
                            availableCoins = 0
                        }

                    }

                    is ResultSealedClass.Failure -> {

                        handleResponseFailure(it)
                    }
                }

            })
    }

    private fun createAlertDialogForCoins() {

        MutableLiveData<String>().also {

        }
        var dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext()).create()
        var view1 = LayoutInflater.from(context).inflate(
            R.layout.coins_alert_dialog_layout,
            null,
            false
        )
        dialogBuilder.setView(
            view1

        )


        var binding = CoinsAlertDialogLayoutBinding.bind(view1)

        var livedataListenerForSelectedCoin = MutableLiveData<String>()
        livedataListenerForSelectedCoin.observe(viewLifecycleOwner,
            Observer<String> {
                this.binding.apply {
                    cardView.setCardBackgroundColor(resources.getColor(R.color.purple_200))
                    selectcoinsbutton.text = it + " Coins"
                    selectedCoins = it.toInt()
                }

            })
        var coinsAdapter = CoinsDialogAdapter()
        binding.selectcoinbutton.setOnClickListener {


            dialogBuilder.dismiss()
        }
        binding.coinsselectrecycler.apply {

            coinsAdapter.setLiveDataObserver(livedataListenerForSelectedCoin)
            layoutManager = LinearLayoutManager(context)
            this.adapter = coinsAdapter


        }
        (activity as MainActivity).makeProgressVisible()

        viewModel.getAvailableCoinsToPlay().observe(viewLifecycleOwner, Observer {
            when (it) {
                is ResultSealedClass.Failure -> {
                    handleResponseFailure(it)
                    (activity as MainActivity).makeProgresssHide()
                }
                is ResultSealedClass.Success -> {
                    coinsAdapter.submitList(it.data?.data)
                    (activity as MainActivity).makeProgresssHide()
                }
            }
        })



        dialogBuilder.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)


        dialogBuilder.show()

    }


    override fun onStart() {

        binding.placeAgamebuton.setOnClickListener {

            when {
                selectedCoins > availableCoins || selectedCoins <= 0 || availableCoins <= 0 -> {
                    Toast.makeText(
                        context,
                        "Your coins are less . Please recharge and try again",
                        Toast.LENGTH_SHORT
                    ).show()

                    (activity as MainActivity).displayGeneralAlertDialog(
                        requireContext().getString(R.string.dont_have_enough_coins_dialog),
                        R.drawable.oops
                    )
                }

                else -> {
                    hostAGame()
                }
            }


        }




        super.onStart()
    }

    private fun hostAGame() {

        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        val id = activity?.getPreferences(Activity.MODE_PRIVATE)
            ?.getString(Constants.USERIDCONSTANT, "")!!
        val name = activity?.getPreferences(Activity.MODE_PRIVATE)
            ?.getString(Constants.USERNAMECONSTANT, "")!!

        viewModel.hostGame((activity as MainActivity).gameType, id, name, selectedCoins.toString())
            .observe(viewLifecycleOwner,
                Observer {
                    when (it) {
                        is ResultSealedClass.Failure -> handleResponseFailure(it)
                        is ResultSealedClass.Success -> getGamesList()
                    }
                })


    }


    fun getGameListBasedOnType() {
        (activity as MainActivity).makeProgressVisible()
        viewModel.getGamesList((activity as MainActivity).gameType).observe(viewLifecycleOwner,
            Observer {

                when (it) {
                    is ResultSealedClass.Success -> {
                        (activity as MainActivity).makeProgresssHide()
                        profilecoinsAdapter.submitList(it.data?.filter { it1 ->
                            it1.game_status == "0" || isUserHostOrPlayer(
                                it1
                            )   //filtering to display game only to host or user if
                        })
                    }

                    is ResultSealedClass.Failure -> {
                        (activity as MainActivity).makeProgresssHide()
                        handleResponseFailure(it)
                    }
                }
            })

    }


    private fun isUserHostOrPlayer(it: GameDetailsModelClass) =
        (it.host_id == (activity as MainActivity).getUserId() ||
                it.player_id == (activity as MainActivity).getUserId())


     fun <T> handleResponseFailure(it1: ResultSealedClass.Failure<T>) {
        try {
            if (it1.status == Constants.NETWORKFAIL) {
                (activity as MainActivity).showToast(it1.throwable.toString())
            } else {
                it1.message?.let { it2 ->
                    (activity as MainActivity).showToast(
                        it2
                    )
                }
            }
        } catch (e: Exception) {


        }
    }


}

