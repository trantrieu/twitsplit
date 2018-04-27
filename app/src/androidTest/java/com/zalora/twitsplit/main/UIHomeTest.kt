package com.zalora.twitsplit.main

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.zalora.twitsplit.R
import com.zalora.twitsplit.Utils
import com.zalora.twitsplit.main.adapter.MessageAdapter
import com.zalora.twitsplit.main.adapter.MessageVH
import com.zalora.twitsplit.main.domain.MessageUseCase.Companion.EXCEPTION_ERROR_INPUT_EMPTY
import com.zalora.twitsplit.main.domain.MessageUseCase.Companion.EXCEPTION_ERROR_INPUT_TOO_LONG
import com.zalora.twitsplit.matcher.RecyclerViewMatcher.Companion.withRecyclerView
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UIHomeTest {

    @get:Rule
    var testRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testPostErrorEmpty() {
        onView(withId(R.id.input)).perform(typeText(""))
        onView(withId(R.id.postBtn)).perform(click())

        onView(withText(EXCEPTION_ERROR_INPUT_EMPTY.message)).inRoot(withDecorView(not(testRule.activity.window.decorView))).check(matches(isDisplayed()))
    }

    @Test
    fun testPostErrorString() {
        onView(withId(R.id.input)).perform(typeText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.postBtn)).perform(click())

        onView(withText(EXCEPTION_ERROR_INPUT_TOO_LONG.message)).inRoot(withDecorView(not(testRule.activity.window.decorView))).check(matches(isDisplayed()))
    }

    @Test
    fun testPostSuccessful() {
        onView(withId(R.id.input)).perform(typeText("aaaa"))
        onView(withId(R.id.postBtn)).perform(click())

        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("aaaa")))
    }

    @Test
    fun testPostMultipleMessage() {
        onView(withId(R.id.input)).perform(typeText("aaaa"))
        onView(withId(R.id.postBtn)).perform(click())

        onView(withId(R.id.input)).perform(typeText("aaaaa"))
        onView(withId(R.id.postBtn)).perform(click())

        onView(withId(R.id.input)).perform(typeText("aaaaaa aa"))
        onView(withId(R.id.postBtn)).perform(click())

        onView(withId(R.id.input)).perform(typeText("aaaa b b b"))
        onView(withId(R.id.postBtn)).perform(click())

        var position = 0
        val matcherRecyclerView = CoreMatchers.allOf(withId(R.id.recyclerView), isDisplayed())
        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("aaaa")))

        position++
        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("aaaaa")))

        position++
        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("aaaaaa aa")))

        position++
        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("aaaa b b b")))
    }

    @Test
    fun testPostSplit() {
        onView(withId(R.id.input)).perform(typeText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.postBtn)).perform(click())

        var position = 0
        val matcherRecyclerView = CoreMatchers.allOf(withId(R.id.recyclerView), isDisplayed())

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("1/3 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")))

        position++
        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("2/3 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")))

        position++
        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("3/3 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")))
    }

    @Test
    fun testPostSplitMultipleMessage() {
        var position = 0
        val matcherRecyclerView = CoreMatchers.allOf(withId(R.id.recyclerView), isDisplayed())

        onView(withId(R.id.input)).perform(typeText("aaaa"))
        onView(withId(R.id.postBtn)).perform(click())
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("aaaa")))

        position++
        onView(withId(R.id.input)).perform(typeText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.postBtn)).perform(click())

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("1/3 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")))

        position++
        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("2/3 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")))

        position++
        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("3/3 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")))
    }

    @Test
    fun testRotateAndPost() {
        val matcherRecyclerView = CoreMatchers.allOf(withId(R.id.recyclerView), isDisplayed())
        var position = 0
        /**
         * input
         */
        for (i in 0..6) {
            onView(withId(R.id.input)).perform(typeText("aaaa"))
            onView(withId(R.id.postBtn)).perform(click())

            onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
            onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("aaaa")))
            position++
        }

        /**
         * input
         */
        onView(withId(R.id.input)).perform(typeText("I can't believe Tweeter now supports chunking my messages, so I don't have to do it myself."))
        onView(withId(R.id.postBtn)).perform(click())

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("1/2 I can't believe Tweeter now supports chunking")))
        position++

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("2/2 my messages, so I don't have to do it myself.")))
        position++

        onView(withId(R.id.input)).perform(closeSoftKeyboard())
        Utils.rotateToLandscape(testRule.activity)

        /**
         * input
         */
        onView(withId(R.id.input)).perform(typeText("aaaaaaaa"))
        onView(withId(R.id.input)).perform(pressImeActionButton())
        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("aaaaaaaa")))
        position++

        /**
         * input
         */
        onView(withId(R.id.input)).perform(typeText("I can't believe Tweeter now supports chunking my messages, so I don't have to do it myself."))
        onView(withId(R.id.input)).perform(pressImeActionButton())

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("1/2 I can't believe Tweeter now supports chunking")))
        position++

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("2/2 my messages, so I don't have to do it myself.")))
        position++

        onView(withId(R.id.input)).perform(closeSoftKeyboard())
        Utils.rotateToPortrait(testRule.activity)
        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(0))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("aaaa")))
    }

    @Test
    fun testPostAll() {

        val matcherRecyclerView = CoreMatchers.allOf(withId(R.id.recyclerView), isDisplayed())

        onView(withId(R.id.input)).perform(typeText(""))
        onView(withId(R.id.postBtn)).perform(click())
        onView(withText(EXCEPTION_ERROR_INPUT_EMPTY.message)).inRoot(withDecorView(not(testRule.activity.window.decorView))).check(matches(isDisplayed()))

        onView(withId(R.id.input)).perform(typeText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.postBtn)).perform(click())
        onView(withText(EXCEPTION_ERROR_INPUT_TOO_LONG.message)).inRoot(withDecorView(not(testRule.activity.window.decorView))).check(matches(isDisplayed()))

        var position = 0
        /**
         * input
         */
        onView(withId(R.id.input)).perform(typeText("aaaa"))
        onView(withId(R.id.postBtn)).perform(click())

        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("aaaa")))
        position++

        /**
         * input
         */
        onView(withId(R.id.input)).perform(typeText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.postBtn)).perform(click())

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("1/3 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")))
        position++

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("2/3 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")))
        position++

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("3/3 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")))
        position++

        /**
         * input
         */
        onView(withId(R.id.input)).perform(typeText("I can't believe Tweeter now supports chunking my messages, so I don't have to do it myself."))
        onView(withId(R.id.postBtn)).perform(click())

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("1/2 I can't believe Tweeter now supports chunking")))
        position++

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("2/2 my messages, so I don't have to do it myself.")))
        position++

        /**
         * input
         */
        onView(withId(R.id.input)).perform(typeText("I can't believe Tweeter now supports chunking my messages, so I don't have to do it myself."))
        onView(withId(R.id.postBtn)).perform(click())

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("1/2 I can't believe Tweeter now supports chunking")))
        position++

        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(position))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(position, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("2/2 my messages, so I don't have to do it myself.")))
        position++

        /**
         * Scroll to top
         */
        onView(matcherRecyclerView).perform(RecyclerViewActions.scrollToPosition<MessageVH>(0))
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.messageTv, MessageAdapter::class.java)).check(matches(withText("aaaa")))
    }

}