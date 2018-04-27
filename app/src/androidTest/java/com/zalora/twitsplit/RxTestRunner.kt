package com.zalora.twitsplit

import android.support.test.runner.AndroidJUnitRunner
import com.squareup.rx2.idler.Rx2Idler
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by Apple on 1/17/18.
 */
class RxTestRunner : AndroidJUnitRunner() {
    override fun onStart() {
        RxJavaPlugins.setInitComputationSchedulerHandler(Rx2Idler.create("setInitComputationSchedulerHandler"))
        RxJavaPlugins.setInitIoSchedulerHandler(Rx2Idler.create("setInitIoSchedulerHandler"))
        RxJavaPlugins.setInitNewThreadSchedulerHandler(Rx2Idler.create("setInitNewThreadSchedulerHandler"))
        RxJavaPlugins.setInitSingleSchedulerHandler(Rx2Idler.create("setInitSingleSchedulerHandler"))
        super.onStart()
    }
}