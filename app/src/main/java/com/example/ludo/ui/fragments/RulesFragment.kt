package com.example.ludo.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.ludo.R
import com.example.ludo.databinding.FragmentRulesBinding


class RulesFragment : Fragment(R.layout.fragment_rules) {
    lateinit var binding:FragmentRulesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentRulesBinding.bind(view)

        super.onViewCreated(view, savedInstanceState)
    }

}