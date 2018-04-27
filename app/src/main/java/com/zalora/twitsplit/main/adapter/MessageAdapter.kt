package com.zalora.twitsplit.main.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zalora.twitsplit.R
import com.zalora.twitsplit.TwitSplitApp
import javax.inject.Inject

class MessageAdapter (context: Context): RecyclerView.Adapter<MessageVH>(), MessageContract.IMessageView {

    @Inject
    lateinit var iMessagePresenter: MessageContract.IMessagePresenter

    @Inject
    lateinit var layoutInflater: LayoutInflater

    private var listPart = mutableListOf<String>()

    init {
        DaggerMessageAdapterComponent.builder()
                .messageAdapterModule(MessageAdapterModule(this))
                .twitSplitComponent((context.applicationContext as TwitSplitApp).getTwitSplitComponent())
                .build()
                .inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageVH {
        return MessageVH(layoutInflater.inflate(R.layout.item_message, parent, false))
    }

    override fun getItemCount(): Int = listPart.size


    override fun onBindViewHolder(holder: MessageVH, position: Int) {
        iMessagePresenter.handleMessagePart(holder, listPart[position])
    }

    fun setListPart(list: List<String>) {
        listPart.addAll(list)
        notifyDataSetChanged()
    }

    override fun showMessage(messageVH: MessageVH, string: String) {
        messageVH.displayMessage(string)
    }

}

/**
 * Make this open for testing
 */
open class MessageVH(view: View): RecyclerView.ViewHolder(view) {

    fun displayMessage(message: String) {
        itemView.findViewById<TextView>(R.id.messageTv).text = message
    }
}