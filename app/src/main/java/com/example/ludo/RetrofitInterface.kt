package com.example.ludo

import GameMatchedPlayerDetailsModelClass
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    @POST(Constants.LOGIN_URL)
    fun login(@Body userModelClass: UserModelClass): Call<UserRegistrationResponseModel>

    @Multipart
    @POST(Constants.PROFILE_UPDATE_URL)
    fun updateProfile(
        @Part("data") userModelClass: UserModelClass,
        @Part image: MultipartBody.Part
    ): Call<GameMatchedPlayerDetailsModelClass>

    @FormUrlEncoded
    @POST(Constants.VERIFY_OTP_URL)
    fun verifyOtp(
        @Field("phone_number") phoneNumber: String,
        @Field("otp_value") otpValue: String
    ): Call<UserRegistrationResponseModel>


    @FormUrlEncoded
    @POST(Constants.HOSTAGAME_URL)
    fun hostAGAme(
        @Field("host_id") id: String,
        @Field("host_name") name: String,
        @Field("entry_fee") fee: String
    ): Call<UserRegistrationResponseModel>


    @GET(Constants.TOTALGAMESURL)
    fun getGamesList(): Call<GameListResponseModel>

    @GET(Constants.COINS_API)
    fun coinsapi(): Call<CoinsResponseModelClass>

    @FormUrlEncoded
    @POST(Constants.CANCEL_HOST_GAME_API)
    fun cancelHostApi(
        @Field("userid") userId: String,
        @Field("gameid") gameId: String
    ): Call<UserRegistrationResponseModel>

    @POST(Constants.PLAYGAME_API)
    @FormUrlEncoded
    fun playerPlayClickApi(
        @Field("playerid") userId: String,
        @Field("id") gameId: String, @Field("playername") userName: String
    ): Call<UserRegistrationResponseModel>

    @POST(Constants.PLAYERS_DETAILS_AFTER_GAMEMATCH_API)
    @FormUrlEncoded
    fun playersGameMatchDetails(@Field("id") gameid: String): Call<GameMatchedPlayerDetailsModelClass>

    @POST(Constants.SUBMIT_GAME_CODE_API)
    @FormUrlEncoded
    fun submitGameCodeHost(
        @Field("id") gameId: String,
        @Field("ludocode") gameCode: String
    ): Call<UserRegistrationResponseModel>


    @FormUrlEncoded
    @POST(Constants.GET_GAME_CODE_API)
    fun getGameCodePlayer(@Field("id") gameid: String): Call<UserRegistrationResponseModel>

    @Multipart
    @POST(Constants.POST_SCREENSHOT_FINAL_GAME_RESULT)
    fun resultsApi(
        @Part("data") gameResultModelClassToSend: GameResultModelClassToSend,
        @Part image: MultipartBody.Part
    ): Call<GameResultModelClassResponse>

    @FormUrlEncoded
    @POST(Constants.GET_GAME_RESULT_API)
    fun getGameResult(
        @Field("id") gameid: String
    ): Call<GameResultModelClassResponse>

    @FormUrlEncoded
    @POST(Constants.IS_USER_SUBMITTED_RESULTS_API)
    fun checkIfPlayerSubmittedResult(
        @Field("gameid") gameid: String,
        @Field("playerid") playerId: String
    ): Call<UserRegistrationResponseModel>




@FormUrlEncoded
@POST("api/profile")
fun getProfileData(@Field("id") userId:String):Call<GameMatchedPlayerDetailsModelClass>





    @FormUrlEncoded
    @POST(Constants.HOSTAGAME_URL_SNAKE)
    fun hostAGAme_snake(
        @Field("host_id") id: String,
        @Field("host_name") name: String,
        @Field("entry_fee") fee: String
    ): Call<UserRegistrationResponseModel>


    @GET(Constants.TOTALGAMESURL_SNAKE)
    fun getGamesList_snake(): Call<GameListResponseModel>



    @FormUrlEncoded
    @POST(Constants.CANCEL_HOST_GAME_API_SNAKE)
    fun cancelHostApi_snake(
        @Field("userid") userId: String,
        @Field("gameid") gameId: String
    ): Call<UserRegistrationResponseModel>

    @POST(Constants.PLAYGAME_API_SNAKE)
    @FormUrlEncoded
    fun playerPlayClickApi_snake(
        @Field("playerid") userId: String,
        @Field("id") gameId: String, @Field("playername") userName: String
    ): Call<UserRegistrationResponseModel>

    @POST(Constants.PLAYERS_DETAILS_AFTER_GAMEMATCH_API_SNAKE)
    @FormUrlEncoded
    fun playersGameMatchDetails_snake(@Field("id") gameid: String): Call<GameMatchedPlayerDetailsModelClass>

    @POST(Constants.SUBMIT_GAME_CODE_API_SNAKE)
    @FormUrlEncoded
    fun submitGameCodeHost_snake(
        @Field("id") gameId: String,
        @Field("ludocode") gameCode: String
    ): Call<UserRegistrationResponseModel>


    @FormUrlEncoded
    @POST(Constants.GET_GAME_CODE_API_SNAKE)
    fun getGameCodePlayer_snake(@Field("id") gameid: String): Call<UserRegistrationResponseModel>

    @Multipart
    @POST(Constants.POST_SCREENSHOT_FINAL_GAME_RESULT_SNAKE)
    fun resultsApi_snake(
        @Part("data") gameResultModelClassToSend: GameResultModelClassToSend,
        @Part image: MultipartBody.Part
    ): Call<GameResultModelClassResponse>

    @FormUrlEncoded
    @POST(Constants.GET_GAME_RESULT_API_SNAKE)
    fun getGameResult_snake(
        @Field("id") gameid: String
    ): Call<GameResultModelClassResponse>

    @FormUrlEncoded
    @POST(Constants.IS_USER_SUBMITTED_RESULTS_API_SNAKE)
    fun checkIfPlayerSubmittedResult_snake(
        @Field("gameid") gameid: String,
        @Field("playerid") playerId: String
    ): Call<UserRegistrationResponseModel>



    @GET("api/story")
    fun story(

    ): Call<StoriesModelClass>


}