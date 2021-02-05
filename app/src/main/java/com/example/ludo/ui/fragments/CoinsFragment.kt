package com.example.ludo.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ludo.*
import com.example.ludo.adapters.CoinsDialogAdapter
import com.example.ludo.adapters.ProfileCoinsAdapter
import com.example.ludo.data.CoinsResponseModelClass
import com.example.ludo.data.GameDetailsModelClass
import com.example.ludo.data.GameListResponseModel
import com.example.ludo.data.UserRegistrationResponseModel
import com.example.ludo.databinding.CoinsAlertDialogLayoutBinding
import com.example.ludo.databinding.FragmentCoinsBinding
import com.example.ludo.ui.activities.MainActivity
import com.example.ludo.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CoinsFragment : Fragment(R.layout.fragment_coins) {

    lateinit var binding: FragmentCoinsBinding
    var selectedCoins = 0
    var availableCoins = 0
    var gameType = ""
    private lateinit var profilecoinsAdapter: ProfileCoinsAdapter
    lateinit var dialog: AlertDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {


            binding = FragmentCoinsBinding.bind(view)

            gameType = arguments?.getString("type", "")!!
            (activity as MainActivity).profileDetailsLiveData.observe(viewLifecycleOwner, Observer {
                if(it!=null) {
                    binding.mycoinstextview.text = it
                    availableCoins = it.toInt()
                }
                else{
                    binding.mycoinstextview.text = "0 Coins"
                    availableCoins=0
                }
            })

            if ((activity as MainActivity).gameType == Constants.SNAKEGAMETYPE) {
                (activity as MainActivity).binding.root.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.snakebg, null)
                (activity as MainActivity).setToolBarText("Snake Game")
            }
                else
            {
                (activity as MainActivity).binding.root.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.ludobg, null)
                (activity as MainActivity).setToolBarText("Ludo Game")
            }


            availableCoins = (activity as MainActivity).getUserData().coins.toInt()

            profilecoinsAdapter = ProfileCoinsAdapter(requireActivity())





            binding.playercoinsrecycler.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                this.adapter = profilecoinsAdapter

            }
            if (gameType == Constants.LUDOGAMETYPE)
                getLudoGamesList()
            else
                getSnakeGameList()


            binding.selectcoinsbutton.setOnClickListener {
                createAlertDialogForCoins()
            }



            super.onViewCreated(view, savedInstanceState)
        } catch (e: Exception) {
            Log.d("exceptionincoinsfr", e.toString())
        }
    }

    private fun createAlertDialogForCoins() {

        MutableLiveData<String>().also {

        }
        var dialogBuilder = AlertDialog.Builder(context)
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

            if (this::dialog.isInitialized)
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
                            if (response.body()?.data != null && response.body()?.data?.isNotEmpty()!!)
                                coinsAdapter.submitList(response.body()?.data)
                        } else
                            (activity as MainActivity).showToast(response.body()?.message!!)
                    } else {
                        (activity as MainActivity).showToast(response.toString())
                    }
                }
            })


        dialog = dialogBuilder.create()
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)

//        if (this::dialog.isInitialized) {
//            dialog.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener {
//                dialog.window?.decorView?.rootView?.apply {
//                    setBackgroundColor(Color.TRANSPARENT)
//
//
//                    viewTreeObserver.addOnGlobalLayoutListener {
//
//
//                    }
//                }
//            }
//        }

        dialog.show()

    }


    override fun onStart() {

        binding.placeAgamebuton.setOnClickListener {

            when {
                selectedCoins > availableCoins -> {
                    Toast.makeText(
                        context,
                        "Your coins are less . Please recharge and try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                selectedCoins <= 0 || availableCoins<=0 -> {


                    Toast.makeText(
                        context,
                        "Your coins are less . Please recharge and try again",
                        Toast.LENGTH_SHORT
                    ).show()
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

        if ((activity as MainActivity).gameType == Constants.LUDOGAMETYPE)
            hostLudoGame(id, name)
        else {
            hostSnakeGame(id, name)
        }
    }


    fun getSnakeGameList() {

        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        (activity as MainActivity).retrofit?.getGamesList_snake()
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
                            var gameHostedOrProgressList = mutableListOf<GameDetailsModelClass>()
                            var list: MutableList<GameDetailsModelClass>? =
                                (response.body()?.data?.let { list ->
                                    list.filter {       // removing modelifit is host or player to add it
                                        // below and display it on top of list
                                        if ( it.game_status=="0") {
                                            gameHostedOrProgressList.add(it)

                                            return@filter false
                                        }
                                        return@filter true
                                    }
                                } as MutableList<GameDetailsModelClass>?)!!
                            for (i in gameHostedOrProgressList) {
                                list?.add(0, i)
                            }
                            profilecoinsAdapter.submitList(list?.filter {
                                it.game_status == "0" || isUserHostOrPlayer(
                                    it
                                )   //filtering to display game only to host or user if
                            })


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


    fun getLudoGamesList() {
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
                            var gameHostedOrProgressList = mutableListOf<GameDetailsModelClass>()
                            var list: MutableList<GameDetailsModelClass>? =
                                (response.body()?.data?.let { list ->
                                    list.filter {       // removing modelifit is host or player to add it
                                        // below and display it on top of list
                                        if (it.game_status=="0") {
                                            gameHostedOrProgressList.add(it)

                                            return@filter false
                                        }
                                        return@filter true
                                    }
                                } as MutableList<GameDetailsModelClass>?)!!
                            for (i in gameHostedOrProgressList) {

                                list?.add(0, i)

                            }
                            profilecoinsAdapter.submitList(list?.filter {
                                it.game_status == "0" || isUserHostOrPlayer(
                                    it
                                )   //filtering to display game only to host or user if
                            })


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

    private fun isUserHostOrPlayer(it: GameDetailsModelClass) =
        (it.host_id == (activity as MainActivity).getUserId() ||
                it.player_id == (activity as MainActivity).getUserId())


    private fun hostLudoGame(id: String, name: String) {
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

                        getLudoGamesList()

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


    private fun hostSnakeGame(id: String, name: String) {
        (activity as MainActivity).retrofit?.hostAGAme_snake(
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
                        getSnakeGameList()
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


}

