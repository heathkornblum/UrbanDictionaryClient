package com.heathkornblum.urbandictionary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

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
             textview_first.text = it.list[0].definition
            viewAdapter = DefinitionsListAdapter(it)

            recyclerView = rootView.findViewById<RecyclerView>(R.id.terms_recycler_view).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

        viewAdapter = DefinitionsListAdapter(null)
        udViewModel.response.observe(this.viewLifecycleOwner, termObserver)

        viewManager = LinearLayoutManager(this.context)


//        activity?.let {
//            recyclerView = it.findViewById<RecyclerView>(R.id.terms_recycler_view).apply {
//
//            }
//        }

        // Inflate the layout for this fragment
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onResume() {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {

            udViewModel.fetchDefinitions()
//            textview_first.text = udViewModel.bodyString
////            udViewModel.response.observe(viewLifecycleOwner, object : Definitions() ->
////            )


        }
        super.onResume()
    }
}
