package com.example.ludo

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ludo.databinding.CustomHostRowLayoutBinding
import com.example.ludo.databinding.PlayerUsernameDialogLayoutBinding
import com.example.ludo.databinding.ProfileCoinsRecyclerRowLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileCoinsAdapter(var activity: Activity) :
    ListAdapter<GameDetailsModelClass, RecyclerView.ViewHolder>(
        GameDetailsModelClass.diff
    ) {


    inner class ProfileCoinsViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var binding =
            ProfileCoinsRecyclerRowLayoutBinding.bind(view)


        init {
            (binding)?.playbutton?.setOnClickListener {
//                (activity as MainActivity).isHost = false
                if (!isGameStarted(currentList[adapterPosition]))
                    displayEnterUsernameDialogForPlayer()
                else {
                    openGameMatchingFragment(currentList[adapterPosition].id, false)
                }


            }


        }

        private fun displayEnterUsernameDialogForPlayer() {

            var alert = AlertDialog.Builder(binding.root.context).create()

            var view = LayoutInflater.from(binding.root.context)

                .inflate(R.layout.player_username_dialog_layout, null, false)

            alert.setView(view)
            var binding = PlayerUsernameDialogLayoutBinding.bind(view)

            binding.submitButton?.setOnClickListener {

                if (binding.usernameEdittext.text.isNotEmpty()) {

                    if (currentList[adapterPosition].game_status == "0") {
                        if ((activity as MainActivity).gameType == Constants.LUDOGAMETYPE)
                            send_player_username_to_server_ludo(binding, alert)
                        else
                            send_player_username_to_server_snake(binding, alert)
                    } else {
                        (activity as MainActivity).showToast("Other Player has joined the Game ")
                        alert?.dismiss()
                    }
                } else
                    (activity as MainActivity).showToast("Enter your user name")
            }

            binding.cancelButton.setOnClickListener {
                alert?.dismiss()
            }

            alert.window?.decorView?.rootView?.setBackgroundColor(Color.TRANSPARENT)

//        alert?.window?.setLayout(
//            resources.displayMetrics.widthPixels / 2,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )

            alert.show()


        }

    }

    private fun ProfileCoinsViewHolder.send_player_username_to_server_ludo(
        binding: PlayerUsernameDialogLayoutBinding,
        alert: AlertDialog
    ) {
        (activity as MainActivity).retrofit?.playerPlayClickApi(
            (activity as MainActivity).getUserId(),
            currentList[adapterPosition].id,
            binding.usernameEdittext.text.toString()
        )?.enqueue(object : Callback<UserRegistrationResponseModel> {
            override fun onFailure(
                call: Call<UserRegistrationResponseModel>,
                t: Throwable
            ) {
                (activity as MainActivity).apply {
                    this.binding.progressbar.visibility = View.GONE
                    showToast(t.toString())
                }
            }

            override fun onResponse(
                call: Call<UserRegistrationResponseModel>,
                response: Response<UserRegistrationResponseModel>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.status == "1") {
                        getAllGamesList()

                    } else
                        (activity as MainActivity).showToast(response.body()?.message!!)
                } else {
                    (activity as MainActivity).showToast(response.toString())
                }
                alert?.dismiss()
            }
        })
    }


    private fun ProfileCoinsViewHolder.send_player_username_to_server_snake(
        binding: PlayerUsernameDialogLayoutBinding,
        alert: AlertDialog
    ) {
        (activity as MainActivity).retrofit?.playerPlayClickApi_snake(
            (activity as MainActivity).getUserId(),
            currentList[adapterPosition].id,
            binding.usernameEdittext.text.toString()
        )?.enqueue(object : Callback<UserRegistrationResponseModel> {
            override fun onFailure(
                call: Call<UserRegistrationResponseModel>,
                t: Throwable
            ) {
                (activity as MainActivity).apply {
                    this.binding.progressbar.visibility = View.GONE
                    showToast(t.toString())
                }
            }

            override fun onResponse(
                call: Call<UserRegistrationResponseModel>,
                response: Response<UserRegistrationResponseModel>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.status == "1") {
                        getAllGamesList()

                    } else
                        (activity as MainActivity).showToast(response.body()?.message!!)
                } else {
                    (activity as MainActivity).showToast(response.toString())
                }
                alert?.dismiss()
            }
        })
    }


    inner class CustomProfileCoinsViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var binding =
            CustomHostRowLayoutBinding.bind(
                view
            )


        init {
            (binding)?.cancelbutton?.setOnClickListener {
                if (!isGameStarted(currentList[adapterPosition])) {
                    if ((activity as MainActivity).gameType == Constants.LUDOGAMETYPE)
                        cancelTheHostedLudoGame()
                    else
                        cancelTheHostedSnakeGame()

                } else {
                    openGameMatchingFragment(currentList[adapterPosition].id, true)

                }

            }


        }

    }

    fun openGameMatchingFragment(id: String, isHost: Boolean) {
        if((activity as MainActivity).gameType==Constants.LUDOGAMETYPE)
        checkIfPlayerSubittedResultLudo(id, isHost)
        else
            checkIfPlayerSubittedResultSnake(id, isHost)
    }

    private fun checkIfPlayerSubittedResultLudo(id: String, isHost: Boolean) {
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        (activity as MainActivity).retrofit?.checkIfPlayerSubmittedResult(
            id,
            (activity as MainActivity).getUserId()
        )?.enqueue(object : Callback<UserRegistrationResponseModel> {
            override fun onFailure(call: Call<UserRegistrationResponseModel>, t: Throwable) {
                (activity as MainActivity).apply {
                    showToast(t.toString())
                    binding.progressbar.visibility = View.GONE
                }
            }

            override fun onResponse(
                call: Call<UserRegistrationResponseModel>,
                response: Response<UserRegistrationResponseModel>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.status == "1") {
                        (activity as MainActivity).loadFragment(GameResultFragment().apply {
                            arguments = Bundle().apply {
                                putString(Constants.GAMEIDCONSTANT, id)
                                putBoolean(Constants.ISHOSTCONSTANT, isHost)
                            }
                        })


                    } else {
                        (activity as MainActivity).loadFragment(GameMatchingFragment().apply {
                            arguments = Bundle().apply {
                                putString(Constants.GAMEIDCONSTANT, id)
                                putBoolean(Constants.ISHOSTCONSTANT, isHost)
                            }
                        })
                    }
                } else {
                    (activity as MainActivity).showToast(response.toString())
                }

                (activity as MainActivity).binding.progressbar.visibility = View.GONE
            }
        })
    }




    private fun checkIfPlayerSubittedResultSnake(id: String, isHost: Boolean) {
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        (activity as MainActivity).retrofit?.checkIfPlayerSubmittedResult_snake(
            id,
            (activity as MainActivity).getUserId()
        )?.enqueue(object : Callback<UserRegistrationResponseModel> {
            override fun onFailure(call: Call<UserRegistrationResponseModel>, t: Throwable) {
                (activity as MainActivity).apply {
                    showToast(t.toString())
                    binding.progressbar.visibility = View.GONE
                }
            }

            override fun onResponse(
                call: Call<UserRegistrationResponseModel>,
                response: Response<UserRegistrationResponseModel>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.status == "1") {
                        (activity as MainActivity).loadFragment(GameResultFragment().apply {
                            arguments = Bundle().apply {
                                putString(Constants.GAMEIDCONSTANT, id)
                                putBoolean(Constants.ISHOSTCONSTANT, isHost)
                            }
                        })


                    } else {
                        (activity as MainActivity).loadFragment(GameMatchingFragment().apply {
                            arguments = Bundle().apply {
                                putString(Constants.GAMEIDCONSTANT, id)
                                putBoolean(Constants.ISHOSTCONSTANT, isHost)
                            }
                        })
                    }
                } else {
                    (activity as MainActivity).showToast(response.toString())
                }

                (activity as MainActivity).binding.progressbar.visibility = View.GONE
            }
        })
    }






    private fun CustomProfileCoinsViewHolder.cancelTheHostedLudoGame() {
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        (activity as MainActivity).retrofit?.cancelHostApi(
            (activity as MainActivity).getUserId(),
            currentList[adapterPosition].id
        )?.enqueue(
            object : Callback<UserRegistrationResponseModel> {
                override fun onFailure(
                    call: Call<UserRegistrationResponseModel>,
                    t: Throwable
                ) {
                    (activity as MainActivity).binding.progressbar.visibility =
                        View.GONE
                    (activity as MainActivity).showToast(t.toString())
                }

                override fun onResponse(
                    call: Call<UserRegistrationResponseModel>,
                    response: Response<UserRegistrationResponseModel>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {

                            getAllGamesList()
                        } else {
                            (activity as MainActivity).showToast(response.body()?.message!!)
                        }

                    } else {
                        (activity as MainActivity).showToast(response.toString())
                    }
                    (activity as MainActivity).showToast(response.toString())
                    (activity as MainActivity).binding.progressbar.visibility =
                        View.GONE
                }
            }
        )

    }


    private fun CustomProfileCoinsViewHolder.cancelTheHostedSnakeGame() {
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        (activity as MainActivity).retrofit?.cancelHostApi_snake(
            (activity as MainActivity).getUserId(),
            currentList[adapterPosition].id
        )?.enqueue(
            object : Callback<UserRegistrationResponseModel> {
                override fun onFailure(
                    call: Call<UserRegistrationResponseModel>,
                    t: Throwable
                ) {
                    (activity as MainActivity).binding.progressbar.visibility =
                        View.GONE
                    (activity as MainActivity).showToast(t.toString())
                }

                override fun onResponse(
                    call: Call<UserRegistrationResponseModel>,
                    response: Response<UserRegistrationResponseModel>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {

                            getAllGamesList()
                        } else {
                            (activity as MainActivity).showToast(response.body()?.message!!)
                        }

                    } else {
                        (activity as MainActivity).showToast(response.toString())
                    }
                    (activity as MainActivity).showToast(response.toString())
                    (activity as MainActivity).binding.progressbar.visibility =
                        View.GONE
                }
            }
        )

    }


    private fun getAllGamesList() {
        if ((activity as MainActivity).gameType == Constants.LUDOGAMETYPE)

            ((activity as MainActivity).supportFragmentManager.findFragmentById(
                R.id.container
            ) as CoinsFragment).getLudoGamesList()
        else


            ((activity as MainActivity).supportFragmentManager.findFragmentById(
                R.id.container
            ) as CoinsFragment).getSnakeGameList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0)
            ProfileCoinsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.profile_coins_recycler_row_layout, parent, false)
            )
        else
            CustomProfileCoinsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.custom_host_row_layout, parent, false)
            )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var model = currentList[position]

        Log.d("modelll", model.toString())


        if (getItemViewType(position) == 0) {
            (holder as ProfileCoinsViewHolder).binding.apply {
                if (isGameStarted(model)) {
                    playbutton.text = "View Game"
                    DrawableCompat.setTint(playbutton.background, Color.BLUE)

                } else {
                    playbutton.text = "Play"
                    DrawableCompat.setTint(
                        playbutton.background,
                        ResourcesCompat.getColor(activity.resources, R.color.green, null)
                    )
                }

                nametextview?.text = model.host_name
                availablecoinsTextView?.text = model.entry_fee + " Coins"
            }
        } else {
            (holder as CustomProfileCoinsViewHolder).binding.apply {

                if (isGameStarted(model)) {
                    cancelbutton.text = "View Game"
                    DrawableCompat.setTint(cancelbutton.background, Color.BLUE)

                } else {
                    cancelbutton.text = "Cancel"
                    DrawableCompat.setTint(
                        cancelbutton.background,
                        ResourcesCompat.getColor(activity.resources, R.color.red, null)
                    )
                }

                nametextview?.text = model.host_name
                availablecoinsTextView?.text = model.entry_fee + " Coins"
            }
        }

    }

    private fun isGameStarted(model: GameDetailsModelClass) =
        model.game_status != "0" && (model.host_id == (activity as MainActivity).getUserId() || model.player_id == (activity as MainActivity).getUserId())


    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].host_id == (activity as MainActivity).getUserId())
            1
        else
            0
    }
}






















