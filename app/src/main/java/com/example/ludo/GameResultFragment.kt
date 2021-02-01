package com.example.ludo

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.ludo.databinding.FragmentGameResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GameResultFragment : Fragment(R.layout.fragment_game_result) {
    lateinit var binding: FragmentGameResultBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentGameResultBinding.bind(view)
        var model = (activity as MainActivity).gameResultModel

//        binding.roomcodeButton.text = arguments?.getString(Constants.GAMECODECONSTANT, "no code")


        val isHost = (activity as MainActivity).isHost

        val userId = (activity as MainActivity).getUserId()

        val gameId = arguments?.getString(Constants.GAMEIDCONSTANT)


        if ((activity as MainActivity).gameType == Constants.LUDOGAMETYPE)
            getGameResultLudo(gameId, model, isHost)
        else
            getSnakeResultLudo(gameId, model, isHost)


        (activity as MainActivity).getPlayersDetails(gameId)

        (activity as MainActivity).gameDetailsLiveData?.observe(viewLifecycleOwner, Observer {
            for (i in it) {


                if (i.id == (activity as MainActivity).getUserId()) {
                    binding.hostnametextview.text = i.username
                    Glide.with(requireActivity()).load( i.profile_pic)
                        .transform(CircleCrop())
                        .placeholder(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.coinsimage,
                                null
                            )
                        ).into(binding.player1image)


                } else {
                    binding.playernametextview.text = i.username
                    Glide.with(requireActivity()).load( i.profile_pic)
                        .transform(CircleCrop())
                        .placeholder(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.coinsimage,
                                null
                            )
                        ).into(binding.player2image)

                }
            }
        })


        activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
                activity?.supportFragmentManager?.popBackStackImmediate(
                    "coins",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
        })







        super.onViewCreated(view, savedInstanceState)
    }

    private fun getGameResultLudo(
        gameId: String?,
        model: GameResultModelClass?,
        isHost: Boolean
    ) {
        var model1 = model
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        (activity as MainActivity).retrofit?.getGameResult(gameId!!)
            ?.enqueue(object : Callback<GameResultModelClassResponse> {

                override fun onFailure(call: Call<GameResultModelClassResponse>, t: Throwable) {
                    (activity as MainActivity).apply {
                        showToast(t.toString())
                        binding.progressbar.visibility = View.GONE
                    }

                }

                override fun onResponse(
                    call: Call<GameResultModelClassResponse>,
                    response: Response<GameResultModelClassResponse>
                ) {


                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {
                            (activity as MainActivity).isGamesultSubmitted = true
                            var body = response.body()!!
                            if (response.body()?.data != null && response.body()?.data?.isNotEmpty()!!) {
                                model1 = body.data?.get(0)
                                binding.roomcodeButton.text = body.game_code
                                var text = ""
                                var image: Drawable? = null
                                checkResult(model1, isHost, text, image)
                            }
                            else{
                                Toast.makeText(context,"Content Empty",Toast.LENGTH_SHORT).show()
                            }

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


    private fun getSnakeResultLudo(
        gameId: String?,
        model: GameResultModelClass?,
        isHost: Boolean
    ) {
        var model1 = model
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        (activity as MainActivity).retrofit?.getGameResult_snake(gameId!!)
            ?.enqueue(object : Callback<GameResultModelClassResponse> {

                override fun onFailure(call: Call<GameResultModelClassResponse>, t: Throwable) {
                    (activity as MainActivity).apply {
                        showToast(t.toString())
                        binding.progressbar.visibility = View.GONE
                    }

                }

                override fun onResponse(
                    call: Call<GameResultModelClassResponse>,
                    response: Response<GameResultModelClassResponse>
                ) {


                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {
                            (activity as MainActivity).isGamesultSubmitted = true
                            var body = response.body()!!
                            if (body.data!=null && body.data!!.isNotEmpty()) {
                                model1 = body.data!![0]
                                binding.roomcodeButton.text = body.game_code
                                var text = ""
                                var image: Drawable? = null
                                checkResult(model1, isHost, text, image)
                            }

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


    private fun checkResult(
        model: GameResultModelClass?,
        isHost: Boolean,
        text: String,
        image: Drawable?,
        userId: String = (activity as MainActivity).getUserId()
    ) {
        var text1 = text
        var image1 = image
        when (model?.final_result) {

            Constants.GAME_CANCELLED_RESULT_CONSTANT -> {
                var cancelledPlayerId =
                    if (model?.host_result == "3") model.host_id else model.player_id

                if (cancelledPlayerId == userId) {

                    text1 =
                        "Game is cancelled"


                } else

                    text1 = resources.getString(
                        R.string.results_player_cancelled_admin_action
                    )
                image1 =
                    ResourcesCompat.getDrawable(resources, R.drawable.game_cancelled_emoji, null)

            }
            Constants.BOTH_SUBMITTED_WIN_CONSTANT -> {
                text1 =
                    resources.getString(R.string.results_other_player_submitted_wait_until_verify)
                image1 =
                    ResourcesCompat.getDrawable(resources, R.drawable.game_cancelled_emoji, null)

            }
            Constants.WINNER_DECLARED_CONSTANT -> {
                var winnerId = if (model?.host_result == "1") model.host_id else model.player_id
                if (winnerId == userId) {
                    text1 =
                        resources.getString(R.string.gamewon)
                    image1 =
                        ResourcesCompat.getDrawable(resources, R.drawable.gamewonemoji, null)
                } else {
                    text1 =
                        resources.getString(
                            R.string.lostgame
                        )
                    image1 =

                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.game_cancelled_emoji,
                            null
                        )
                }


            }
            Constants.ONE_SUBMITTED_RESULT_OTHER_RESULT_PENDING_CONSTANT -> {


                text1 =
                    resources.getString(R.string.results_submitted_waiting)


                image1 =

                    ResourcesCompat.getDrawable(resources, R.drawable.waitingemoji, null)
            }
            Constants.CANCELLATION_VERIFIED_CONSTANT -> {
                var cancelledPlayerId =
                    if (model?.host_result == "3") model.host_id else model.player_id
                if (cancelledPlayerId == userId) {
                    text1 =
                        resources.getString(R.string.game_cancelled_refunded)
                    image1 =

                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.game_cancelled_emoji,
                            null
                        )
                } else {
                    text1 =
                        "Game is Cancelled"
                    image1 =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.game_cancelled_emoji,
                            null
                        )
                }
            }


        }
        binding.showresultmessageTextView.text = text1

        binding.resultImageView.setImageDrawable(image1)
    }

}