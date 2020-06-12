package com.heathkornblum.urbandictionary

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class MainFragment : Fragment() {

    private val udViewModel: UdViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var keyboardJob: Job? = null

    // A reference for finding views on this fragment
    private lateinit var rootView: View

    // Use the main dispatcher to update the main thread
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var progressBar: ProgressBar

    private var defsList: List<WordData>? = null

    // short term reference to sorting style
    private var ascending = true

    // unicode arrows for sorting UI
    private var upArrow = "\u25b2"
    private var downArrow = "\u25bc"

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(R.layout.fragment_main, container, false)
        progressBar = rootView.findViewById(R.id.progressBar)

        defsList = udViewModel.listOfDefinitions.value

        viewAdapter = DefinitionsListAdapter(defsList)
        viewManager = LinearLayoutManager(this.context)

        val termObserver = Observer<List<WordData>> {

            // This is a bit heavy, next time around will use notifyDataSetChanged() on the adapter.
            // Currently losing reference to Adapter's data because the data is changed by assignment rather
            // than by using member functions to clear() and addAll() in the View Model.
            recyclerView.adapter = DefinitionsListAdapter(udViewModel.listOfDefinitions.value)
            setArrowIcons()
        }

        recyclerView = rootView.findViewById<RecyclerView>(R.id.terms_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        udViewModel.listOfDefinitions.observe(this.viewLifecycleOwner, termObserver)

        val progressObserver = Observer<UdViewModel.Progress> { newStatus ->
            when (newStatus) {
                UdViewModel.Progress.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                UdViewModel.Progress.FINISHED -> {
                    progressBar.visibility = View.GONE
                }
                UdViewModel.Progress.ERROR -> {
                    progressBar.visibility = View.GONE
                }
                null -> {
                    progressBar.visibility = View.GONE
                }
            }
        }

        udViewModel.status.observe(this.viewLifecycleOwner, progressObserver)

        val searchView = rootView.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                // look up a blank string if empty or null to avoid an arrow lookup
                val searchText = if (newText.isNullOrEmpty()) {
                    " "
                } else newText


                val fetchJob = coroutineScope.launch {
                    udViewModel.fetchDefinitions(searchText)
                    // cancel the keyboard hiding job if it is currently running
                    keyboardJob?.let {
                        if (it.isActive) {
                            it.cancel()
                        }
                    }

                    // keyboard hides 3 seconds after adding a new letter to the search
                    keyboardJob = coroutineScope.launch {
                        delay(3000)
                        hideKeyboard()
                    }
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                coroutineScope.launch {
                    udViewModel.fetchDefinitions(query)
                    udViewModel.lastLookup = query
                    hideKeyboard()

                }
                return true
            }
        })

        val thumbsUp = rootView.findViewById<TextView>(R.id.thumbsupHeading)
        thumbsUp.setOnClickListener {
            if (!udViewModel.listOfDefinitions.value.isNullOrEmpty()) {
                // sort by and then set ascending value to it's current opposite
                udViewModel.sortWordsByThumbs(true, !ascending)
                ascending = !ascending
                thumbsUpArrow.text = if (ascending) {upArrow} else {downArrow}
                thumbsUpArrow.visibility = View.VISIBLE
                thumbsDownArrow.visibility = View.INVISIBLE
                udViewModel.ascendingSearch = ascending
                udViewModel.thumbsUpSearch = true
            }
        }

        val thumbsDown = rootView.findViewById<TextView>(R.id.thumbsdownHeading)
        thumbsDown.setOnClickListener {
            if (!udViewModel.listOfDefinitions.value.isNullOrEmpty()) {
                // sort by and then set ascending value to it's current opposite
                udViewModel.sortWordsByThumbs(false, !ascending)
                ascending = !ascending
                thumbsDownArrow.text = if (ascending) {upArrow} else {downArrow}
                thumbsDownArrow.visibility = View.VISIBLE
                thumbsUpArrow.visibility = View.INVISIBLE
                udViewModel.ascendingSearch = ascending
                udViewModel.thumbsUpSearch = false

            }

        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as FragmentActivity).actionBar?.title = "hello"
    }

    override fun onResume() {
        // Don't search for nothing
        if (!udViewModel.listOfDefinitions.value.isNullOrEmpty()) {
            val job = coroutineScope.launch {
                // this keeps state between events such as screen rotation
                udViewModel.fetchDefinitions(udViewModel.lastLookup)
                setArrowIcons()
            }
        }
        super.onResume()
    }

    /**
     * Assure that arrows show after configuration change, or hide them when there is no data
     */
    private fun setArrowIcons() {
        if (!udViewModel.listOfDefinitions.value.isNullOrEmpty()) {
            if (udViewModel.thumbsUpSearch == true) {
                thumbsUpArrow.text = if (udViewModel.ascendingSearch == true) {upArrow} else {downArrow}
                thumbsUpArrow.visibility = View.VISIBLE
                thumbsDownArrow.visibility = View.INVISIBLE
            } else if (udViewModel.thumbsUpSearch == false) {
                thumbsDownArrow.text = if (udViewModel.ascendingSearch == true) {upArrow} else {downArrow}
                thumbsDownArrow.visibility = View.VISIBLE
                thumbsUpArrow.visibility = View.INVISIBLE
            }
        } else {
            // make arrows disappear if there are no definitions to show
            thumbsDownArrow.visibility = View.INVISIBLE
            thumbsUpArrow.visibility = View.INVISIBLE
            udViewModel.thumbsUpSearch = null
        }

    }

    /**
     * hide the keyboard, catch the error if Input Method Service is null
     */
    private fun hideKeyboard() {
        try {
            val systemService: InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            systemService.hideSoftInputFromWindow(searchView.windowToken, 0)
        } catch (e: TypeCastException) {
            // Do nothing on exception
        }

    }
}
