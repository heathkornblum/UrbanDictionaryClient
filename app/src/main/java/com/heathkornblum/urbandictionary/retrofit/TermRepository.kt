package com.heathkornblum.urbandictionary.retrofit

class TermRepository(private val api: ApiFactory.UrbanDictionaryApi) : BaseRepository() {
    suspend fun getTermDefinitions() : MutableList<WordDefinition>? {

        val termResponse = safeApiCall(
            call = {api.getTerm("heath").await() },
            errorMessage = "Error Fetching Term Definitions"
        )

        return termResponse?.results?.toMutableList()
    }
}