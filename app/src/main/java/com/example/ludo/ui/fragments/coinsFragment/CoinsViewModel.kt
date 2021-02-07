package com.example.ludo.ui.fragments.coinsFragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ludo.R
import com.example.ludo.ResultSealedClass
import com.example.ludo.data.CoinsResponseModelClass
import com.example.ludo.data.GameDetailsModelClass
import com.example.ludo.data.GameListResponseModel
import com.example.ludo.data.UserRegistrationResponseModel
import com.example.ludo.ui.activities.MainActivity
import com.example.ludo.ui.fragments.GameMatchingFragment
import com.example.ludo.ui.fragments.GameResultFragment
import com.example.ludo.utils.Constants
import com.example.ludo.utils.RetrofitInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CoinsViewModel @Inject constructor(val retrofitInterface: RetrofitInterface) : ViewModel() {

    private val gameListLiveData = MutableLiveData<ResultSealedClass<List<GameDetailsModelClass>>>()

    private val gameHostLiveData =
        MutableLiveData<ResultSealedClass<UserRegistrationResponseModel>>()

    private val getGameCoinsLiveData = MutableLiveData<ResultSealedClass<CoinsResponseModelClass>>()

    private val sendUsernameLiveData =
        MutableLiveData<ResultSealedClass<UserRegistrationResponseModel>>()


    private val checkIfUserSubmittedResultLiveData =
        MutableLiveData<ResultSealedClass<UserRegistrationResponseModel>>()

    private val cancelGameLiveData =
        MutableLiveData<ResultSealedClass<UserRegistrationResponseModel>>()

    fun cancelGame(
        type: String,
        userId: String,
        id: String
    ): MutableLiveData<ResultSealedClass<UserRegistrationResponseModel>> {

        cancelGameLocal(type, userId, id)
        return cancelGameLiveData

    }

    private fun cancelGameLocal(type: String, userId: String, id: String) {

        val gameType by lazy {
            if (type == Constants.LUDOGAMETYPE)
                retrofitInterface.cancelHostApi(userId, id)
            else
                retrofitInterface.cancelHostApi_snake(userId, id)
        }


        gameType.enqueue(
            object : Callback<UserRegistrationResponseModel> {
                override fun onFailure(
                    call: Call<UserRegistrationResponseModel>,
                    t: Throwable
                ) {
                    cancelGameLiveData.value =
                        ResultSealedClass.Failure("", t, Constants.NETWORKFAIL)
                }

                override fun onResponse(
                    call: Call<UserRegistrationResponseModel>,
                    response: Response<UserRegistrationResponseModel>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {

                            cancelGameLiveData.value =
                                ResultSealedClass.Success(data = response.body())

                        } else {
                            cancelGameLiveData.value = ResultSealedClass.Failure(
                                response.body()?.message,
                                null,
                                Constants.REQUIREMENTFAIL
                            )
                        }

                    } else {
                        cancelGameLiveData.value = ResultSealedClass.Failure(
                            response.toString(),
                            null,
                            Constants.REQUIREMENTFAIL
                        )
                    }


                }
            }
        )


    }


    fun checkIfUserSubmittedResult(
        type: String,
        id: String,
        userId: String
    ): MutableLiveData<ResultSealedClass<UserRegistrationResponseModel>> {

        checkIfUserSubmittedResultLocal(type, id, userId)

        return checkIfUserSubmittedResultLiveData
    }

    private fun checkIfUserSubmittedResultLocal(type: String, id: String, userId: String) {
        val gameType by lazy {
            if (type == Constants.LUDOGAMETYPE)
                retrofitInterface.checkIfPlayerSubmittedResult(id, userId)
            else
                retrofitInterface.checkIfPlayerSubmittedResult_snake(id, userId)
        }

        gameType.enqueue(object : Callback<UserRegistrationResponseModel> {
            override fun onFailure(call: Call<UserRegistrationResponseModel>, t: Throwable) {
                checkIfUserSubmittedResultLiveData.value =
                    ResultSealedClass.Failure("", t, Constants.NETWORKFAIL)
            }

            override fun onResponse(
                call: Call<UserRegistrationResponseModel>,
                response: Response<UserRegistrationResponseModel>
            ) {
                if (response.isSuccessful) {


                    checkIfUserSubmittedResultLiveData.value =
                        ResultSealedClass.Success(data = response.body())


                } else {
                    checkIfUserSubmittedResultLiveData.value =
                        ResultSealedClass.Failure(
                            response.toString(),
                            null,
                            Constants.REQUIREMENTFAIL
                        )

                }


            }
        })


    }


    fun sendUsernameToServer(
        type: String,
        userId: String,
        gameId: String,
        userName: String
    ): MutableLiveData<ResultSealedClass<UserRegistrationResponseModel>> {

        sendUserNameLocal(type, userId, gameId, userName)

        return sendUsernameLiveData
    }

    private fun sendUserNameLocal(type: String, userId: String, gameId: String, userName: String) {
        val gameType by lazy {
            if (type == Constants.LUDOGAMETYPE)
                retrofitInterface.playerPlayClickApi(userId, gameId, userName)
            else
                retrofitInterface.playerPlayClickApi_snake(userId, gameId, userName)
        }

        gameType.enqueue(object : Callback<UserRegistrationResponseModel> {
            override fun onFailure(
                call: Call<UserRegistrationResponseModel>,
                t: Throwable
            ) {
                sendUsernameLiveData.value =
                    ResultSealedClass.Failure("", t, Constants.NETWORKFAIL)

            }

            override fun onResponse(
                call: Call<UserRegistrationResponseModel>,
                response: Response<UserRegistrationResponseModel>
            ) {
                try {
                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {
                            sendUsernameLiveData.value =
                                ResultSealedClass.Success(data = response.body())

                        } else {
                            sendUsernameLiveData.value =
                                ResultSealedClass.Failure(
                                    response.body()?.message,
                                    null,
                                    Constants.REQUIREMENTFAIL
                                )
                        }

                    } else {
                        sendUsernameLiveData.value =
                            ResultSealedClass.Failure(
                                response.toString(),
                                null,
                                Constants.REQUIREMENTFAIL
                            )
                    }

                } catch (e: Exception) {

                }
            }
        })
    }


    fun getAvailableCoinsToPlay(): MutableLiveData<ResultSealedClass<CoinsResponseModelClass>> {
        getGameCoinsLocal()
        return getGameCoinsLiveData
    }

    private fun getGameCoinsLocal() {
        retrofitInterface.coinsapi()
            .enqueue(object : Callback<CoinsResponseModelClass> {
                override fun onFailure(call: Call<CoinsResponseModelClass>, t: Throwable) {
                    gameHostLiveData.value = ResultSealedClass.Failure(
                        "",
                        t,
                        Constants.NETWORKFAIL
                    )
                }

                override fun onResponse(
                    call: Call<CoinsResponseModelClass>,
                    response: Response<CoinsResponseModelClass>
                ) {

                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {
                            if (response.body()?.data != null && response.body()?.data?.isNotEmpty()!!) {
                                getGameCoinsLiveData.value =
                                    ResultSealedClass.Success(data = response.body())
                            }

                        } else {
                            gameHostLiveData.value = ResultSealedClass.Failure(
                                response.body()?.message,
                                null,
                                Constants.REQUIREMENTFAIL
                            )
                        }

                    } else {
                        gameHostLiveData.value = ResultSealedClass.Failure(
                            response.toString(),
                            null,
                            Constants.REQUIREMENTFAIL
                        )
                    }


                }
            })
    }


    fun getGamesList(gameType: String): MutableLiveData<ResultSealedClass<List<GameDetailsModelClass>>> {
        getGamesListLocal(gameType)
        return gameListLiveData
    }

    fun hostGame(
        gameType: String,
        id: String,
        name: String,
        selectedCoins: String
    ): MutableLiveData<ResultSealedClass<UserRegistrationResponseModel>> {

        hostGameLocal(gameType, id, name, selectedCoins)

        return gameHostLiveData
    }


    private fun hostGameLocal(gameType: String, id: String, name: String, selectedCoins: String) {

        val gameHostType by lazy {
            if (gameType == Constants.LUDOGAMETYPE)
                retrofitInterface.hostAGAme(id, name, selectedCoins)
            else {
                retrofitInterface.hostAGAme_snake(id, name, selectedCoins)
            }
        }


        gameHostType.enqueue(object : Callback<UserRegistrationResponseModel> {
            override fun onFailure(
                call: Call<UserRegistrationResponseModel>,
                t: Throwable
            ) {
                gameHostLiveData.value = ResultSealedClass.Failure("", t, Constants.NETWORKFAIL)

            }

            override fun onResponse(
                call: Call<UserRegistrationResponseModel>,
                response: Response<UserRegistrationResponseModel>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.status == "1") {

                        gameHostLiveData.value = ResultSealedClass.Success(data = response.body())


                    } else {
                        gameHostLiveData.value = ResultSealedClass.Failure(
                            response.body()?.message,
                            null,
                            Constants.REQUIREMENTFAIL
                        )
                    }
                } else {
                    gameHostLiveData.value = ResultSealedClass.Failure(
                        response.toString(),
                        null,
                        Constants.REQUIREMENTFAIL
                    )


                }

            }
        })


    }


    private fun getGamesListLocal(gameType: String) {

        val gamelistType by lazy {
            if (gameType == Constants.LUDOGAMETYPE)
                retrofitInterface.getGamesList()
            else {
                retrofitInterface.getGamesList_snake()
            }
        }



        gamelistType
            ?.enqueue(object : Callback<GameListResponseModel> {
                override fun onFailure(call: Call<GameListResponseModel>, t: Throwable) {
                    gameListLiveData.value =
                        ResultSealedClass.Failure("", t, Constants.NETWORKFAIL)
                }

                override fun onResponse(
                    call: Call<GameListResponseModel>,
                    response: Response<GameListResponseModel>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {

                            var gameHostedOrProgressList =
                                mutableListOf<GameDetailsModelClass>()
                            var list: MutableList<GameDetailsModelClass>? =
                                (response.body()?.data?.let { list ->
                                    list.filter {       // removing modelifit is host or player to add it
                                        // below and display it on top of list
                                        if (it.game_status == "0") {
                                            gameHostedOrProgressList.add(it)

                                            return@filter false
                                        }
                                        return@filter true
                                    }
                                } as MutableList<GameDetailsModelClass>?)!!
                            for (i in gameHostedOrProgressList) {

                                list?.add(0, i)

                            }


                            gameListLiveData.value =
                                ResultSealedClass.Success(data = list)

                        } else {

                            gameListLiveData.value =
                                ResultSealedClass.Failure(
                                    response.body()?.message,
                                    null,
                                    Constants.REQUIREMENTFAIL
                                )

                        }
                    } else {
                        gameListLiveData.value =
                            ResultSealedClass.Failure(
                                response.body()?.toString(),
                                null,
                                Constants.REQUIREMENTFAIL
                            )
                    }

                }
            })

    }


}