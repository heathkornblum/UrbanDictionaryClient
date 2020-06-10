package com.heathkornblum.urbandictionary.retrofit

import com.heathkornblum.urbandictionary.Definitions
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
//import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://mashape-community-urban-dictionary.p.rapidapi.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface UdApiService {
    @Headers(
        "x-rapidapi-host: mashape-community-urban-dictionary.p.rapidapi.com",
        "x-rapidapi-key: c1d3005e65msh141ea52bae130d2p1a11dbjsn45ba3a70c455"
    )

    @GET("define")
    fun defineWord(@Query("term") term: String) : Call<Definitions>
}

object UdApi {
    val retrofitService : UdApiService by lazy {
        retrofit.create(UdApiService::class.java)
    }
}