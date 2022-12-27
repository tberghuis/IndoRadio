package dev.tberghuis.indoradio.util

import android.util.Log
import dev.tberghuis.indoradio.BuildConfig

fun logd(s: String) {
  if (BuildConfig.DEBUG) {
    Log.d("xxx", s)
  }
}