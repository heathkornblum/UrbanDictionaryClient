package com.heathkornblum.urbandictionary.retrofit

import okhttp3.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface UrbanDictionaryService {

    @GET("/define")
    fun defineWord(@Query("term") term: String) : Call
}