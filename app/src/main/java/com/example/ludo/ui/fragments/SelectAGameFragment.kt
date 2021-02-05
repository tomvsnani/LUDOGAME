package com.example.ludo.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ludo.utils.Constants
import com.example.ludo.ui.activities.MainActivity
import com.example.ludo.R
import com.example.ludo.adapters.WelcomeRecyclerAdapter
import com.example.ludo.databinding.FragmentSelectAGameBinding

class SelectAGameFragment : Fragment(R.layout.fragment_select_a_game) {
    lateinit var binding: FragmentSelectAGameBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSelectAGameBinding.bind(view)
        (activity as MainActivity).binding.apply {
            rechargelineartoolbar.visibility = View.VISIBLE
            saleslineartoolbar.visibility = View.VISIBLE
            bottomNav.visibility = View.VISIBLE


        }
        (activity as MainActivity).setToolBarText(getString(R.string.app_name))

        (activity as MainActivity).profileDetailsLiveData.observe(viewLifecycleOwner, Observer {
            if(it!=null)
            binding.coinsTextView.text=it+ " coins"
            else
                binding.coinsTextView.text = "0 Coins"
        })

        Glide.with(requireActivity()).asGif().load(R.drawable.cutesmile)
            .into(binding.imageView4)

//        (activity as MainActivity).setUpFragmentsToolbarProperties(
//            resources.getString(R.string.app_name),
//            false,ResourcesCompat.getDrawable(resources,R.drawable.)
//        )

        binding.welcomerecycler.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = WelcomeRecyclerAdapter()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {

        (activity as MainActivity).binding.apply {
            rechargelineartoolbar.visibility = View.VISIBLE
            saleslineartoolbar.visibility = View.VISIBLE
            bottomNav.visibility = View.VISIBLE
        }


        super.onStop()

        binding.ludogamecardwrapper.setOnClickListener {
            (activity as MainActivity).gameType = Constants.LUDOGAMETYPE
            (activity as MainActivity).loadFragment(CoinsFragment().apply {  arguments=Bundle().apply {
                putString("type", Constants.LUDOGAMETYPE)
            }}, true, "coins")

        }


        binding.snakegamecardwrapper.setOnClickListener {
            (activity as MainActivity).gameType = Constants.SNAKEGAMETYPE
            (activity as MainActivity).loadFragment(CoinsFragment().apply {  arguments=Bundle().apply {
                putString("type", Constants.SNAKEGAMETYPE)
            }},true,"coins")
        }
        super.onStart()
    }


    override fun onStop() {
        (activity as MainActivity).binding.apply {
            rechargelineartoolbar.visibility = View.GONE
            saleslineartoolbar.visibility = View.GONE
            bottomNav.visibility = View.GONE
        }
        super.onStop()
    }


}