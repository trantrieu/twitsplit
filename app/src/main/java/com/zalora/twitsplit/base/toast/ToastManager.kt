package com.zalora.twitsplit.base.toast

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.test.espresso.IdlingResource
import android.support.test.espresso.idling.CountingIdlingResource
import android.view.View
import android.widget.Toast

object ToastManager {

    private val idlingResource = CountingIdlingResource("ToastManager")

    @VisibleForTesting
    fun getIdlingResource(): IdlingResource {
        return idlingResource
    }

    var listener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
        }

        override fun onViewDetachedFromWindow(v: View) {
            if (!idlingResource.isIdleNow) {
                idlingResource.decrement()
            }
        }
    }
    private set

    fun increase() {
        idlingResource.increment()
    }
}

object ToastWrapper {

    @SuppressLint("ShowToast")
    fun makeText(context: Context, message: String, duration: Int): Toast {
        val t = Toast.makeText(context, message, duration)
        t.view.addOnAttachStateChangeListener(ToastManager.listener)
        return t
    }

}