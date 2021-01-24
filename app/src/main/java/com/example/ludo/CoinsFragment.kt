package com.example.ludo

import android.app.Activity
import android.app.AlertDialog
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ludo.databinding.CoinsAlertDialogLayoutBinding
import com.example.ludo.databinding.FragmentCoinsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CoinsFragment : Fragment(R.layout.fragment_coins) {

    lateinit var binding: FragmentCoinsBinding
    var selectedCoins = 0
    var availableCoins = 0
    var myNmae = ""
    private lateinit var profilecoinsAdapter: ProfileCoinsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {


            binding = FragmentCoinsBinding.bind(view)
            if ((activity as MainActivity).gameType == Constants.SNAKEGAMETYPE)
                (activity as MainActivity).binding.root.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.snakebg, null)
            else
                (activity as MainActivity).binding.root.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.ludobg, null)

            myNmae = (activity as MainActivity).getUserData().name

            availableCoins = (activity as MainActivity).getUserData().coins.toInt()

            profilecoinsAdapter = ProfileCoinsAdapter(requireActivity())

            binding.mycoinstextview.text = availableCoins.toString()



            binding.playercoinsrecycler.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                this.adapter = profilecoinsAdapter

            }

            getGamesList()


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
                            selectcoinsbutton.text = it + " Coins"
                            selectedCoins = it.toInt()
                        }

                    })
                var coinsAdapter = CoinsDialogAdapter()
                binding.selectcoinbutton.setOnClickListener {

                    dialog.dismiss()
                }
                binding.coinsselectrecycler.apply {

                    coinsAdapter.setLiveDataObserver(livedataListenerForSelectedCoin)
                    layoutManager = LinearLayoutManager(context)
                    this.adapter = coinsAdapter


                }

                (activity as MainActivity).retrofit?.coinsapi()
                    ?.enqueue(object : Callback<CoinsResponseModelClass> {
                        override fun onFailure(call: Call<CoinsResponseModelClass>, t: Throwable) {
                            (activity as MainActivity).apply {
                                showToast(t.toString())
                                this.binding.progressbar.visibility = View.GONE
                            }
                        }

                        override fun onResponse(
                            call: Call<CoinsResponseModelClass>,
                            response: Response<CoinsResponseModelClass>
                        ) {
                            if (response.isSuccessful) {
                                if (response.body()?.status == "1") {
                                   coinsAdapter.submitList(response.body()?.data)
                                }
                                else
                                    (activity as MainActivity).showToast(response.body()?.message!!)
                            } else {
                                (activity as MainActivity).showToast(response.toString())
                            }
                        }
                    })

                dialog.window?.decorView?.rootView?.apply {
                    setBackgroundColor(Color.TRANSPARENT)


                    viewTreeObserver.addOnGlobalLayoutListener {


                    }
                }


                dialog.show()
            }



            super.onViewCreated(view, savedInstanceState)
        } catch (e: Exception) {
            Log.d("exceptionincoinsfr", e.toString())
        }
    }


    override fun onStart() {

        binding.placeAgamebuton.setOnClickListener {

            when {
                selectedCoins > availableCoins -> {
                    Toast.makeText(
                        context,
                        "Your coins are less than the selected coins . Please recharge and try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                selectedCoins <= 0 -> {


                    Toast.makeText(context, "Please select the amount to play", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    hostAGame()
                }
            }
//                else
//
//
//
//
//                    profilecoinsAdapter.currentList.let { it1 ->
//                        if (it1.find { it.isplaying } == null) {
//                            if (it1.isNotEmpty()) {
//                                var list = LinkedList<UserProfileCoinsModelClass>(it1)
//                                list.addFirst(
//                                    UserProfileCoinsModelClass(
//                                        (it1.size + 1).toString(), isplaying = true,
//                                        name = myNmae,
//                                        coins = selectedCoins.toString()
//                                    )
//                                )
//                                profilecoinsAdapter.submitList(list)
//
//                            } else {
//                                profilecoinsAdapter.submitList(listOf(UserProfileCoinsModelClass().apply {
//                                    isplaying = true
//                                    name = myNmae
//                                    coins = selectedCoins.toString()
//                                }))
//
//                            }
//
//
//                        } else {
//                            Toast.makeText(
//                                context,
//                                "Yo have already placed the game",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }


        }




        super.onStart()
    }

    private fun hostAGame() {
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        val id = activity?.getPreferences(Activity.MODE_PRIVATE)
            ?.getString(Constants.USERIDCONSTANT, "")!!
        val name = activity?.getPreferences(Activity.MODE_PRIVATE)
            ?.getString(Constants.USERNAMECONSTANT, "")!!


        (activity as MainActivity).retrofit?.hostAGAme(
            id, name, selectedCoins.toString()
        )?.enqueue(object : Callback<UserRegistrationResponseModel> {
            override fun onFailure(
                call: Call<UserRegistrationResponseModel>,
                t: Throwable
            ) {
                (activity as MainActivity).binding.progressbar.visibility = View.GONE
                (activity as MainActivity).showToast(t.toString())
            }

            override fun onResponse(
                call: Call<UserRegistrationResponseModel>,
                response: Response<UserRegistrationResponseModel>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.status == "1") {
                        (activity as MainActivity).showToast("Game Hosted Successfully . Please wait till other player joins")
                        getGamesList()
                    } else {
                        (activity as MainActivity).showToast(response.body()?.message!!)
                    }
                } else {
                    (activity as MainActivity).showToast(response.toString())

                }
                (activity as MainActivity).binding.progressbar.visibility = View.GONE
            }
        })
    }

    fun getGamesList() {
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        (activity as MainActivity).retrofit?.getGamesList()
            ?.enqueue(object : Callback<GameListResponseModel> {
                override fun onFailure(call: Call<GameListResponseModel>, t: Throwable) {
                    (activity as MainActivity).showToast(t.toString())
                    (activity as MainActivity).binding.progressbar.visibility = View.GONE
                }

                override fun onResponse(
                    call: Call<GameListResponseModel>,
                    response: Response<GameListResponseModel>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {
                            var hostModel: GameDetailsModelClass? = null

                            var list: MutableList<GameDetailsModelClass> =
                                (response.body()?.data?.let { list ->
                                    list.filter {
                                        if (it.host_id == (activity as MainActivity).getUserId()) {
                                            hostModel = it
                                            return@filter false
                                        }
                                        return@filter true
                                    }
                                } as MutableList<GameDetailsModelClass>?)!!
                            if (hostModel != null)
                                list.add(0, hostModel!!)
                            profilecoinsAdapter.submitList(list)


                        } else {
                            if ((response.body()?.data == null)) {
                                (activity as MainActivity).showToast("No Games Available")
                            }
                        }
                    } else {

                    }
                    (activity as MainActivity).binding.progressbar.visibility = View.GONE
                }
            })

    }
}

