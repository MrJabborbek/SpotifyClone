package com.fraggeil.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fraggeil.spotifyclone.data.entities.Song
import com.fraggeil.spotifyclone.databinding.ListItemBinding

abstract class BaseSongAdapter() : RecyclerView.Adapter<BaseSongAdapter.SongsViewHolder>() {

    inner class SongsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    protected val differCallback = object : DiffUtil.ItemCallback<Song>(){
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    protected abstract val differ : AsyncListDiffer<Song>
    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    protected var onItemClickListener : ((Song) -> Unit)? = null
    fun setItemClickListener(listener: (Song) ->Unit ){
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}