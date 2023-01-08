package com.fraggeil.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.fraggeil.spotifyclone.databinding.ListItemBinding
import com.fraggeil.spotifyclone.databinding.SwipeItemBinding
import javax.inject.Inject

class SwipeSongAdapter() : BaseSongAdapter() {
    private lateinit var binding: SwipeItemBinding

    override val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseSongAdapter.SongsViewHolder {
        binding = SwipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongsViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: BaseSongAdapter.SongsViewHolder, position: Int) {
        val song = songs[position]
        binding.apply {
            val text = "${song.title} - ${song.subtitle}"
            tvPrimary.text = text

            root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }
}