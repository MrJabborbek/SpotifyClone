package com.fraggeil.spotifyclone.ui.fragmens

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.fraggeil.spotifyclone.R
import com.fraggeil.spotifyclone.data.entities.Song
import com.fraggeil.spotifyclone.databinding.FragmentSongBinding
import com.fraggeil.spotifyclone.exoplayer.isPlaying
import com.fraggeil.spotifyclone.exoplayer.toSong
import com.fraggeil.spotifyclone.other.Status
import com.fraggeil.spotifyclone.other.Status.SUCCESS
import com.fraggeil.spotifyclone.ui.viewmodels.MainViewModel
import com.fraggeil.spotifyclone.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
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

    private var playbackState: PlaybackStateCompat? = null
    private var shouldUpdateSeekbar = true

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

        binding.ivPlayPauseDetail.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        binding.ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }

        binding.ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    setCurrentPositionToTVCurTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    shouldUpdateSeekbar = true
                    mainViewModel.seekTo(it.progress.toLong())
                }
            }

        })
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

        mainViewModel.playbackState.observe(viewLifecycleOwner){
            playbackState = it
            binding.ivPlayPauseDetail.setImageResource(
                if (it?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
            binding.seekBar.progress = it?.position?.toInt() ?: 0
        }

        songViewModel.curPlayerPosition.observe(viewLifecycleOwner){
            if (shouldUpdateSeekbar) {
                setCurrentPositionToTVCurTime(it)
                binding.seekBar.progress = it.toInt()
            }
        }

        songViewModel.curSongDuration.observe(viewLifecycleOwner){
            binding.seekBar.max = it.toInt()
            val sdf = SimpleDateFormat("mm:ss", Locale.getDefault())
            binding.tvSongDuration.text = sdf.format(it)
        }
    }

    private fun setCurrentPositionToTVCurTime(ms: Long){
        val sdf = SimpleDateFormat("mm:ss", Locale.getDefault())
        binding.tvCurTime.text = sdf.format(ms)
    }

}