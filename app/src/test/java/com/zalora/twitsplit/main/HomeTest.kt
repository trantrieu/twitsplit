package com.zalora.twitsplit.main

import android.content.Context
import com.zalora.twitsplit.rx.RxImmediateSchedulerRule
import com.zalora.twitsplit.di.DaggerMainComponentTest
import com.zalora.twitsplit.di.DaggerTwitSplitComponent
import com.zalora.twitsplit.di.TwitSplitModule
import com.zalora.twitsplit.main.domain.MessageUseCase.Companion.EXCEPTION_ERROR_INPUT_EMPTY
import com.zalora.twitsplit.main.domain.MessageUseCase.Companion.EXCEPTION_ERROR_INPUT_TOO_LONG
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import javax.inject.Inject

class HomeTest {

    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var iMainView: MainContract.IMainView

    @Inject
    lateinit var iMainPresenter: MainContract.IMainPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        DaggerMainComponentTest.builder()
                .twitSplitComponent(DaggerTwitSplitComponent.builder().twitSplitModule(TwitSplitModule(context)).build())
                .mainModule(MainModule(iMainView))
                .build()
                .inject(this)
    }

    @Test
    fun testCallingPostEmpty() {
        iMainPresenter.postData("")
        verify(iMainView).displayError(EXCEPTION_ERROR_INPUT_EMPTY.message!!)
    }

    @Test
    fun testCallingPostTooLong() {
        iMainPresenter.postData("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        verify(iMainView).displayError(EXCEPTION_ERROR_INPUT_TOO_LONG.message!!)
    }

    @Test
    fun testCallingPostPartTooLong() {
        iMainPresenter.postData("aaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        verify(iMainView).displayError(EXCEPTION_ERROR_INPUT_TOO_LONG.message!!)
    }

    @Test
    fun testCallingPostSuccessful() {

        iMainPresenter.postData("I can't believe Tweeter now supports chunking my messages, so I don't have to do it myself.")
        verify(iMainView).displayMessageList(listOf(
                "1/2 I can't believe Tweeter now supports chunking",
                "2/2 my messages, so I don't have to do it myself."
        ))
    }

}