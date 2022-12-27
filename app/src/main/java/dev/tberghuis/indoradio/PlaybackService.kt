package dev.tberghuis.indoradio

import androidx.media3.common.MediaItem
import androidx.media3.session.MediaSession
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture


// Extend MediaSessionService
class PlaybackService : MediaSessionService(), MediaSession.Callback {
  private var mediaSession: MediaSession? = null

  // Create your Player and MediaSession in the onCreate lifecycle event
  override fun onCreate() {
    super.onCreate()
    val player = ExoPlayer.Builder(this).build()
    mediaSession = MediaSession.Builder(this, player).build()
  }

  // Return a MediaSession to link with the MediaController that is making
  // this request.
  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
    mediaSession


  override fun onDestroy() {
    mediaSession?.run {
      player.release()
      release()
      mediaSession = null
    }
    super.onDestroy()
  }


  override fun onAddMediaItems(
    mediaSession: MediaSession,
    controller: MediaSession.ControllerInfo,
    mediaItems: MutableList<MediaItem>
  ): ListenableFuture<MutableList<MediaItem>> {
    val updatedMediaItems =
      mediaItems.map { it.buildUpon().setUri(it.mediaId).build() }.toMutableList()
    return Futures.immediateFuture(updatedMediaItems)
  }

}