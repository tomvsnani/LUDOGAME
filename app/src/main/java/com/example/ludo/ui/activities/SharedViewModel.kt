package com.example.ludo.ui.activities

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ludo.ResultSealedClass
import com.example.ludo.data.GameMatchedPlayerDetailsModelClass
import com.example.ludo.utils.Constants
import com.example.ludo.utils.RetrofitInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(val retrofitInterface: RetrofitInterface) : ViewModel() {


    private var coinsLiveData =
        MutableLiveData<ResultSealedClass<GameMatchedPlayerDetailsModelClass>>()

    private var ludoGamePlayeDetailsLiveData =
        MutableLiveData<ResultSealedClass<GameMatchedPlayerDetailsModelClass>>()

    private var snakeGamePlayeDetailsLiveData =
        MutableLiveData<ResultSealedClass<GameMatchedPlayerDetailsModelClass>>()

    fun getCoins(id: String): LiveData<ResultSealedClass<GameMatchedPlayerDetailsModelClass>> {

        getUserCoins(id)

        return coinsLiveData
    }

    fun getLudoPlayerDetails(gameId: String): LiveData<ResultSealedClass<GameMatchedPlayerDetailsModelClass>> {

        return ludoGamePlayeDetailsLiveData
    }


    fun getSnakePlayerDetails(gameId: String): LiveData<ResultSealedClass<GameMatchedPlayerDetailsModelClass>> {

        return snakeGamePlayeDetailsLiveData
    }


    private fun getUserCoins(id: String) {

        retrofitInterface?.getProfileData(id)?.enqueue(
            object : Callback<GameMatchedPlayerDetailsModelClass> {
                override fun onFailure(
                    call: Call<GameMatchedPlayerDetailsModelClass>,
                    t: Throwable
                ) {
                    coinsLiveData.value =
                        ResultSealedClass.Failure(throwable = t, status = Constants.NETWORKFAIL);
                }

                override fun onResponse(
                    call: Call<GameMatchedPlayerDetailsModelClass>,
                    response: Response<GameMatchedPlayerDetailsModelClass>
                ) {
                    if (response.isSuccessful) {
                        if (response?.body()?.status == "1") {


                            if (response.body()?.data != null && response.body()?.data?.isNotEmpty()!!) {

                                coinsLiveData.value =
                                    ResultSealedClass.Success(data = response.body()!!)
                            }

                        } else {

                            coinsLiveData.value =
                                ResultSealedClass.Failure(
                                    response.body()?.message,
                                    null,
                                    Constants.REQUIREMENTFAIL
                                )

                        }
                    } else {

                        coinsLiveData.value =
                            ResultSealedClass.Failure(
                                response.body().toString(),
                                null,
                                Constants.REQUIREMENTFAIL
                            )

                    }

                }
            }
        )
    }


}