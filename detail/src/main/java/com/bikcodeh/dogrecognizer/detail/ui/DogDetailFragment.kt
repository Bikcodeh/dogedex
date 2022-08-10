package com.bikcodeh.dogrecognizer.detail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.bikcodeh.dogrecognizer.core.model.Dog
import com.bikcodeh.dogrecognizer.core.util.extension.decode
import com.bikcodeh.dogrecognizer.detail.databinding.FragmentDogDetailBinding
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.net.URLEncoder

@AndroidEntryPoint
class DogDetailFragment : Fragment() {

    private var _binding: FragmentDogDetailBinding? = null
    private val binding: FragmentDogDetailBinding
        get() = _binding!!

    private var dog: Dog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Dog> = moshi.adapter(Dog::class.java)
        arguments?.getString("dog")?.let {
            val decode = URLDecoder.decode(it, "UTF-8")
            try {
                dog = jsonAdapter.fromJson(decode)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        dog?.let {
            with(binding) {
                 dogIndex.text = "#${it.index}"
                 lifeExpectancy.text = "${it.lifeExpectancy} years"
                 binding.dog = it
                 dogImage.load(it.imageUrl)
                 closeButton.setOnClickListener {
                     findNavController().popBackStack()
                 }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}