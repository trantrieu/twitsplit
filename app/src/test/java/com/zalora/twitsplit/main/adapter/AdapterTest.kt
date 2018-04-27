package com.zalora.twitsplit.main.adapter

import android.content.Context
import com.zalora.twitsplit.rx.RxImmediateSchedulerRule
import com.zalora.twitsplit.di.DaggerMessageAdapterComponentTest
import com.zalora.twitsplit.di.DaggerTwitSplitComponent
import com.zalora.twitsplit.di.TwitSplitModule
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import javax.inject.Inject

class AdapterTest {

    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var iMessageView: MessageContract.IMessageView

    @Inject
    lateinit var iMessagePresenter: MessageContract.IMessagePresenter

    @Mock
    lateinit var vh: MessageVH

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        DaggerMessageAdapterComponentTest.builder()
                .twitSplitComponent(DaggerTwitSplitComponent.builder().twitSplitModule(TwitSplitModule(context)).build())
                .messageAdapterModule(MessageAdapterModule(iMessageView))
                .build()
                .inject(this)
    }

    @Test
    fun testDisplayMessage() {
        iMessagePresenter.handleMessagePart(vh, "test")
        verify(iMessageView).showMessage(vh, "test")

        iMessagePresenter.handleMessagePart(vh, "test123")
        verify(iMessageView).showMessage(vh, "test123")
    }
}