package com.bikcodeh.dogrecognizer.presentation.dogdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bikcodeh.dogrecognizer.databinding.FragmentDogDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DogDetailFragment : Fragment() {

    private var _binding: FragmentDogDetailBinding? = null
    private val binding: FragmentDogDetailBinding
        get() = _binding!!

    private val args: DogDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), args.itemDog.name.uppercase(), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}