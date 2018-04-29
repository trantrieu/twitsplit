package com.zalora.twitsplit.business

import android.content.Context
import com.zalora.twitsplit.DaggerTwitSplitComponent
import com.zalora.twitsplit.TwitSplitAppModule
import com.zalora.twitsplit.di.DaggerMainComponentTest
import com.zalora.twitsplit.domain.PostMessageUseCase
import com.zalora.twitsplit.domain.PostMessageUseCase.Companion.EXCEPTION_ERROR_INPUT_TOO_LONG
import com.zalora.twitsplit.domain.PostMessageUseCase.Companion.LIMIT
import com.zalora.twitsplit.main.MainContract
import com.zalora.twitsplit.main.MainModule
import com.zalora.twitsplit.rx.RxImmediateSchedulerRule
import io.reactivex.functions.Consumer
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import javax.inject.Inject

class BusinessTest {

    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Inject
    lateinit var postMessageUseCase: PostMessageUseCase

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var iMainView: MainContract.IMainView

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        DaggerMainComponentTest.builder()
                .twitSplitComponent(DaggerTwitSplitComponent.builder().twitSplitAppModule(TwitSplitAppModule(context)).build())
                .mainModule(MainModule(iMainView))
                .build()
                .inject(this)
    }

    private fun splitTestCase(str: String, expectedResult: List<String>? = null, expectedException: Exception? = null) {
        postMessageUseCase.execute(str, Consumer { listPart ->

            /**
             * Check the size of expected list and result list
             */
            Assert.assertEquals(expectedResult!!.size, listPart.size)

            expectedResult.forEachIndexed { i, s ->
                /**
                 * Check each part length is smaller than LIMIT
                 */
                Assert.assertTrue(listPart[i].length <= LIMIT)

                /**
                 * Check each part string is same as expected part
                 */
                Assert.assertEquals(s, listPart[i])
            }
        }, Consumer { exception ->
            Assert.assertEquals(expectedException!!.message, exception.message)
        })
    }

    @Test
    fun testAssignmentCase() {
        splitTestCase(
                "I can't believe Tweeter now supports chunking my messages, so I don't have to do it myself.",
                listOf(
                        "1/2 I can't believe Tweeter now supports chunking",
                        "2/2 my messages, so I don't have to do it myself."
                )
        )
    }

    @Test
    fun testPostEmpty() {
        splitTestCase("", expectedException = PostMessageUseCase.EXCEPTION_ERROR_INPUT_EMPTY)
    }

    @Test
    fun testPostTooLong() {
        splitTestCase(
                "aaa, aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                expectedException = EXCEPTION_ERROR_INPUT_TOO_LONG
        )

        /**
         * the last piece length = 50 and contain no space
         */
        splitTestCase(
                "aaa, aaa aaa aaa aaa aaa aaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                expectedException = EXCEPTION_ERROR_INPUT_TOO_LONG
        )

        /**
         * input contains many pieces length = 48
         */
        splitTestCase(
                "aaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                expectedException = EXCEPTION_ERROR_INPUT_TOO_LONG
        )
    }

    @Test
    fun testPostSingle() {
        /**
         * small string
         */
        splitTestCase(
                "aaaa",
                listOf("aaaa")
        )

        /**
         * some small strings
         */
        splitTestCase(
                "aaaa, bbbb   dddd  eeee",
                listOf("aaaa, bbbb dddd eeee")
        )

        /**
         * the last piece length = 46 and contain no space
         */
        splitTestCase(
                "b aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                listOf("b aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        )

        /**
         * the middle piece length = 46 and contain no space
         */
        splitTestCase(
                "b aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa A",
                listOf("b aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa A")
        )

        /**
         * string length = 50
         */
        splitTestCase("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", listOf(
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        ))
    }

    @Test
    fun testSpecialCharacter() {
        splitTestCase("!@#$%#$%$#%$#%$#%", listOf(
                "!@#$%#$%$#%$#%$#%"
        ))

        splitTestCase("#aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa11", listOf(
                "#aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa11"
        ))
    }

    @Test
    fun testPostSplit() {

        /**
         * input contains 3 space
         * the middle piece length = 46
         */
        splitTestCase(
                "bbbbb aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa A",
                listOf(
                        "1/3 bbbbb",
                        "2/3 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "3/3 A"
                )
        )

        /**
         * contains larger space
         */
        splitTestCase(
                "bbbbb aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa A          aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                listOf(
                        "1/4 bbbbb",
                        "2/4 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "3/4 A",
                        "4/4 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                )
        )

        splitTestCase(
                "aaa, aaa aaa aaa aaa aaa aaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                listOf(
                        "1/2 aaa, aaa aaa aaa aaa aaa aaa",
                        "2/2 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                )
        )

        splitTestCase(
                "I can't believe Tweeter now supports chunking my messages, so I don't have to do it myself myself.",
                listOf(
                        "1/3 I can't believe Tweeter now supports chunking",
                        "2/3 my messages, so I don't have to do it myself",
                        "3/3 myself."
                ))

        splitTestCase(
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa          aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa       " +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                listOf(
                        "1/12 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "2/12 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "3/12 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "4/12 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "5/12 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "6/12 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "7/12 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "8/12 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "9/12 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "10/12 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "11/12 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "12/12 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                )
        )

        /**
         * each piece length = 22 and can combine in group of two
         */
        splitTestCase(
                "aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa",
                listOf(
                        "1/6 aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa",
                        "2/6 aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa",
                        "3/6 aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa",
                        "4/6 aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa",
                        "5/6 aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa",
                        "6/6 aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa"
                )
        )

        /**
         * each piece length = 23 and cannot combine
         */
        splitTestCase(
                "aaaaaaaaaaaaaaaaaaaaaaa         aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa",
                listOf(
                        "1/12 aaaaaaaaaaaaaaaaaaaaaaa",
                        "2/12 aaaaaaaaaaaaaaaaaaaaaaa",
                        "3/12 aaaaaaaaaaaaaaaaaaaaaaa",
                        "4/12 aaaaaaaaaaaaaaaaaaaaaaa",
                        "5/12 aaaaaaaaaaaaaaaaaaaaaaa",
                        "6/12 aaaaaaaaaaaaaaaaaaaaaaa",
                        "7/12 aaaaaaaaaaaaaaaaaaaaaaa",
                        "8/12 aaaaaaaaaaaaaaaaaaaaaaa",
                        "9/12 aaaaaaaaaaaaaaaaaaaaaaa",
                        "10/12 aaaaaaaaaaaaaaaaaaaaaaa",
                        "11/12 aaaaaaaaaaaaaaaaaaaaaaa",
                        "12/12 aaaaaaaaaaaaaaaaaaaaaaa"
                )
        )

        /**
         * evolve the number from x/y to ab/cd
         */
        splitTestCase(
                "aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa " +
                        "aaaa aaaa aaaa aaaa    aaaa aaaa aaaa aaaa aaaa " +
                        "aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa " +
                        "aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa " +
                        "aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa " +
                        "aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa " +
                        "aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa " +
                        "aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa " +
                        "aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa " +
                        "aaaa aaaa aaaa aaaa       aaaa aaaa aaaa aaaa aaaa " +
                        "aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa " +
                        "a",
                listOf(
                        "1/12 aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa",
                        "2/12 aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa",
                        "3/12 aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa",
                        "4/12 aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa",
                        "5/12 aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa",
                        "6/12 aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa",
                        "7/12 aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa",
                        "8/12 aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa",
                        "9/12 aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa",
                        "10/12 aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa",
                        "11/12 aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa",
                        "12/12 a"

                )
        )

    }

}