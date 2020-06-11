package com.heathkornblum.urbandictionary

import android.util.Log
import android.widget.ProgressBar
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

    var lastLookup : String? = null

    var listOfDefinitions = MutableLiveData<Definitions>()

    var status = MutableLiveData<Progress>()

    enum class Progress {
        LOADING, FINISHED, ERROR
    }

    fun fetchDefinitions(lookupTerm: String? = lastLookup) {
        status.value = Progress.LOADING
        // if there is no lookup string, do nothing
        lookupTerm?.let {
            UdApi.retrofitService.defineWord(lookupTerm).enqueue(
                object: Callback<Definitions> {
                    override fun onFailure(call: Call<Definitions>, t: Throwable) {
                        status.value = Progress.ERROR
                    }

                    override fun onResponse(call: Call<Definitions>, response: Response<Definitions>) {
                        _response.value = response.body()
                        listOfDefinitions.value = response.body()
                        lastLookup = lookupTerm
                        status.value = Progress.FINISHED
                    }
                }
            )
        }

    }
}
