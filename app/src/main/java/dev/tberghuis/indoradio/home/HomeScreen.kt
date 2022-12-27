package dev.tberghuis.indoradio.home

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.media.session.MediaSession
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.google.common.util.concurrent.MoreExecutors
import dev.tberghuis.indoradio.PlaybackService
import dev.tberghuis.indoradio.util.logd

@Composable
fun HomeScreen() {
  val context = LocalContext.current
  val mediaController = remember { mutableStateOf<MediaController?>(null) }

  // doitwrong
  LaunchedEffect(Unit) {
    val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
    val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
    controllerFuture.addListener(
      { mediaController.value = controllerFuture.get() }, MoreExecutors.directExecutor()
    )
  }



  Column {
    Text("hello home screen")
    if (mediaController.value != null) {
      PlayerWrapper(mediaController.value!!)
    }
  }
}


@Composable
fun PlayerWrapper(mediaController: MediaController) {
  LaunchedEffect(Unit) {
    logd("player wrapper")
  }


  AndroidView(factory = {
    PlayerView(it).apply {

      player = mediaController.apply {

//        val media = MediaItem.Builder().setMediaId("https://c5.siar.us/proxy/ssfm/stream").build()
//        setMediaItem(media)


        val mediaItem = MediaItem.Builder().setUri("https://c5.siar.us/proxy/ssfm/stream")
          .setMimeType(MimeTypes.AUDIO_AAC).build()
        setMediaItem(mediaItem)
        prepare()
        this.play()
      }

    }
  }, modifier = Modifier, update = {})
}