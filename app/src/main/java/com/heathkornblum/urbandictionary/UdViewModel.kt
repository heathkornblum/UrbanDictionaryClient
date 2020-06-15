package com.heathkornblum.urbandictionary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UdViewModel: ViewModel() {
    enum class Progress {
        LOADING, FINISHED, ERROR
    }

    private val _response = MutableLiveData<Definitions>()

    var lastLookup : String? = null
    var thumbsUpSearch : Boolean? = null
    var ascendingSearch : Boolean? = null

    var listOfDefinitions = MutableLiveData<List<WordData>>()

    var status = MutableLiveData<Progress>()

    fun fetchDefinitions(lookupTerm: String? = lastLookup) {
        status.postValue(Progress.LOADING)
        // if there is no lookup string, do nothing
        lookupTerm?.let {
            UdApi.retrofitService.defineWord(lookupTerm).enqueue(
                object: Callback<Definitions> {
                    override fun onFailure(call: Call<Definitions>, t: Throwable) {
                        status.postValue(Progress.ERROR)
                    }

                    override fun onResponse(call: Call<Definitions>, response: Response<Definitions>) {
                        _response.value = response.body()
                        listOfDefinitions.value = response.body()?.list
                        sortWordsByThumbs(thumbsUpSearch, ascendingSearch)
                        lastLookup = lookupTerm
                        status.postValue(Progress.FINISHED)
                    }
                }
            )
        }

    }

    fun sortWordsByThumbs(thumbsUpOrDown: Boolean?, descending: Boolean?) {
        // true for thumbs up, false for thumbs down

        listOfDefinitions.postValue( when (thumbsUpOrDown) {
            true -> {
                when (descending) {
                    true -> listOfDefinitions.value?.sortedByDescending { it.thumbs_up }
                    false -> listOfDefinitions.value?.sortedBy { it.thumbs_up }
                    null -> listOfDefinitions.value?.sortedByDescending { it.thumbs_up }
                }
            }
            false -> {
                when (descending) {
                    true -> listOfDefinitions.value?.sortedByDescending { it.thumbs_down }
                    false -> listOfDefinitions.value?.sortedBy { it.thumbs_down }
                    null -> listOfDefinitions.value?.sortedByDescending { it.thumbs_down }
                }
            }
            null -> listOfDefinitions.value?.sortedByDescending { it.thumbs_up }
        })
    }
}
