package com.example.ludo

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.ludo.databinding.CustomHostRowLayoutBinding
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
            (binding as ProfileCoinsRecyclerRowLayoutBinding)?.playbutton?.setOnClickListener {
                (activity as MainActivity).isHost = false
                (activity as MainActivity).loadFragment(GameMatchingFragment())

            }


        }

    }


    inner class CustomProfileCoinsViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var binding =
            CustomHostRowLayoutBinding.bind(
                view
            )


        init {
            (binding as CustomHostRowLayoutBinding)?.cancelbutton?.setOnClickListener {
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
                            (activity as MainActivity).binding.progressbar.visibility = View.GONE
                            (activity as MainActivity).showToast(t.toString())
                        }

                        override fun onResponse(
                            call: Call<UserRegistrationResponseModel>,
                            response: Response<UserRegistrationResponseModel>
                        ) {
                            if (response.isSuccessful) {
                                if (response.body()?.status == "1") {

                                    ((activity as MainActivity).supportFragmentManager.findFragmentById(
                                        R.id.container
                                    ) as CoinsFragment).getGamesList()
                                } else {
                                    (activity as MainActivity).showToast(response.body()?.message!!)
                                }

                            } else {
                                (activity as MainActivity).showToast(response.toString())
                            }
                            (activity as MainActivity).showToast(response.toString())
                            (activity as MainActivity).binding.progressbar.visibility = View.GONE
                        }
                    }
                )
                (activity as MainActivity).isHost = true
            }


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


        if (getItemViewType(position) == 0) {
            (holder as ProfileCoinsViewHolder).binding.apply {
                nametextview?.text = model.host_name
                availablecoinsTextView?.text = model.entry_fee + " Coins"
            }
        } else {
            (holder as CustomProfileCoinsViewHolder).binding.apply {
                nametextview?.text = model.host_name
                availablecoinsTextView?.text = model.entry_fee + " Coins"
            }
        }

    }


    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].host_id == (activity as MainActivity).getUserId())
            1
        else
            0
    }
}