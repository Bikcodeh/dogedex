package com.bikcodeh.dogrecognizer.presentation.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bikcodeh.dogrecognizer.databinding.FragmentFavoriteBinding
import com.bikcodeh.dogrecognizer.domain.model.Dog
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
        setUpListeners()
        favoriteViewModel.getFavoriteDogs()
    }

    private fun setUpCollectors() {
        observeFlows { scope ->
            scope.launch {
                favoriteViewModel.favoriteDogs.collect { state ->
                    binding.favoriteLoadingPb.isVisible = state.isLoading
                    handleOnSuccess(state.dogs)
                    state.error?.let {
                        handleOnError(it)
                    } ?: run {
                        binding.viewErrorFavorite.root.hide()
                    }
                }
            }
        }
    }

    private fun handleOnSuccess(dogs: List<Dog>) {
        if (dogs.isEmpty()) {
            binding.favoriteRv.hide()
            binding.emptyFavoritesView.root.show()
        } else {
            favoriteAdapter.submitList(dogs)
            binding.emptyFavoritesView.root.hide()
            binding.favoriteRv.show()
        }
    }

    private fun handleOnError(resId: Int) {
        with (binding) {
            favoriteRv.hide()
            emptyFavoritesView.root.hide()
            root.snack(getString(resId))
            viewErrorFavorite.errorTextTv.text = getString(resId)
            viewErrorFavorite.root.show()
        }
    }

    private fun setUpViews() {
        binding.favoriteRv.apply {
            adapter = favoriteAdapter
        }
    }

    private fun setUpListeners() {
        binding.viewErrorFavorite.tryAgainBtn.setOnClickListener {
            favoriteViewModel.getFavoriteDogs()
        }
    }
}