package com.heathkornblum.urbandictionary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.heathkornblum.urbandictionary.retrofit.ApiFactory
import com.heathkornblum.urbandictionary.retrofit.TermRepository
import com.heathkornblum.urbandictionary.retrofit.UdApi
import com.heathkornblum.urbandictionary.retrofit.WordDefinition
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class UdViewModel: ViewModel() {

    private val _response = MutableLiveData<JsonObject>()

    val response: LiveData<JsonObject>
        get() = _response

    init {
        fetchDefinitions()
    }

    fun fetchDefinitions() {
        UdApi.retrofitService.defineWord("wat").enqueue(
            object: Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("helphelp", t.message!!)
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    _response.value = response.body()
                }

            }
        )
    }
}

/*
class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties()
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties() {
        _response.value = "Set the Mars API Response here!"
    }
}

 */