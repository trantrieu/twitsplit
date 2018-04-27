package com.zalora.twitsplit

import android.content.Context
import android.view.LayoutInflater
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module
class TwitSplitAppModule @Inject constructor(private val context: Context) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideLayoutInflater(): LayoutInflater {
        return context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

}

@Singleton
@Component(modules = [TwitSplitAppModule::class])
interface TwitSplitComponent {

    fun provideApplicationContext(): Context
    fun provideLayoutInflater(): LayoutInflater

}