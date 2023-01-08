package com.fraggeil.spotifyclone.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.fraggeil.spotifyclone.R
import com.fraggeil.spotifyclone.adapters.SwipeSongAdapter
import com.fraggeil.spotifyclone.data.entities.Song
import com.fraggeil.spotifyclone.databinding.ActivityMainBinding
import com.fraggeil.spotifyclone.exoplayer.isPlaying
import com.fraggeil.spotifyclone.exoplayer.toSong
import com.fraggeil.spotifyclone.other.Status.*
import com.fraggeil.spotifyclone.ui.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //    private val mainViewModel =  MainViewModel by viewModels()
    private lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private var curPlayingSong: Song? = null
    private lateinit var binding: ActivityMainBinding

    private var playbackState: PlaybackStateCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeToObservers()

        binding.vpSong.adapter = swipeSongAdapter
        binding.vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (playbackState?.isPlaying == true){
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                }else{
                    curPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })
        binding.vpSong.setOnClickListener {
            binding.navHostFragment.findNavController().navigate(R.id.action_global_songFragment)
        }

        binding.navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.songFragment -> hideBottomBar()
                else -> showBottomBar()
            }
        }

        binding.ivPlayPause.setOnClickListener {
            if (playbackState?.isPlaying == true){
                curPlayingSong?.let { it ->
                    mainViewModel.playOrToggleSong(it, true) }
            }
        }
    }

    private fun hideBottomBar(){
        binding.apply {
            ivCurSongImage.isVisible = false
            ivPlayPause.isVisible = false
            vpSong.isVisible = false
        }
    }
    private fun showBottomBar(){
        binding.apply {
            ivCurSongImage.isVisible = true
            ivPlayPause.isVisible = true
            vpSong.isVisible = true
        }
    }

    private fun switchViewPagerToCurrentSong(song: Song) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if (newItemIndex != -1) {
            binding.vpSong.currentItem = newItemIndex
            curPlayingSong = song
        }
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(this) {
            it?.let { result ->
                when (result.status) {
                    SUCCESS -> {
                        result.data?.let { songs ->
                            swipeSongAdapter.songs = songs
                            if (songs.isNotEmpty()) {
                                glide.load((curPlayingSong ?: songs[0]).imageUrl)
                                    .into(binding.ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                    ERROR -> Unit
                    LOADING -> Unit
                }
            }
        }

        mainViewModel.curPlayingSong.observe(this) {
            if (it == null) return@observe

            curPlayingSong = it.toSong()
            glide.load(curPlayingSong?.imageUrl).into(binding.ivCurSongImage)
            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)

        }

        mainViewModel.playbackState.observe(this) {
            playbackState = it
            binding.ivPlayPause.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        mainViewModel.isConnected.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    ERROR -> {
                        Snackbar.make(
                            binding.rootLayout,
                            result.message ?: "An unknown error occurred",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
                    else -> Unit
                }
            }
        }

        mainViewModel.netWorkError.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    ERROR -> {
                        Snackbar.make(
                            binding.rootLayout,
                            result.message ?: "An unknown error occurred",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
                    else -> Unit
                }
            }
        }
    }

}