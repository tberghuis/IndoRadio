package dev.tberghuis.indoradio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_PLAY_PAUSE
import androidx.media3.common.Player.COMMAND_PREPARE
import androidx.media3.common.Player.COMMAND_SET_MEDIA_ITEM
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dev.tberghuis.indoradio.home.HomeScreen
import dev.tberghuis.indoradio.ui.theme.IndoRadioTheme

class MainActivity : ComponentActivity() {

  private lateinit var controllerFuture: ListenableFuture<MediaController>
  private lateinit var controller: MediaController

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    log("onCreate MainActivity")
    setContent {
      IndoRadioTheme {

        Scaffold(
          modifier = Modifier,
          topBar = { TopAppBar(title = { Text("Suara Surabaya Radio") }) },


          ) { paddingValues ->

          Column(
            modifier = Modifier
              .padding(paddingValues)
              .fillMaxSize(),
            verticalArrangement = Arrangement.Center
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.Center
            ) {
              Button(onClick = {
                val url = "https://c5.siar.us/proxy/ssfm/stream"
                play(url)
              }) {
                Text(text = "Play")
              }

              Button(onClick = {
                playerStop()
              }) {
                Text(text = "Stop")
              }


            }
          }

        }
      }
    }
  }

  override fun onStart() {
    super.onStart()
    val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
    controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
    controllerFuture.addListener(
      {
        controller = controllerFuture.get()
        initController()
      }, MoreExecutors.directExecutor()
    )
  }

  override fun onStop() {
    MediaController.releaseFuture(controllerFuture)
    super.onStop()
  }

  private fun initController() {
    //controller.playWhenReady = true
    controller.addListener(object : Player.Listener {

      override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        log("onMediaMetadataChanged=$mediaMetadata")
      }

      override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        log("onIsPlayingChanged=$isPlaying")
      }

      override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        log("onPlaybackStateChanged=${getStateName(playbackState)}")
      }

      override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        log("onPlayerError=${error.stackTraceToString()}")
      }

      override fun onPlayerErrorChanged(error: PlaybackException?) {
        super.onPlayerErrorChanged(error)
        log("onPlayerErrorChanged=${error?.stackTraceToString()}")
      }
    })
    log("start=${getStateName(controller.playbackState)}")
    log("COMMAND_PREPARE=${controller.isCommandAvailable(COMMAND_PREPARE)}")
    log("COMMAND_SET_MEDIA_ITEM=${controller.isCommandAvailable(COMMAND_SET_MEDIA_ITEM)}")
    log("COMMAND_PLAY_PAUSE=${controller.isCommandAvailable(COMMAND_PLAY_PAUSE)}")
  }

  private fun play(url: String) {
    log("play($url)")
    log("before=${getStateName(controller.playbackState)}")

//    controller.setMediaItem(MediaItem.fromUri(url))

    val media = MediaItem.Builder().setMediaId(url).build()
    controller.setMediaItem(media)

    controller.prepare()
    controller.play()
    log("after=${getStateName(controller.playbackState)}")
  }


  private fun playerStop() {
    controller.stop()
  }


  private fun getStateName(i: Int): String? {
    return when (i) {
      1 -> "STATE_IDLE"
      2 -> "STATE_BUFFERING"
      3 -> "STATE_READY"
      4 -> "STATE_ENDED"
      else -> null
    }
  }

  private fun log(message: String) {
    Log.e("=====[TestMedia]=====", message)
  }
}