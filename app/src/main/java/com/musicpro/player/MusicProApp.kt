package com.musicpro.player

import android.app.Application

class MusicProApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MusicProApp
            private set
    }
}
