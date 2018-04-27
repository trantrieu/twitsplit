package com.zalora.twitsplit

import android.app.Application
import com.zalora.twitsplit.di.DaggerTwitSplitComponent
import com.zalora.twitsplit.di.TwitSplitComponent
import com.zalora.twitsplit.di.TwitSplitModule

class TwitSplitApp: Application() {

    private lateinit var twitSplitComponent: TwitSplitComponent

    override fun onCreate() {
        super.onCreate()
        twitSplitComponent = DaggerTwitSplitComponent.builder()
                .twitSplitModule(TwitSplitModule(this))
                .build()
    }

    fun getTwitSplitComponent(): TwitSplitComponent {
        return twitSplitComponent
    }
}