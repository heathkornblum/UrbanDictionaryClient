package com.heathkornblum.urbandictionary

import androidx.arch.core.executor.testing.InstantTaskExecutorRule

import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import org.hamcrest.Matchers.greaterThan

@RunWith(JUnit4::class)
class UdViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val udViewModel = UdViewModel()

    private val wordData = listOf(
        WordData("sample definition",
            "http://example.com",
            4,
            "Mockthor",
            "TERM",
            123,
            "thumbsup",
            "paper",
            "example",
            43),
        WordData("sample definition 2",
            "http://2.example.com",
            2,
            "Mockthor2",
            "TERM2",
            1232,
            "thumbsup2",
            "paper2",
            "example2",
            432))



    @Test
    fun orderByThumbsDownDescending() {
        udViewModel.listOfDefinitions.value = wordData

        // index 1, the second item is a known value for most thumbs down
        val mostThumbsDown = wordData[1].thumbs_down

        // sort the local list, and assign to newData
        udViewModel.sortWordsByThumbs(false, true)

        //
        assertEquals(mostThumbsDown,  udViewModel.listOfDefinitions.value?.get(0)?.thumbs_down)
    }

}