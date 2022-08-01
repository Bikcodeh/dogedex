package com.bikcodeh.dogrecognizer.presentation.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bikcodeh.dogrecognizer.databinding.ItemFavoriteDogBinding
import com.bikcodeh.dogrecognizer.domain.model.Dog

class FavoriteAdapter : ListAdapter<Dog, FavoriteAdapter.FavoriteViewHolder>(FavoriteDiffUtil()) {

    private class FavoriteDiffUtil : DiffUtil.ItemCallback<Dog>() {
        override fun areItemsTheSame(oldItem: Dog, newItem: Dog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Dog, newItem: Dog): Boolean {
            return oldItem == newItem
        }
    }

    inner class FavoriteViewHolder(private val binding: ItemFavoriteDogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dog: Dog) {
            with(binding) {
                itemFavoriteIv.load(dog.imageUrl)
                itemFavoriteNameTv.text = dog.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder(
            ItemFavoriteDogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}