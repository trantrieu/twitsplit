package com.zalora.twitsplit.base.usecase

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

abstract class UseCase<Result, Param> {

    private val compositeDisposable = CompositeDisposable()

    protected abstract fun buildCommand(param: Param): Single<Result>

    fun execute(param: Param, successConsumer: Consumer<Result>, failConsumer: Consumer<Throwable>? = null) {
        val single = buildCommand(param).observeOn(AndroidSchedulers.mainThread())
        compositeDisposable.add(
            if (failConsumer == null) {
                single.subscribe(successConsumer)
            } else {
                single.subscribe(successConsumer, failConsumer)
            }
        )
    }

    fun disposal() {
        compositeDisposable.dispose()
    }
}