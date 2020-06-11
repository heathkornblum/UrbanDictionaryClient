package com.heathkornblum.urbandictionary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heathkornblum.urbandictionary.retrofit.UdApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UdViewModel: ViewModel() {

    private val _response = MutableLiveData<Definitions>()

    val response: LiveData<Definitions>
        get() = _response

    var bodyString : String? = null

    var listOfDefinitions :Definitions?  = null

    fun fetchDefinitions() {
        UdApi.retrofitService.defineWord("wat").enqueue(
            object: Callback<Definitions> {
                override fun onFailure(call: Call<Definitions>, t: Throwable) {
                    Log.e("helphelp", t.message!!)
                }

                override fun onResponse(call: Call<Definitions>, response: Response<Definitions>) {
                    _response.value = response.body()
                    bodyString = response.body()?.list?.get(0)?.definition
                    listOfDefinitions = response.body()
                }
            }
        )
    }
}
