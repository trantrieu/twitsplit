package com.zalora.twitsplit.main

import com.zalora.twitsplit.base.IBasePresenter
import com.zalora.twitsplit.main.domain.MessageUseCase
import io.reactivex.functions.Consumer
import javax.inject.Inject

class MainPresenter @Inject constructor(private val iMainView: MainContract.IMainView): IBasePresenter(), MainContract.IMainPresenter {

    @Inject
    lateinit var messageUseCase: MessageUseCase

    override fun postData(message: String) {
        messageUseCase.execute(message, Consumer {list->
            iMainView.displayMessageList(list)
        }, Consumer {
            iMainView.displayError(it.message!!)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        messageUseCase.disposal()
    }
}