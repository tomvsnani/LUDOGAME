package com.example.ludo

data class UserRegistrationResponseModel(
    var status: String = "",
    var message: String = "",
    var data: UserModelClass
)