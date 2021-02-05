package com.example.ludo.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ludo.ui.activities.MainActivity
import com.example.ludo.R
import com.example.ludo.adapters.StoriesAdapter
import com.example.ludo.data.StoriesModelClass
import com.example.ludo.databinding.FragmentStoriesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StoriesFragment : Fragment(R.layout.fragment_stories) {
lateinit var binding:FragmentStoriesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding= FragmentStoriesBinding.bind(view)
        var adapterr= StoriesAdapter()
        binding.recycler.apply {
            layoutManager=LinearLayoutManager(context)
            adapter=adapterr
        }
        (activity as MainActivity).   binding.progressbar.visibility=View.VISIBLE
        (activity as MainActivity).retrofit?.story()?.enqueue(object : Callback<StoriesModelClass> {
            override fun onFailure(call: Call<StoriesModelClass>, t: Throwable) {
                (activity as MainActivity).apply {
                    binding.progressbar.visibility=View.GONE
                    showToast(t.toString())
                }
            }

            override fun onResponse(
                call: Call<StoriesModelClass>,
                response: Response<StoriesModelClass>
            ) {
                if(response.isSuccessful){
                    if(response.body()?.status=="1"){
                        adapterr.submitList(response.body()?.data)
                    }
                    else{
                        (activity as MainActivity).showToast(response.body()?.message!!)
                    }
                }
                else
                    (activity as MainActivity).showToast(response.toString())
                (activity as MainActivity).   binding.progressbar.visibility=View.GONE
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }
}