package com.example.movieproject.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieproject.R as R1
import com.example.movieproject.model.ConstantFile
import com.example.movieproject.adapters.GameAdapter
import com.example.movieproject.util.OnItemClickListener
import com.example.movieproject.util.PaginationScrollListener
import com.example.movieproject.viewmodel.MainViewModel


@SuppressLint("NotifyDataSetChanged")
class MainFragment : Fragment(R1.layout.main_fragment), OnItemClickListener {

    companion object {
        const val KEY = "d6ed726d947a44518a080762ce705e39"
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var search: SearchView
    private var initialString = ""
    private lateinit var adapter: GameAdapter
    private lateinit var gridAdapter: GameAdapter
    private var currentPage = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        adapter = GameAdapter(R1.layout.item_list, this, activity)
        gridAdapter = GameAdapter(R1.layout.item_list2, this, activity)
        initViews(view)
        observeViewModel()
        initResultRecyclerView()

    }

    override fun onPause() {
        super.onPause()

        val sharedPref = activity?.getSharedPreferences(ConstantFile.SHARED_PREFS, Context.MODE_PRIVATE)
        if (recyclerView.adapter == adapter) {
            sharedPref?.edit()?.putInt("layoutFlag", 1)?.apply()
        } else if (recyclerView.adapter == gridAdapter) {
            sharedPref?.edit()?.putInt("layoutFlag", 2)?.apply()
        }
    }



    private fun initViews(view: View) {
        currentPage = 1
        viewModel.getGames(KEY, initialString, currentPage, true)
        recyclerView = view.findViewById(R1.id.mRecyclerView)
        search = view.findViewById<SearchView>(R1.id.search_button)
        search.setQueryHint("Search for the games")
        search.clearFocus()
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false

            }

            override fun onQueryTextChange(p0: String?): Boolean {
                currentPage = 1

//                val gameList = viewModel.gameMutableLiveData.value
                if (p0!!.length > 3) {
                      viewModel.getGames(KEY, p0, currentPage, true)
                      recyclerView.layoutManager?.scrollToPosition(0)
                } else if (p0.length!! == 0) {
                      viewModel.getGames(KEY, p0, currentPage, true)
                      recyclerView.layoutManager?.scrollToPosition(0)
                }

                return false
            }

        })

    }


    override fun onItemClick(position: Int) {

        val idGame = viewModel.getGameId(position)
        val action = idGame?.let { MainFragmentDirections.actionMainFragmentToDetailFragment(it) }
        view?.let {
            if (action != null) {
                Navigation.findNavController(it).navigate(action)
            }
        }
    }

    private fun initResultRecyclerView() {

        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.layoutManager?.let {
            recyclerView.addOnScrollListener(object : PaginationScrollListener(it) {
                override fun loadMoreItems() {
                    currentPage += 1
                    viewModel.getGames(KEY, initialString, currentPage, false)
                    isLoading = false
                }

                override val isLastPage: Boolean = false
                override var isLoading: Boolean = false
            })
        }
    }

    private fun observeViewModel() {

        viewModel.gameLiveData.observe(viewLifecycleOwner) {
            adapter.apply {
                gatheredGames = it
                notifyDataSetChanged()
            }
            gridAdapter.apply {
                gatheredGames = it
                notifyDataSetChanged()
            }
        }
        viewModel.errorStateLiveData.observe(this) {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }
    }


}
