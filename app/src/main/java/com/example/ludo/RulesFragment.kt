package com.example.ludo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ludo.databinding.FragmentRulesBinding


class RulesFragment : Fragment(R.layout.fragment_rules) {
    lateinit var binding:FragmentRulesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentRulesBinding.bind(view)

        super.onViewCreated(view, savedInstanceState)
    }

}