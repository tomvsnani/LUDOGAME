package com.example.ludo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.ludo.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment(R.layout.fragment_login) {
    lateinit var binding: FragmentLoginBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentLoginBinding.bind(view)

        binding.sednooverifyotpButton.setOnClickListener {
            if (!binding.sednooverifyotpButton.text.toString().toLowerCase().contains("verify"))
                sendOtpForLogin()
            else {
                verifyOtp()
            }
        }

        (activity as MainActivity).setUpFragmentsToolbarProperties(
            resources.getString(R.string.app_name),
            true, null
        )

        super.onViewCreated(view, savedInstanceState)
    }

    private fun verifyOtp() {
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        if (areFieldsValid()) {
            (activity as MainActivity).retrofit?.verifyOtp(
                "91${binding.mobilenumberEditText.text.toString()}",
                binding.pinView.text.toString()
            )?.enqueue(object : Callback<UserRegistrationResponseModel> {
                override fun onFailure(call: Call<UserRegistrationResponseModel>, t: Throwable) {
                    (activity as MainActivity).showToast(t.toString())
                    (activity as MainActivity).binding.progressbar.visibility = View.GONE
                }

                override fun onResponse(
                    call: Call<UserRegistrationResponseModel>,
                    response: Response<UserRegistrationResponseModel>
                ) {
                    if (response.isSuccessful) {
                        (activity as MainActivity).binding.progressbar.visibility = View.GONE
                        if (response.body()?.status == "1") {
                            (activity as MainActivity).apply {
                                showToast("Otp Verified")
                                sessionManageMent(response.body()?.data!!)
                                loadFragment(SelectAGameFragment(), true, "home")
                            }
                        }
                    } else {
                        (activity as MainActivity).binding.progressbar.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun sendOtpForLogin() {
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        if (areFieldsValid()) {
            (activity as MainActivity).retrofit?.login(
                UserModelClass(
                    phone_number = "91${binding.mobilenumberEditText.text.toString()}",
                    username = binding.usernameEditText.text.toString()

                )
            )?.enqueue(object : Callback<UserRegistrationResponseModel> {
                override fun onFailure(
                    call: Call<UserRegistrationResponseModel>,
                    t: Throwable
                ) {
                    (activity as MainActivity).showToast(t.toString())
                }

                override fun onResponse(
                    call: Call<UserRegistrationResponseModel>,
                    response: Response<UserRegistrationResponseModel>
                ) {
                    if (response.isSuccessful) {

                        if (response.body()?.status == "1") {
                            (activity as MainActivity).showToast(response.body()?.message!!)
                            binding.sednooverifyotpButton.text = "Verify Otp"
                            binding.apply {
                                pinView.visibility = View.VISIBLE

                                DrawableCompat.setTint(
                                    usernameEditText.background,
                                    Color.TRANSPARENT
                                )
                                usernameEditText.clearFocus()
                                mobilenumberEditText.clearFocus()
                                usernameEditText.isFocusableInTouchMode = false
                                usernameEditText.isClickable=false
                                mobilenumberEditText.isClickable=false

                                mobilenumberEditText.isFocusableInTouchMode = false
                                DrawableCompat.setTint(
                                    mobilenumberEditText.background,
                                    Color.TRANSPARENT
                                )

                            }
                            (activity as MainActivity).binding.progressbar.visibility = View.GONE
                        } else {
                            response.body()?.message?.let { it1 ->
                                (activity as MainActivity).binding.progressbar.visibility = View.GONE
                                (activity as MainActivity).showToast(
                                    it1
                                )
                            }
                        }

                    } else {
                        (activity as MainActivity).binding.progressbar.visibility = View.GONE
                        Log.d("Failedd", response.toString())
                    }
                }
            })
        } else {
            (activity as MainActivity).showToast("Please enter all fields")
        }
    }

    private fun areFieldsValid(): Boolean {
        return binding.let {
            it.usernameEditText.text.isNotEmpty() && it.mobilenumberEditText.text.isNotEmpty()
        }
    }

    override fun onStop() {
        (activity as MainActivity).setUpFragmentsToolbarProperties(
            resources.getString(R.string.app_name),
            true, ResourcesCompat.getDrawable(resources, R.drawable.ic_hamburger, null)
        )
        super.onStop()
    }

}


