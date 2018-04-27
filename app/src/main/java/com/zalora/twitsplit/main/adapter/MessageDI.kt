package com.zalora.twitsplit.main.adapter

import com.zalora.twitsplit.TwitSplitComponent
import com.zalora.twitsplit.di.scope.ItemScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@Module(includes = [MessageAdapterModule.Declarations::class])
class MessageAdapterModule (private val iMessageView: MessageContract.IMessageView) {

    @Provides
    fun provideMessageView(): MessageContract.IMessageView {
        return iMessageView
    }

    @Module
    interface Declarations {

        @Binds
        fun bindingMessagePresenter(messagePresenter: MessagePresenter): MessageContract.IMessagePresenter
    }
}

@ItemScope
@Component(modules = [MessageAdapterModule::class], dependencies = [TwitSplitComponent::class])
interface MessageAdapterComponent {

    fun inject(messageAdapter: MessageAdapter)

}