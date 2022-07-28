package com.bikcodeh.dogrecognizer.presentation.doglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.databinding.ActivityDogListBinding
import com.bikcodeh.dogrecognizer.domain.model.Dog

class DogListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDogListBinding
    private val dogAdapter: DogListAdapter by lazy { DogListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDogListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
    }

    private fun setUpViews() {
        binding.dogListRv.apply {
            layoutManager = LinearLayoutManager(this@DogListActivity)
            adapter = dogAdapter
        }
        val dogs = getFakeDogs()
        dogAdapter.submitList(dogs)
    }

    private fun getFakeDogs(): MutableList<Dog> {
        val dogList = mutableListOf<Dog>()
        dogList.add(
            Dog(
                1, 1, "Chihuahua", "Toy", 5.4,
                6.7, "", "12 - 15", "", 10.5,
                12.3
            )
        )
        dogList.add(
            Dog(
                2, 1, "Labrador", "Toy", 5.4,
                6.7, "", "12 - 15", "", 10.5,
                12.3
            )
        )
        dogList.add(
            Dog(
                3, 1, "Retriever", "Toy", 5.4,
                6.7, "", "12 - 15", "", 10.5,
                12.3
            )
        )
        dogList.add(
            Dog(
                4, 1, "San Bernardo", "Toy", 5.4,
                6.7, "", "12 - 15", "", 10.5,
                12.3
            )
        )
        dogList.add(
            Dog(
                5, 1, "Husky", "Toy", 5.4,
                6.7, "", "12 - 15", "", 10.5,
                12.3
            )
        )
        dogList.add(
            Dog(
                6, 1, "Xoloscuincle", "Toy", 5.4,
                6.7, "", "12 - 15", "", 10.5,
                12.3
            )
        )
        return dogList
    }
}