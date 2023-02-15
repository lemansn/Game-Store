package com.example.movieproject.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieproject.R
import com.example.movieproject.databinding.GameFavoriteBinding
import com.example.movieproject.adapters.FavoriteAdapter
import com.example.movieproject.adapters.GameAdapter
import com.example.movieproject.viewmodel.FavViewModel

//import com.example.movieproject.favorites

class FavoriteFragment : Fragment(R.layout.game_favorite) {

    private val viewModel: FavViewModel by viewModels()
    private lateinit var favadapter: FavoriteAdapter
    private var size :Int =0
    private var _binding: GameFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteViewModel: FavViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoriteViewModel = ViewModelProvider(this)[FavViewModel::class.java]
        favadapter = FavoriteAdapter()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = GameFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observeFavorites()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {

        binding.favRecyclerView.apply {
            adapter = favadapter
        }

        val itemSwipe = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Delete Item.")
                builder.setMessage("Are you sure?")
                val pos = viewHolder.adapterPosition
                val id = (favadapter.getPos(pos).id)
                builder.setPositiveButton("Confirm"){dialog, which ->
                    favoriteViewModel.deleteFavoriteById(id.toString())
                    favadapter.notifyItemRemoved(pos)
                }

                builder.setNegativeButton("Cancel"){dialog, which ->
                    favadapter.notifyItemChanged(pos)
                }
                builder.show()

            }


        }

        val swap = ItemTouchHelper(itemSwipe)
        swap.attachToRecyclerView(binding.favRecyclerView)
        favadapter.notifyDataSetChanged()
    }

    private fun observeFavorites() {

        viewModel.observeFavorites().observe(viewLifecycleOwner) {
            favadapter.setFavorites(it)
             size = it.size
            if(size >0){
                binding.text.setText(String.format("%s(%d)", "Favourites",size))
            }
            else if(size ==0){
                binding.favRecyclerView.visibility = View.INVISIBLE
                binding.noFavText?.visibility = View.VISIBLE
                binding.text.setText("Favourites")

            }
        }
    }

}


