package com.bikcodeh.dogrecognizer.presentation.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bikcodeh.dogrecognizer.databinding.FragmentFavoriteBinding
import com.bikcodeh.dogrecognizer.presentation.util.extension.hide
import com.bikcodeh.dogrecognizer.presentation.util.extension.observeFlows
import com.bikcodeh.dogrecognizer.presentation.util.extension.show
import com.bikcodeh.dogrecognizer.presentation.util.extension.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding: FragmentFavoriteBinding
        get() = _binding!!

    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private val favoriteAdapter: FavoriteAdapter by lazy { FavoriteAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpCollectors()
        favoriteViewModel.getFavoriteDogs()
    }

    private fun setUpCollectors() {
        observeFlows { scope ->
            scope.launch {
                favoriteViewModel.favoriteDogs.collect { state ->
                    if (state.dogs.isEmpty()) {
                        binding.emptyFavoritesView.root.show()
                        binding.favoriteRv.hide()
                    } else {
                        favoriteAdapter.submitList(state.dogs)
                        binding.favoriteRv.show()
                        binding.emptyFavoritesView.root.hide()
                    }
                    binding.favoriteLoadingPb.isVisible = state.isLoading
                    state.error?.let {
                        requireView().snack(getString(it))
                    }
                }
            }
        }
    }

    private fun setUpViews() {
        binding.favoriteRv.apply {
            adapter = favoriteAdapter
        }
    }
}