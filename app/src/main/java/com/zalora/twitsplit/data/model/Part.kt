package com.zalora.twitsplit.data.model

import com.zalora.twitsplit.domain.PostMessageUseCase.Companion.UN_KNOW


data class Part(private val index: Int = UN_KNOW, private val size: Int = UN_KNOW, private val splits: List<String>) {

    private var holderMessage: String? = null

    private fun getMessage(): String {
        if (holderMessage == null) {
            val builder = StringBuffer()
            splits.forEach {
                builder.append(it).append(" ")
            }
            builder.deleteCharAt(builder.lastIndex)
            holderMessage = builder.toString()
        }
        return holderMessage!!
    }

    fun getIndex() = index
    fun getSize() = size

    //Return a new list instead itself
    fun getSplits(): List<String> {
        return listOf(*splits.toTypedArray())
    }

    fun getFullMessage(): String {
        return if (index == UN_KNOW || size == UN_KNOW) {
            getMessage()
        } else {
            getPrefix() + " " + getMessage()
        }
    }

    fun getFullMessageLength(): Int = getFullMessage().length

    fun getPrefix(): String {
        if (index == UN_KNOW || size == UN_KNOW) {
            throw IllegalStateException("both index and size are -1")
        }
        val indexDisplay = index + 1
        return "$indexDisplay/$size"
    }
}