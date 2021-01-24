package com.example.ludo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        binding.ludogamecardwrapper.setOnClickListener {
            (activity as MainActivity).gameType = Constants.LUDOGAMETYPE
            (activity as MainActivity).loadFragment(CoinsFragment())

        }


        binding.snakegamecardwrapper.setOnClickListener {
            (activity as MainActivity).gameType = Constants.SNAKEGAMETYPE
            (activity as MainActivity).loadFragment(CoinsFragment())
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