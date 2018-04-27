package com.zalora.twitsplit

import android.app.Activity
import android.content.pm.ActivityInfo

object Utils {

    fun rotateToLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun rotateToPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

}