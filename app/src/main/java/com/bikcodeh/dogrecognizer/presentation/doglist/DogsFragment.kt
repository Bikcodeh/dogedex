package com.bikcodeh.dogrecognizer.presentation.doglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bikcodeh.dogrecognizer.databinding.FragmentDogsBinding
import com.bikcodeh.dogrecognizer.domain.model.Dog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DogsFragment : Fragment() {

    private var _binding: FragmentDogsBinding? = null
    private val binding: FragmentDogsBinding
        get() = _binding!!

    private val dogViewModel by viewModels<DogListViewModel>()

    private val dogAdapter: DogListAdapter by lazy {
        DogListAdapter {
            navigateToDetail(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews() {
        binding.dogListRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dogAdapter
        }
    }

    private fun setUpObservers() {
        dogViewModel.dogsLivedata.observe(viewLifecycleOwner) {
            dogAdapter.submitList(it)
        }
    }

    private fun navigateToDetail(dog: Dog) {
        val action = DogsFragmentDirections.actionDogsFragmentToDogDetailFragment(dog)
        findNavController().navigate(action)
    }
}