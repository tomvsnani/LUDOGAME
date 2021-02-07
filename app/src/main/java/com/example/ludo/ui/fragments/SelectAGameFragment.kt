package com.example.ludo.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ludo.utils.Constants
import com.example.ludo.ui.activities.MainActivity
import com.example.ludo.R
import com.example.ludo.ResultSealedClass
import com.example.ludo.adapters.WelcomeRecyclerAdapter
import com.example.ludo.data.GameMatchedPlayerDetailsModelClass
import com.example.ludo.databinding.FragmentSelectAGameBinding
import com.example.ludo.ui.activities.SharedViewModel
import com.example.ludo.ui.fragments.coinsFragment.CoinsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectAGameFragment : Fragment(R.layout.fragment_select_a_game) {
    lateinit var binding: FragmentSelectAGameBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentSelectAGameBinding.bind(view)


        val viewmodelActivity by activityViewModels<SharedViewModel>()

        getUserCoins(viewmodelActivity)

        setUpGifImage()


        super.onViewCreated(view, savedInstanceState)
    }




    override fun onStart() {

        setUpToolbarBottomNav()

        setUpClickListeners()

        super.onStart()
    }






    private fun setUpGifImage() {
        Glide.with(requireActivity()).asGif().load(R.drawable.cutesmile)
            .into(binding.imageView4)
    }


    private fun getUserCoins(
        viewmodelActivity: SharedViewModel
    ) {
        (activity as MainActivity).makeProgressVisible()
        viewmodelActivity.getCoins((activity as MainActivity).getUserId())
            .observe(viewLifecycleOwner, Observer {

                when (it) {
                    is ResultSealedClass.Success -> {
                        if (it.data?.data != null)
                            binding.coinsTextView.text = "${it.data.data[0].wallet}  coins"
                        else
                            binding.coinsTextView.text = "0 Coins"
                        (activity as MainActivity).makeProgresssHide()
                    }

                    is ResultSealedClass.Failure -> {
                        handleResponseFailure(it)
                        (activity as MainActivity).makeProgresssHide()
                    }
                }
            })
    }



    private fun setUpToolbarBottomNav() {
        (activity as MainActivity).setToolBarText(getString(R.string.app_name))

        (activity as MainActivity).binding.apply {
            rechargelineartoolbar.visibility = View.VISIBLE
            saleslineartoolbar.visibility = View.VISIBLE
            bottomNav.visibility = View.VISIBLE
        }
    }

    private fun setUpClickListeners() {
        binding.ludogamecardwrapper.setOnClickListener {
            (activity as MainActivity).gameType = Constants.LUDOGAMETYPE
            (activity as MainActivity).loadFragment(CoinsFragment().apply {
                arguments = Bundle().apply {
                    putString("type", Constants.LUDOGAMETYPE)
                }
            }, true, "coins")

        }


        binding.snakegamecardwrapper.setOnClickListener {
            (activity as MainActivity).gameType = Constants.SNAKEGAMETYPE
            (activity as MainActivity).loadFragment(CoinsFragment().apply {
                arguments = Bundle().apply {
                    putString("type", Constants.SNAKEGAMETYPE)
                }
            }, true, "coins")
        }
    }


    override fun onStop() {
        (activity as MainActivity).binding.apply {
            rechargelineartoolbar.visibility = View.GONE
            saleslineartoolbar.visibility = View.GONE
            bottomNav.visibility = View.GONE
        }
        super.onStop()
    }






    private fun handleResponseFailure(it1: ResultSealedClass.Failure<GameMatchedPlayerDetailsModelClass>) {
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