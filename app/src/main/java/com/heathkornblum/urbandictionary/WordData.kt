package com.heathkornblum.urbandictionary

data class Definitions(
    val list: List<WordData>
)

data class WordData(
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