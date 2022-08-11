package com.bikcodeh.dogrecognizer.dogspresentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.bikcodeh.dogrecognizer.core_model.Dog
import com.bikcodeh.dogrecognizer.dogspresentation.databinding.ItemDogBinding
import com.bikcodeh.dogrecognizer.core.R as RC

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
            binding.dogImage.load(dog.imageUrl) {
                crossfade(true)
                placeholder(RC.drawable.ic_image)
                error(RC.drawable.ic_broken_image)
                transformations(CircleCropTransformation())
            }

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