package com.example.movieproject.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.movieproject.R
import com.example.movieproject.databinding.GameDetailBinding
import com.example.movieproject.model.Favorite
import com.example.movieproject.model.GameDetails
import com.example.movieproject.viewmodel.DetailViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailFragment : Fragment(R.layout.game_detail) {

    private lateinit var game: GameDetails
    private lateinit var idGameGathered: String
    private lateinit var binding: GameDetailBinding
    private val args: DetailFragmentArgs by navArgs()
    private var websiteLink = ""
    private var redditLink = ""
    private lateinit var detailViewModel: DetailViewModel
    private var _binding: GameDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        binding = GameDetailBinding.inflate(layoutInflater)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = GameDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        idGameGathered = args.idMovie
        if(!isFavorite()){
            binding.favorite.setText("Favourite")
        }

        if(isFavorite()){
            binding.favorite.setText("Favourited")
        }
        getGameById()
        observeGame()
        setOnClickFloatingActionButton()
        showLoading()
    }


    private fun showLoading() {

        binding.mImageView.visibility = View.GONE
        binding.mTextViewName.visibility = View.GONE
        binding.mTextViewInstruction.visibility = View.GONE

    }


    private fun getGameById() {

        detailViewModel.getGameById(idGameGathered)
    }

    private fun observeGame() {

        detailViewModel.observeGame().observe(viewLifecycleOwner) {
            this.game = it
            websiteLink = it.website
            redditLink = it.reddit_url
            setViews()
            hideLoading()
        }

    }


    private fun hideLoading() {

        binding.mImageView.visibility = View.VISIBLE
        binding.mTextViewName.visibility = View.VISIBLE
        binding.mTextViewInstruction.visibility = View.VISIBLE

    }


    private fun setViews() {

        binding.apply {
            binding.mImageView.load(game.background_image) {
                placeholder(R.color.purple_200)
                error(R.color.purple_200)
            }
            mTextViewName.text = game.name
            mTextViewInstruction.text = game.description
        }

    }

    private fun insertFavorite() {

        val favorite = Favorite(
            id = game.id!!.toInt(),
            name = game.name!!,
            imageLink = game.background_image!!,
            meta = game.metacritic!!
        )
        detailViewModel.insertFavorite(favorite)

    }

    private fun deleteFavoriteById() {

        detailViewModel.deleteFavoriteById(idGameGathered)

    }


    private fun isFavorite(): Boolean {

        return detailViewModel.isFavorite(idGameGathered)

    }
    private fun setOnClickFloatingActionButton() {

        binding.favorite.setOnClickListener {

            if (isFavorite()) {

                deleteFavoriteById()
                binding.favorite.setText("Favourite")

            } else {

                insertFavorite()
                binding.favorite.setText("Favourited")

            }

        }
                binding.gamesBack.setOnClickListener{
                    val dir = DetailFragmentDirections.actionDetailFragmentToMainFragment()
                    findNavController().navigate(dir)
                }

                binding.website?.setOnClickListener{

                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(websiteLink)))
                }

                binding.reddit?.setOnClickListener{

                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(redditLink)))
                }

                binding.show?.setOnClickListener{

                    binding.show!!.visibility =View.INVISIBLE
                    binding.mTextViewInstruction.setMaxLines(Integer.MAX_VALUE)
                }

                binding.mTextViewInstruction?.setOnClickListener{

                    binding.show!!.visibility =View.VISIBLE
                    binding.mTextViewInstruction.setMaxLines(4)
                }

    }

}
