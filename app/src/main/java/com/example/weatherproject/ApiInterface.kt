package com.example.weatherproject

import retrofit2.http.GET
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApiInterface  {

    @GET("weather?zip=08852,us&appid=a5cf2a811a281fccc912a1b0e55c03e2?json")
    fun fetchFact() : Call<JsonObject>

    companion object {
        fun create() : ApiInterface {

            val client = OkHttpClient.Builder()
                .build()

            return Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .build().create(ApiInterface::class.java)
        }
    }
}