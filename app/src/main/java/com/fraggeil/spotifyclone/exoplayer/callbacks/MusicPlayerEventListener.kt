package com.fraggeil.spotifyclone.exoplayer.callbacks

import android.app.Service
import android.media.session.PlaybackState
import android.os.Build
import android.widget.Toast
import com.fraggeil.spotifyclone.exoplayer.MusicService
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_READY

class MusicPlayerEventListener(
    private val musicService: MusicService

) : Player.Listener{
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if (playbackState == STATE_READY && !playWhenReady){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                musicService.stopForeground(Service.STOP_FOREGROUND_DETACH)
            }else{
                musicService.stopForeground(false)
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "An error occurred", Toast.LENGTH_SHORT).show()
    }
}