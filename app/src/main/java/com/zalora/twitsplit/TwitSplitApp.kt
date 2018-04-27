package com.zalora.twitsplit

import android.app.Application

class TwitSplitApp: Application() {

    private lateinit var twitSplitComponent: TwitSplitComponent

    override fun onCreate() {
        super.onCreate()
        twitSplitComponent = DaggerTwitSplitComponent.builder()
                .twitSplitAppModule(TwitSplitAppModule(this))
                .build()
    }

    fun getTwitSplitComponent(): TwitSplitComponent {
        return twitSplitComponent
    }
}