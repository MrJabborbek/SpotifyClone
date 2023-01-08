package com.fraggeil.spotifyclone.ui.fragmens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.fraggeil.spotifyclone.R
import com.fraggeil.spotifyclone.data.entities.Song
import com.fraggeil.spotifyclone.databinding.FragmentSongBinding
import com.fraggeil.spotifyclone.exoplayer.toSong
import com.fraggeil.spotifyclone.other.Status
import com.fraggeil.spotifyclone.other.Status.SUCCESS
import com.fraggeil.spotifyclone.ui.viewmodels.MainViewModel
import com.fraggeil.spotifyclone.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    private lateinit var binding: FragmentSongBinding

    @Inject
    lateinit var glide : RequestManager

    // farqi mainViewModel activityLifeciyclega ulanadi, songViewModel esa fragmentLifecyclega, shuning uchun ikki xil usulda e'lon qilinmoqda
    private lateinit var mainViewModel: MainViewModel
    private  val songViewModel: SongViewModel by viewModels()

    private var curPlayingSong: Song? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToObservers()
    }

    private fun updateTitleAndSongImage(song: Song){
        val title = "${song.title} - ${song.subtitle}"
        binding.tvSongName.text = title
        glide.load(song.imageUrl).into(binding.ivSongImage)
    }

    private fun subscribeToObservers(){
        mainViewModel.mediaItems.observe(viewLifecycleOwner){
            it?.let { result->
                when(result.status){
                    SUCCESS -> {
                        result.data?.let { songs ->
                            if (curPlayingSong == null && songs.isNotEmpty()){
                                curPlayingSong = songs[0]
                                updateTitleAndSongImage(songs[0])
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }

        mainViewModel.curPlayingSong.observe(viewLifecycleOwner){
            if(it == null) return@observe
            curPlayingSong = it.toSong()
            updateTitleAndSongImage(curPlayingSong!!)
        }
    }
}