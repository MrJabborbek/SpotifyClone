package com.fraggeil.spotifyclone.exoplayer

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fraggeil.spotifyclone.other.Constants.NETWORK_ERROR
import com.fraggeil.spotifyclone.other.Event
import com.fraggeil.spotifyclone.other.Resource

class MusicPlayerConnection(
    context: Context
) {
    private var _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    private var isConnected : LiveData<Event<Resource<Boolean>>> = _isConnected

    private var _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    private var networkError: LiveData<Event<Resource<Boolean>>> = _networkError

    private var _networkState = MutableLiveData<PlaybackStateCompat?>()
    private var networkState : LiveData<PlaybackStateCompat?> = _networkState

    private var _curPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    private var curPlayingSong : LiveData<MediaMetadataCompat?> = _curPlayingSong

    lateinit var mediaController: MediaControllerCompat
    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        MediaBrowserConnectionCallback(context),
        null
    ).apply {
        connect()
    }

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: SubscriptionCallback){
        mediaBrowser.unsubscribe(parentId, callback)
    }

    private inner class MediaBrowserConnectionCallback(
        private val  context: Context
    ): MediaBrowserCompat.ConnectionCallback(){
        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            _isConnected.postValue(
                Event(
                    Resource.success(true)
                )
            )
        }

        override fun onConnectionSuspended() {
            _isConnected.postValue(
                Event(
                    Resource.error(
                        "The connection was suspended", false
                    )
                )
            )
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(
                Event(
                    Resource.error(
                        "Couldn't connect to media browser", false
                    )
                )
            )
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _networkState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _curPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when(event){
                NETWORK_ERROR -> {
                    _networkError.postValue(
                        Event(
                            Resource.error(
                                "Couldn't connect to the server. Please check your internet connection",
                                null
                            )
                        )
                    )
                }
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionFailed()
        }

    }
}