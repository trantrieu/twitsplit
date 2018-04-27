package com.zalora.twitsplit.di

import android.content.Context
import android.view.LayoutInflater
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [TwitSplitModule::class])
interface TwitSplitComponent {

    fun provideApplicationContext(): Context
    fun provideLayoutInflater(): LayoutInflater

}