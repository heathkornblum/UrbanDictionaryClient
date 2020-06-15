package com.heathkornblum.urbandictionary

import androidx.lifecycle.Observer
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

import org.hamcrest.Matchers.greaterThan
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UdViewModelTestInstrumented {

    val viewModel = UdViewModel()

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun checkLastLookup() {

        viewModel.fetchDefinitions("heath")
        assertEquals(null, viewModel.listOfDefinitions.value?.size)

    }
}