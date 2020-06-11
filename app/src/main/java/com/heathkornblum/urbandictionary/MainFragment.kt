package com.heathkornblum.urbandictionary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private val udViewModel: UdViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    // A reference for finding views on this fragment
    private lateinit var rootView: View

    // Use the main dispatcher to update the main thread
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(R.layout.fragment_main, container, false)
        progressBar = rootView.findViewById(R.id.progressBar)

        viewAdapter = DefinitionsListAdapter(udViewModel.listOfDefinitions.value)
        viewManager = LinearLayoutManager(this.context)

        val termObserver = Observer<Definitions> {
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

                coroutineScope.launch {
                    udViewModel.fetchDefinitions(searchText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                coroutineScope.launch {
                    udViewModel.fetchDefinitions(query)
                    udViewModel.lastLookup = query
                }
                return true
            }
        })

        // Inflate the layout for this fragment
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onResume() {
        // Don't search for nothing
        if (!udViewModel.lastLookup.isNullOrEmpty()) {
            coroutineScope.launch {
                udViewModel.fetchDefinitions(udViewModel.lastLookup)
            }
        }
        super.onResume()
    }

}
