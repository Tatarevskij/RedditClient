package com.example.redditclient

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

const val ACCESS_TOKEN = "access_token"
const val REFRESH_TOKEN = "refresh_token"
const val LINKS_HISTORY = "links_history"

@HiltAndroidApp
class App: Application() {
}