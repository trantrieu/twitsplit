package com.zalora.twitsplit.main

import com.zalora.twitsplit.di.TwitSplitComponent
import com.zalora.twitsplit.di.scope.ScreenScope
import com.zalora.twitsplit.main.domain.MessageUseCase
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@Module(includes = [MainModule.Declarations::class])
class MainModule (private val iMainView: MainContract.IMainView) {

    @Provides
    fun provideMessageUseCase(): MessageUseCase {
        return MessageUseCase()
    }

    @Provides
    fun provideIMainView(): MainContract.IMainView {
        return iMainView
    }

    @Module
    interface Declarations {

        @Binds
        fun bindMainPresenter(mainPresenter: MainPresenter): MainContract.IMainPresenter
    }

}

@ScreenScope
@Component (modules = [MainModule::class], dependencies = [TwitSplitComponent::class])
interface MainComponent {

    fun inject(mainActivity: MainActivity)

}