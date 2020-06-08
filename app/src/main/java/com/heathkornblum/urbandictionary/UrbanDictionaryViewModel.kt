package com.heathkornblum.urbandictionary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heathkornblum.urbandictionary.retrofit.ApiFactory
import com.heathkornblum.urbandictionary.retrofit.TermRepository
import com.heathkornblum.urbandictionary.retrofit.WordDefinition
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class UrbanDictionaryViewModel: ViewModel() {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository : TermRepository = TermRepository(ApiFactory.urbanDictionaryApi)

    val termDefinitionsLiveData = MutableLiveData<MutableList<WordDefinition>>()

    fun fetchDefinitions() {
        scope.launch {
            val termDefs = repository.getTermDefinitions()
            termDefinitionsLiveData.postValue(termDefs)
        }
    }

    fun cancelAllRequests() = coroutineContext.cancel()
}