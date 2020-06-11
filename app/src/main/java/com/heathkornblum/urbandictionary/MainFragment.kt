package com.heathkornblum.urbandictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private val udViewModel: UdViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val rootView : View = inflater.inflate(R.layout.fragment_first, container, false)
        val termObserver = Observer<Definitions> {
            viewAdapter = DefinitionsListAdapter(it)

            recyclerView = rootView.findViewById<RecyclerView>(R.id.terms_recycler_view).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

        udViewModel.response.observe(this.viewLifecycleOwner, termObserver)

        viewManager = LinearLayoutManager(this.context)

        // Inflate the layout for this fragment
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            udViewModel.fetchDefinitions()
        }
        super.onResume()
    }
}
