package com.fraggeil.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.fraggeil.spotifyclone.databinding.ListItemBinding
import javax.inject.Inject

class SongsAdapter @Inject constructor(
    private val glide : RequestManager
): BaseSongAdapter() {
    private lateinit var binding: ListItemBinding

    override val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseSongAdapter.SongsViewHolder {
        binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongsViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: BaseSongAdapter.SongsViewHolder, position: Int) {
        val song = songs[position]
        binding.apply {
            tvPrimary.text = song.title
            tvSecondary.text = song.subtitle
            glide.load(song.imageUrl).into(ivItemImage)
            root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }
}