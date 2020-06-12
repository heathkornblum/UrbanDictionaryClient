package com.heathkornblum.urbandictionary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heathkornblum.urbandictionary.retrofit.UdApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UdViewModel: ViewModel() {
    enum class Progress {
        LOADING, FINISHED, ERROR
    }

    private val _response = MutableLiveData<Definitions>()

    var lastLookup : String? = null
    var thumbsUpSearch = true
    var descendingSearch = true

    var listOfDefinitions = MutableLiveData<List<WordData>>()

    var status = MutableLiveData<Progress>()

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
                        listOfDefinitions.value = response.body()?.list
                        lastLookup = lookupTerm
                        status.value = Progress.FINISHED
                    }
                }
            )
        }

    }

    fun sortWordsByThumbs(thumbsUpOrDown: Boolean = true, descending: Boolean = true) {
        // true for thumbs up, false for thumbs down

        listOfDefinitions.value = when (thumbsUpOrDown) {
            true -> {
                if (descending) {
                    listOfDefinitions.value?.sortedByDescending { it.thumbs_up }
                } else {
                    listOfDefinitions.value?.sortedBy { it.thumbs_up }
                }
            }
            false -> {
                if (descending) {
                    listOfDefinitions.value?.sortedByDescending { it.thumbs_down }
                } else {
                    listOfDefinitions.value?.sortedBy { it.thumbs_down }
                }
            }
        }
    }
}
