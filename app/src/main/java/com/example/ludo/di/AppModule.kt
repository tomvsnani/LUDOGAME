package com.example.ludo.di

import android.util.Log
import com.example.ludo.utils.Constants
import com.example.ludo.utils.RetrofitInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {




    @Provides
    @Singleton
    fun getRetrofit() :Retrofit{
        val client = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val request = chain.request()

                val response: okhttp3.Response = chain.proceed(request)
                val rawJson: String = response.body()?.string()!!
                Log.d("interceptor", rawJson)


                // Re-create the response before returning it because body can be read only once

                // Re-create the response before returning it because body can be read only once
                return response.newBuilder()
                    .body(ResponseBody.create(response.body()?.contentType(), rawJson))
                    .build()
            }
        }).connectTimeout(1, TimeUnit.MINUTES).readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS).build()


        return Retrofit.Builder().let {
            it.client(client)
            it.addConverterFactory(GsonConverterFactory.create())
            it.baseUrl(Constants.BASEURL)
            it.build()
        }
    }



    @Provides
    @Singleton
    fun getRetrofitInterface(retrofit: Retrofit)=retrofit.create(RetrofitInterface::class.java)

}