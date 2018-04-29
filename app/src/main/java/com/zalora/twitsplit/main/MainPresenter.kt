package com.zalora.twitsplit.main

import com.zalora.twitsplit.base.IBasePresenter
import com.zalora.twitsplit.domain.PostMessageUseCase
import io.reactivex.functions.Consumer
import javax.inject.Inject

class MainPresenter @Inject constructor(private val iMainView: MainContract.IMainView, private val postMessageUseCase: PostMessageUseCase): IBasePresenter(), MainContract.IMainPresenter {

    override fun postData(message: String) {
        postMessageUseCase.execute(message, Consumer { list->
            iMainView.displayMessageList(list)
        }, Consumer {
            iMainView.displayError(it.message!!)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        postMessageUseCase.disposal()
    }
}