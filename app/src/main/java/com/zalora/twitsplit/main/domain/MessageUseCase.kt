package com.zalora.twitsplit.main.domain

import com.zalora.twitsplit.base.usecase.UseCase
import com.zalora.twitsplit.data.DataException
import com.zalora.twitsplit.data.model.Part
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.schedulers.Schedulers

class MessageUseCase: UseCase<List<String>, String>() {

    companion object {
        val EXCEPTION_ERROR_INPUT_TOO_LONG = DataException("Your string is too long and doesn't contain any spaces")
        val EXCEPTION_ERROR_INPUT_EMPTY = DataException("Your string is empty")
        const val LIMIT = 50
        const val UN_KNOW = -1
    }

    override fun buildCommand(param: String): Single<List<String>> {
        return getSplitPart(param).toObservable().flatMap {
            return@flatMap Observable.fromIterable(it)
        }.map {
            return@map it.getFullMessage()
        }.toList().subscribeOn(Schedulers.io())
    }

    private fun getSplitPart(strInput: String): Single<List<Part>> {
        return object: Single<List<Part>>() {
            override fun subscribeActual(observer: SingleObserver<in List<Part>>) {
                try {
                    //https://stackoverflow.com/questions/37070352/how-do-i-replace-duplicate-whitespaces-in-a-string-in-kotlin?rq=1
                    val str = strInput.trim().replace("\\s+".toRegex(), " ")
                    var listPart = mutableListOf<Part>()
                    if (splitToArray(str, listPart)) {
                        listPart = addIndexAndSize(listPart)

                        while (!checkIndexAndLength(listPart)) {
                            checkPartLength(listPart)
                        }

                    }
                    observer.onSuccess(listPart)
                } catch (e: Exception) {
                    observer.onError(e)
                }
            }
        }.subscribeOn(Schedulers.io())
    }

    /**
     * Check index and size values are correct or not
     */
    private fun checkIndexAndLength(list: MutableList<Part>): Boolean {

        list.forEachIndexed { i, part ->
            if (part.getIndex() == UN_KNOW) {
                return false
            }

            if (part.getSize() == UN_KNOW) {
                return false
            }

            if (part.getSize() != list.size) {
                return false
            }

            if (part.getIndex() != i) {
                return false
            }

            if (part.getFullMessageLength() > LIMIT) {
                return false
            }
        }

        return true
    }

    /**
     * Init index and size for list
     */
    private fun addIndexAndSize(listOrigin: MutableList<Part>): MutableList<Part> {
        val list = mutableListOf<Part>()
        listOrigin.forEachIndexed { i, part ->
            val newPart = Part(i, listOrigin.size, part.getSplits())
            list.add(newPart)
        }
        return list
    }

    private fun checkPartLength(list: MutableList<Part>) {
        //Handle on list

        var index = 0
        var listStringEnd: List<String>? = null
        val size = list.size
        while (index < list.size) {
            var part = list[index]
            listStringEnd?.run {
                //Edit the next if list string end no null
                val splits = part.getSplits().toMutableList()
                listStringEnd!!.forEach {
                    splits.add(0, it)
                }
                list.removeAt(index)
                list.add(index, Part(index, size, splits))
            }
            listStringEnd = null

            part = list[index]
            if (part.getFullMessageLength() > LIMIT) {
                //Edit the current if list string end no null
                listStringEnd = removePieceUtilSuccessful(part)
                val splits = part.getSplits().toMutableList()
                var count = listStringEnd.lastIndex
                while (count >= 0) {
                    splits.removeAt(splits.lastIndex)
                    count--
                }

                list.removeAt(index)
                list.add(index, Part(index, size, splits))
            }

            //Check size
            if (part.getSize() != list.size) {
                val old = list.removeAt(index)
                list.add(index, Part(index, size, old.getSplits()))
            }

            index ++
        }

        //Handle the part du
        if (listStringEnd != null) {
            list.add(Part(list.size, list.size, listStringEnd))
        }

        System.out.println()
    }

    /**
     * Remove the last index, until the remain length is smaller than limit
     */
    private fun removePieceUtilSuccessful(part: Part): List<String> {
        val list = mutableListOf<String>()
        val splits = part.getSplits()
        var index = splits.lastIndex
        var endLength = 0

        if (splits.size == 1) {
            if (!splits[0].contains(" ")) {
                if (splits[0].length + part.getPrefix().length > LIMIT) {
                    throw EXCEPTION_ERROR_INPUT_TOO_LONG
                }
            }
        }

        while (index >= 0) {
            endLength += splits[index].length
            val remainLength = part.getFullMessageLength() - endLength
            list.add(0, splits[index])

            if (remainLength <= LIMIT) {
                return list
            }

            //For space
            endLength += 1
            if (remainLength - 1 <= LIMIT) {
                return list
            }

            index--
        }
        return list
    }

    /**
     * Split message to each item, each item:
     * - contains only message
     * - not contains x/y
     * - message length is <= LIMIT
     */
    private fun splitToArray(message: String, list: MutableList<Part>): Boolean{

        if (message.isEmpty()) {
            throw EXCEPTION_ERROR_INPUT_EMPTY
        }

        if (message.length <= LIMIT) {
            list.add(Part(splits = listOf(message)))
            return false
        }

        val splits = message.split(" ")
        if (!splits.isEmpty()) {
            splits.forEach {
                if (it.length > LIMIT && !it.contains(" ")) {
                    throw EXCEPTION_ERROR_INPUT_TOO_LONG
                }
            }
        }

        val builder = StringBuffer()
        val listString = mutableListOf<String>()
        var i = 0
        while (i < splits.size) {
            builder.append(splits[i])
            if (i == splits.size - 1) {
                builder.deleteCharAt(builder.lastIndex)
            }
            listString.add(splits[i])

            if (builder.length >= LIMIT) {
                listString.removeAt(listString.lastIndex)
                val part = Part(splits = listOf(*listString.toTypedArray()))
                list.add(part)

                builder.delete(0, builder.length)
                listString.clear()
                i--
            }

            if (i == splits.size - 1) {
                val part = Part(splits = listOf(*listString.toTypedArray()))
                list.add(part)

                builder.delete(0, builder.length)
                listString.clear()
            }
            if (!builder.isEmpty()) {
                builder.append(" ")
            }
            i++
        }
        return true
    }

}