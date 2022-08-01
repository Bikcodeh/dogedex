package com.bikcodeh.dogrecognizer.presentation.doglist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bikcodeh.dogrecognizer.databinding.ItemDogBinding
import com.bikcodeh.dogrecognizer.domain.model.Dog

class DogListAdapter(
    private val onItemClick: (Dog) -> Unit,
    private val onLongClick: (dogId: String) -> Unit
) : ListAdapter<Dog, DogListAdapter.DogViewHolder>(DiffCallback()) {

    private class DiffCallback : DiffUtil.ItemCallback<Dog>() {
        override fun areItemsTheSame(oldItem: Dog, newItem: Dog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Dog, newItem: Dog): Boolean {
            return oldItem == newItem
        }

    }

    inner class DogViewHolder(private val binding: ItemDogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dog: Dog) {
            binding.dogListItemLayout.setOnClickListener {
                onItemClick(dog)
            }
            binding.dogImage.load(dog.imageUrl)

            binding.dogListItemLayout.setOnLongClickListener {
                onLongClick(dog.id.toString())
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val binding = ItemDogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}