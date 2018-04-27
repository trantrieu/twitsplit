package com.zalora.twitsplit.base

interface IPresenter {

    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()

}

open class IBasePresenter : IPresenter {

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onStop() {

    }

    override fun onDestroy() {

    }

}