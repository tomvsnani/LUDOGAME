package com.example.ludo.ui.fragments.login

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ludo.ResultSealedClass
import com.example.ludo.data.UserModelClass
import com.example.ludo.data.UserRegistrationResponseModel
import com.example.ludo.ui.activities.MainActivity
import com.example.ludo.ui.fragments.SelectAGameFragment
import com.example.ludo.utils.Constants
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

   private val loginResponseLiveData = MutableLiveData<ResultSealedClass<UserRegistrationResponseModel>>()

   private val otpVerifiedResponseLiveData =
        MutableLiveData<ResultSealedClass<UserRegistrationResponseModel>>()

    fun loginUser(
        mobileNumber: String,
        userName: String
    ): LiveData<ResultSealedClass<UserRegistrationResponseModel>> {
        sendOtpForLogin(mobileNumber, userName)
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
                loginResponseLiveData.value =
                    ResultSealedClass.Failure(

                        throwable = t,
                        status = Constants.NETWORKFAIL
                    )
            }

            override fun onResponse(
                call: Call<UserRegistrationResponseModel>,
                response: Response<UserRegistrationResponseModel>
            ) {
                if (response.isSuccessful) {

                    if (response.body()?.status == "1") {
                        loginResponseLiveData.value =
                            ResultSealedClass.Success(
                                "success",
                                response.body()!!
                            )

                    } else {
                        loginResponseLiveData.value =
                            ResultSealedClass.Failure(
                                response.body()?.message,
                                null,
                                Constants.REQUIREMENTFAIL
                            )
                    }


                } else {

                    loginResponseLiveData.value =
                        ResultSealedClass.Failure(
                            response.body().toString(),
                            null,
                            Constants.REQUIREMENTFAIL
                        )

                }

            }

        })

    }


     fun verifyOtp(
        mobileNumber: String,
        otp: String
    ): MutableLiveData<ResultSealedClass<UserRegistrationResponseModel>> {

        retrofitInterface.verifyOtp(
            "91${mobileNumber}",
            otp
        )?.enqueue(object : Callback<UserRegistrationResponseModel> {
            override fun onFailure(call: Call<UserRegistrationResponseModel>, t: Throwable) {
                otpVerifiedResponseLiveData.value = ResultSealedClass.Failure(

                    throwable = t,
                    status = Constants.NETWORKFAIL
                )
            }

            override fun onResponse(
                call: Call<UserRegistrationResponseModel>,
                response: Response<UserRegistrationResponseModel>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.status == "1") {
                        otpVerifiedResponseLiveData.value =
                            ResultSealedClass.Success(data = response.body()!!)
                    } else {
                        otpVerifiedResponseLiveData.value = ResultSealedClass.Failure(
                            response.body()?.message,
                            null,
                            Constants.REQUIREMENTFAIL
                        )
                    }

                } else {
                    otpVerifiedResponseLiveData.value = ResultSealedClass.Failure(
                        response.toString(),
                        null,
                        Constants.REQUIREMENTFAIL
                    )
                }
            }
        })

        return otpVerifiedResponseLiveData

    }


}






