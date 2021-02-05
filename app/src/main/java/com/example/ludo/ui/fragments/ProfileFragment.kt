package com.example.ludo.ui.fragments

import com.example.ludo.data.GameMatchedPlayerDetailsModelClass
import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.drawToBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.ludo.utils.Constants
import com.example.ludo.ui.activities.MainActivity
import com.example.ludo.R
import com.example.ludo.data.UserModelClass
import com.example.ludo.databinding.FragmentProfileBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    lateinit var binding: FragmentProfileBinding
    var isInEditMode = false
    lateinit var imagePickerIntent: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerIntent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            Glide.with(requireContext()).load(it).transform(CircleCrop())
                .into(binding.userprofileimageview)
            val preferences = activity?.getPreferences(Activity.MODE_PRIVATE)



            binding.updateProfileButton.setOnClickListener {

                (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
                val stream = ByteArrayOutputStream()
                binding.userprofileimageview.drawToBitmap()
                    .compress(Bitmap.CompressFormat.PNG, 100, stream)


                (activity as MainActivity).retrofit?.updateProfile(
                    UserModelClass(
                        id = preferences?.getString(Constants.USERIDCONSTANT, "")!!,
                        username = preferences?.getString(Constants.USERNAMECONSTANT, "")!!, email =
                        preferences?.getString(Constants.EMAILCONSTANT, "")!!
                    ),
                    MultipartBody.Part.createFormData(
                        "image",
                        "profileimage.png"
                        ,
                        RequestBody.create(
                            MediaType.parse("image/*"),
                            stream.toByteArray()
                        )
                    )
                )?.enqueue(object : Callback<GameMatchedPlayerDetailsModelClass> {
                    override fun onFailure(
                        call: Call<GameMatchedPlayerDetailsModelClass>,
                        t: Throwable
                    ) {
                        (activity as MainActivity).showToast(t.toString())
                        (activity as MainActivity).binding.progressbar.visibility = View.GONE
                    }

                    override fun onResponse(
                        call: Call<GameMatchedPlayerDetailsModelClass>,
                        response: Response<GameMatchedPlayerDetailsModelClass>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()?.status == "1") {


                                binding.apply {
                                    nameEditText.text = response.body()?.data!![0].username
                                    mobileNumberEditText.text =
                                        response.body()?.data!![0].user_phone

                                    showProfileUpdatedView()
                                }
                            }
                        } else {
                            (activity as MainActivity).showToast(response.toString())
                        }
                        (activity as MainActivity).binding.progressbar.visibility = View.GONE
                    }
                })
            }

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentProfileBinding.bind(view)

        binding.changeprofilepiccardview.setOnClickListener {
            imagePickerIntent.launch("image/*")
        }

        if (isInEditMode)
            binding.changeprofilepiccardview.visibility = View.VISIBLE
        else {
            binding.changeprofilepiccardview.visibility = View.GONE

            (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
            (activity as MainActivity).retrofit?.getProfileData((activity as MainActivity).getUserId())
                ?.enqueue(
                    object : Callback<GameMatchedPlayerDetailsModelClass> {
                        override fun onFailure(
                            call: Call<GameMatchedPlayerDetailsModelClass>,
                            t: Throwable
                        ) {
                            (activity as MainActivity).showToast(t.toString())
                            (activity as MainActivity).binding.progressbar.visibility = View.GONE
                        }

                        override fun onResponse(
                            call: Call<GameMatchedPlayerDetailsModelClass>,
                            response: Response<GameMatchedPlayerDetailsModelClass>
                        ) {
                            if (response.isSuccessful) {
                                if (response?.body()?.status == "1") {
                                    binding.apply {
                                        if (response.body()?.data != null && response.body()?.data?.isNotEmpty()!!) {

                                            nameEditText.text = response.body()?.data!![0].username
                                            mobileNumberEditText.text =
                                                response.body()?.data!![0].user_phone
                                            if(context!=null)
                                            Glide.with(context!!)
                                                .load(response.body()?.data!![0].profile_pic)
                                                .transform(CircleCrop())
                                                .into(userprofileimageview)



                                        }
                                    }
                                } else {
                                    (activity as MainActivity).showToast(response.body()?.message!!)
                                }
                            } else {
                                (activity as MainActivity).showToast(response.toString())
                            }
                            (activity as MainActivity).binding.progressbar.visibility = View.GONE
                        }
                    }
                )


//            binding.apply {
//
//                DrawableCompat.setTint(genderEditText.background, Color.TRANSPARENT)
//                DrawableCompat.setTint(mobileNumberEditText.background, Color.TRANSPARENT)
//                DrawableCompat.setTint(nameEditText.background, Color.TRANSPARENT)
//
//                genderEditText.isClickable = false
//                mobileNumberEditText.isClickable = false
//                nameEditText.isClickable = false
//            }


        }
        binding.updateProfileButton.setOnClickListener {

            if (!isInEditMode) {
                isInEditMode = true

                binding.apply {

//                    DrawableCompat.setTint(genderEditText.background, Color.WHITE)
//                    DrawableCompat.setTint(mobileNumberEditText.background, Color.WHITE)
//                    DrawableCompat.setTint(nameEditText.background, Color.WHITE)
//
//                    genderEditText.isClickable = true
//                    mobileNumberEditText.isClickable = true
//                    nameEditText.isClickable = true

                    binding.changeprofilepiccardview.visibility = View.VISIBLE


                }
            } else {
                showProfileUpdatedView()
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun showProfileUpdatedView() {
        binding.profileupdatedsuccesstextview.visibility = View.VISIBLE
        binding.apply {
            nameEditText.visibility = View.GONE
            mobileNumberEditText.visibility = View.GONE
            genderEditText.visibility = View.GONE
            textView11.visibility = View.GONE
            textView12.visibility = View.GONE
            textView13.visibility = View.GONE
            textView14.visibility = View.GONE
            textView15.visibility = View.GONE
            textView16.visibility = View.GONE
            updateProfileButton.visibility = View.GONE
            changeprofilepiccardview.visibility = View.GONE
            Glide.with(requireContext()).load(R.drawable.profile_updated_svg)
                .into(userprofileimageview)

        }
        isInEditMode = false
    }
}