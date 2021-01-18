package com.example.ludo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ludo.databinding.FragmentGameMatchingBinding

class GameMatchingFragment : Fragment(R.layout.fragment_game_matching) {
    lateinit var binding:FragmentGameMatchingBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentGameMatchingBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
    }


}