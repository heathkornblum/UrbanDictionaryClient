package com.heathkornblum.urbandictionary.retrofit

import android.util.Log
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.lang.Exception

data class WordDefinition (
    val definition: String,
    val permalink: String,
    val thumbs_up: Long,
    val author: String,
    val word: String,
    val defid: Long,
    val current_vote: String,
    val written_on: String,
    val example: String,
    val thumbs_down: Long
)

data class UrbanDictionaryResponse (
    val results: List<WordDefinition>
)

sealed class Result<out T: Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

open class BaseRepository {
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, errorMessage: String): T? {
        val result: Result<T> = safeApiResult(call, errorMessage)
        var data : T? = null

        when(result) {
            is Result.Success ->
                data = result.data
            is Result.Error ->
                Log.d(javaClass.simpleName, "$errorMessage & Exception = ${result.exception}")

        }

        return data
    }

    private suspend fun <T: Any> safeApiResult(call: suspend () -> Response<T>, errorMessage: String) : Result<T> {
        val response = call.invoke()
        if (response.isSuccessful) return Result.Success(response.body()!!)
        return Result.Error(IOException("Error occurred while getting safe API result: $errorMessage"))
    }
}