package com.example.herbalscanapplication.api

import com.example.herbalscanapplication.ApiService
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ServiceApi {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("email") name :String,
        @Field("password") password :String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") name :String,
        @Field("password") password :String,
    ):Call<ResponseBody>
}

class ApiConfig {
    fun getApiService(): ServiceApi {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fix-api-capstone.et.r.appspot.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ServiceApi::class.java)
    }
}