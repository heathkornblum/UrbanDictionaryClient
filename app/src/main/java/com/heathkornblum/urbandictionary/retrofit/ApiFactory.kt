package com.heathkornblum.urbandictionary.retrofit

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


object ApiFactory {

    var client: OkHttpClient = OkHttpClient().newBuilder().build()

    var request: Request = Request.Builder()
        .url("https://mashape-community-urban-dictionary.p.rapidapi.com/define?term=wat")
        .get()
        .addHeader("x-rapidapi-host", "mashape-community-urban-dictionary.p.rapidapi.com")
        .addHeader("x-rapidapi-key", "c1d3005e65msh141ea52bae130d2p1a11dbjsn45ba3a70c455")
        .build()

    var response: Response = client.newCall(request).execute()



    interface UrbanDictionaryApi {

        @Headers(
            "x-rapidapi-host: mashape-community-urban-dictionary.p.rapidapi.com",
            "x-rapidapi-key: c1d3005e65msh141ea52bae130d2p1a11dbjsn45ba3a70c455",
            "useQueryString:true"

        )

        @GET("/define")
        fun getTerm(@Query("term") term: String) : Deferred<retrofit2.Response<UrbanDictionaryResponse>>
    }

    fun retrofit() : Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl("https://mashape-community-urban-dictionary.p.rapidapi.com")
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val urbanDictionaryApi: UrbanDictionaryApi = retrofit().create(UrbanDictionaryApi::class.java)
}