package com.example.ludo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ludo.databinding.FragmentGameResultBinding


class GameResultFragment : Fragment(R.layout.fragment_game_result) {
lateinit var binding:FragmentGameResultBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentGameResultBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

    }

}