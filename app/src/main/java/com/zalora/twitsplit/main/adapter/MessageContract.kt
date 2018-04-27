package com.zalora.twitsplit.main.adapter

import com.zalora.twitsplit.base.IPresenter
import com.zalora.twitsplit.base.IView

interface MessageContract {

    interface IMessageView : IView {
        fun showMessage(messageVH: MessageVH, string: String)
    }

    interface IMessagePresenter: IPresenter {
        fun handleMessagePart(messageVH: MessageVH, message: String)
    }

}