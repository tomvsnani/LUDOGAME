package com.example.ludo

sealed class ResultSealedClass<T> {


    data class Success<T>(var message: String = "success", val data: T?):ResultSealedClass<T>()
    data class Failure<T>(var message: String? = "fail", val throwable: Throwable?, val status: String):ResultSealedClass<T>()
}