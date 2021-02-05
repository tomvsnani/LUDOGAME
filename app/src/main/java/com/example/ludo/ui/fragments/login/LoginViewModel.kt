package com.example.ludo.ui.fragments.login

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ludo.data.UserModelClass
import com.example.ludo.data.UserRegistrationResponseModel
import com.example.ludo.ui.activities.MainActivity
import com.example.ludo.utils.RetrofitInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var retrofitInterface: RetrofitInterface

    val loginResponseLiveData = MutableLiveData<UserRegistrationResponseModel>()

    fun loginUser(): LiveData<UserRegistrationResponseModel> {

        return loginResponseLiveData
    }


    private fun sendOtpForLogin(mobileNumber: String, userName: String) {

        retrofitInterface.login(
            UserModelClass(
                phone_number = "91${mobileNumber}",
                username = userName

            )
        )?.enqueue(object : Callback<UserRegistrationResponseModel> {
            override fun onFailure(
                call: Call<UserRegistrationResponseModel>,
                t: Throwable
            ) {

            }

            override fun onResponse(
                call: Call<UserRegistrationResponseModel>,
                response: Response<UserRegistrationResponseModel>
            ) {
                if (response.isSuccessful) {

                    if (response.body()?.status == "1") {


                    }


                } else {

                }

            }

        })

    }
}






