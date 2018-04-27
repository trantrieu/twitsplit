package com.zalora.twitsplit.main

import com.zalora.twitsplit.base.IPresenter
import com.zalora.twitsplit.base.IView

interface MainContract {

    interface IMainPresenter: IPresenter {
        fun postData(message: String)
    }

    interface IMainView: IView {
        fun displayMessageList(list: List<String>)
        fun displayError(error: String)
    }

}