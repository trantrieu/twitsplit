package com.zalora.twitsplit.main.adapter

import com.zalora.twitsplit.base.IBasePresenter
import javax.inject.Inject

class MessagePresenter @Inject constructor(private val iMessageView: MessageContract.IMessageView): IBasePresenter(), MessageContract.IMessagePresenter {

    override fun handleMessagePart(messageVH: MessageVH, message: String) {
        iMessageView.showMessage(messageVH, message)
    }

}