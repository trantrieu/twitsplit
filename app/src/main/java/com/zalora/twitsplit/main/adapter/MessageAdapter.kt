package com.zalora.twitsplit.main.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zalora.twitsplit.R
import javax.inject.Inject

class MessageAdapter @Inject constructor(private val layoutInflater: LayoutInflater): RecyclerView.Adapter<MessageAdapter.MessageVH>() {

    private var listPart = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageVH {
        return MessageVH(layoutInflater.inflate(R.layout.item_message, parent, false))
    }

    override fun getItemCount(): Int = listPart.size

    override fun onBindViewHolder(holder: MessageVH, position: Int) {
        holder.displayMessage(listPart[position])
    }

    fun addMore(list: List<String>) {
        val previousSize = listPart.size
        listPart.addAll(list)
        notifyItemInserted(previousSize)
    }

    inner class MessageVH(view: View): RecyclerView.ViewHolder(view) {

        fun displayMessage(message: String) {
            itemView.findViewById<TextView>(R.id.messageTv).text = message
        }
    }

}