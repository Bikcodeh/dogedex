package com.bikcodeh.dogrecognizer.presentation.doglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.databinding.ActivityDogListBinding
import com.bikcodeh.dogrecognizer.domain.model.Dog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DogListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDogListBinding
    private val dogAdapter: DogListAdapter by lazy { DogListAdapter() }
    private val dogViewModel by viewModels<DogListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDogListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
        setUpObservers()
    }

    private fun setUpViews() {
        binding.dogListRv.apply {
            layoutManager = LinearLayoutManager(this@DogListActivity)
            adapter = dogAdapter
        }
    }

    private fun setUpObservers() {
        dogViewModel.dogsLivedata.observe(this) {
            dogAdapter.submitList(it)
        }
    }
}