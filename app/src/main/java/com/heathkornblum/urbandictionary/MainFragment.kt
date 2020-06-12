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

    private var ascending = true
    private var byThumbsUp = true

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
            recyclerView.adapter = DefinitionsListAdapter(udViewModel.listOfDefinitions.value)
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
            udViewModel.sortWordsByThumbs(true, !ascending)
            ascending = !ascending
        }

        val thumbsDown = rootView.findViewById<TextView>(R.id.thumbsdownHeading)
        thumbsDown.setOnClickListener {
            udViewModel.sortWordsByThumbs(false, !ascending)
            ascending = !ascending
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onResume() {
        // Don't search for nothing
        if (!udViewModel.lastLookup.isNullOrEmpty()) {
            val job = coroutineScope.launch {
                // this keeps state between events such as screen rotation
                udViewModel.fetchDefinitions(udViewModel.lastLookup)
            }
        }
        super.onResume()
    }

//    private fun sortByThumbs(listOfWords: List<WordData>?, upOrDown: Int) : List<WordData>? {
//        return when (upOrDown) {
//            0 -> {
//                listOfWords?.sortedBy { it.thumbs_up }
//            }
//            1 -> {
//                listOfWords?.sortedBy { it.thumbs_down }
//            }
//            else -> listOfWords?.sortedBy { it.thumbs_up }
//        }
//    }

    private fun hideKeyboard() {
        val systemService: InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        systemService.hideSoftInputFromWindow(searchView.windowToken, 0)
    }
}
