package com.zalora.twitsplit.rx

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


/**
 * Created by Apple on 12/27/17.
 */
class RxImmediateSchedulerRule : TestRule {
    private val immediate = object : Scheduler() {
        override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable =// this prevents StackOverflowErrors when scheduling with a delay
                super.scheduleDirect(run, 0, unit)

        override fun createWorker(): Scheduler.Worker = ExecutorScheduler.ExecutorWorker(Executor { it.run() })
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediate }
                RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> immediate }
                RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediate }
                RxJavaPlugins.setInitSingleSchedulerHandler { scheduler -> immediate }
                RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediate }

                try {
                    base.evaluate()
                } finally {
                    RxJavaPlugins.reset()
                    RxAndroidPlugins.reset()
                }
            }
        }
    }
}