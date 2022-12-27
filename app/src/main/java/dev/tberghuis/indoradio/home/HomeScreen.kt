package dev.tberghuis.indoradio.home

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView

@Composable
fun HomeScreen() {
  val context = LocalContext.current
  val fakeVm = FakeVm(context.applicationContext as Application)

  Column {
    Text("hello home screen")
    PlayerWrapper(fakeVm)
  }
}

class FakeVm(app: Application) {
  val player = ExoPlayer.Builder(app).build()
}


@Composable
fun PlayerWrapper(fakeVm: FakeVm) {
  AndroidView(
    factory = {
      PlayerView(it).apply {
        player = fakeVm.player.apply {
          val mediaItem =
            MediaItem.Builder().setUri("https://c5.siar.us/proxy/ssfm/stream")
              .setMimeType(MimeTypes.AUDIO_AAC)
              .build()
          setMediaItem(mediaItem)
          prepare()
        }

      }
    },
    modifier = Modifier,
    update = {}
  )
}