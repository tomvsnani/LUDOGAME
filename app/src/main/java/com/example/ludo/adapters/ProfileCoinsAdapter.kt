package com.example.ludo.adapters

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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ludo.utils.Constants
import com.example.ludo.R
import com.example.ludo.ResultSealedClass
import com.example.ludo.data.GameDetailsModelClass
import com.example.ludo.data.UserRegistrationResponseModel
import com.example.ludo.databinding.CustomHostRowLayoutBinding
import com.example.ludo.databinding.PlayerUsernameDialogLayoutBinding
import com.example.ludo.databinding.ProfileCoinsRecyclerRowLayoutBinding
import com.example.ludo.ui.activities.MainActivity
import com.example.ludo.ui.fragments.coinsFragment.CoinsFragment
import com.example.ludo.ui.fragments.GameMatchingFragment
import com.example.ludo.ui.fragments.GameResultFragment
import com.example.ludo.ui.fragments.coinsFragment.CoinsViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileCoinsAdapter(var activity: Activity) :
    ListAdapter<GameDetailsModelClass, RecyclerView.ViewHolder>(
        GameDetailsModelClass.diff
    ) {

    private lateinit var viewModel: CoinsViewModel

    private lateinit var coinsFragment: CoinsFragment

    public fun setViewModel(viewModel: CoinsViewModel, coinsFragment: CoinsFragment) {
        this.coinsFragment = coinsFragment
        this@ProfileCoinsAdapter.viewModel = viewModel
    }

    inner class ProfileCoinsViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var binding =
            ProfileCoinsRecyclerRowLayoutBinding.bind(view)


        init {
            (binding)?.playbutton?.setOnClickListener {
//                (activity as MainActivity).isHost = false
                if (!isGameStarted(currentList[adapterPosition]))
                    displayEnterUsernameDialogForPlayer()
                else {
                    openGameMatchingFragment(
                        currentList[adapterPosition].id,
                        (activity as MainActivity).getUserId() == currentList[adapterPosition].host_id
                    )
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
                    (activity as MainActivity).makeProgressVisible()

                    viewModel.sendUsernameToServer(
                        (activity as MainActivity).gameType,
                        (activity as MainActivity).getUserId(),
                        currentList[adapterPosition].id,
                        binding.usernameEdittext.text.toString()
                    ).observe(coinsFragment.viewLifecycleOwner, Observer {
                        when (it) {
                            is ResultSealedClass.Success -> {
                                (activity as MainActivity).makeProgresssHide()
                                getAllGamesList()
                                alert?.dismiss()
                            }
                            is ResultSealedClass.Failure -> {
                                (activity as MainActivity).makeProgresssHide()
                                coinsFragment.handleResponseFailure(it)
                                alert?.dismiss()
                            }
                        }
                    })


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


    inner class CustomProfileCoinsViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var binding =
            CustomHostRowLayoutBinding.bind(
                view
            )


        init {
            (binding)?.cancelbutton?.setOnClickListener {
                if (!isGameStarted(currentList[adapterPosition])) {
                    (activity as MainActivity).makeProgressVisible()
                 var a=   viewModel.cancelGame(
                        (activity as MainActivity).gameType, (activity as MainActivity).getUserId(),
                        currentList[adapterPosition].id
                    )
                    a.observe(coinsFragment.viewLifecycleOwner,
                        object :Observer<ResultSealedClass<UserRegistrationResponseModel>> {
                            override fun onChanged(t: ResultSealedClass<UserRegistrationResponseModel>?) {
                                a.removeObserver(this)
                                when (t) {
                                    is ResultSealedClass.Failure -> {
                                        (activity as MainActivity).makeProgresssHide()
                                        coinsFragment.handleResponseFailure(t)
                                    }
                                    is ResultSealedClass.Success -> {
                                        (activity as MainActivity).makeProgresssHide()

                                        (activity as MainActivity).displayGeneralAlertDialog(
                                            activity.getString(R.string.game_cancelled_dialog),
                                            R.drawable.oops
                                        )

                                        getAllGamesList()
                                    }
                                }


                            }
                        })


                } else {
                    openGameMatchingFragment(
                        currentList[adapterPosition].id,
                        (activity as MainActivity).getUserId() == currentList[adapterPosition].host_id
                    )

                }

            }


        }

    }

    fun openGameMatchingFragment(id: String, isHost: Boolean) {
        (activity as MainActivity).makeProgressVisible()
        viewModel.checkIfUserSubmittedResult(
            (activity as MainActivity).gameType,
            id,
            (activity as MainActivity).getUserId()
        ).observe(coinsFragment.viewLifecycleOwner, Observer {
            when (it) {
                is ResultSealedClass.Failure -> {
                    (activity as MainActivity).makeProgresssHide()
                    coinsFragment.handleResponseFailure(it)

                }
                is ResultSealedClass.Success -> {
                    (activity as MainActivity).makeProgresssHide()
                    if (it.data?.status == "1") {
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

                }
            }
        })


    }








    private fun getAllGamesList() {

        (activity as MainActivity).makeProgressVisible()
        try {
            ((activity as MainActivity).supportFragmentManager.findFragmentById(
                R.id.container
            ) as CoinsFragment).getGamesList()
        } catch (e: Exception) {
        }


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
                    nametextview?.text = "${model.host_name} vs ${model.player_name}"

                } else {
                    playbutton.text = "Play"
                    DrawableCompat.setTint(
                        playbutton.background,
                        ResourcesCompat.getColor(activity.resources, R.color.green, null)
                    )
                    nametextview?.text = model.host_name
                }


                availablecoinsTextView?.text = model.entry_fee + " Coins"
            }
        } else {
            (holder as CustomProfileCoinsViewHolder).binding.apply {

                if (isGameStarted(model)) {
                    cancelbutton.text = "View Game"
                    DrawableCompat.setTint(cancelbutton.background, Color.BLUE)
                    nametextview?.text = "${model.host_name} vs ${model.player_name}"

                } else {
                    cancelbutton.text = "Cancel"
                    DrawableCompat.setTint(
                        cancelbutton.background,
                        ResourcesCompat.getColor(activity.resources, R.color.red, null)
                    )
                    nametextview?.text = model.host_name
                }


                availablecoinsTextView?.text = model.entry_fee + " Coins"
            }
        }

    }

    private fun isGameStarted(model: GameDetailsModelClass) =
        model.game_status != "0" && (model.host_id == (activity as MainActivity).getUserId() || model.player_id == (activity as MainActivity).getUserId())


    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].host_id == (activity as MainActivity).getUserId() && currentList[position].game_status == "0")
            1
        else
            0
    }
}






















