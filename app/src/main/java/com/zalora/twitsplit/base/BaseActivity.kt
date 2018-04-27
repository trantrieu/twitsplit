package com.zalora.twitsplit.base

import android.support.v7.app.AppCompatActivity
import com.zalora.twitsplit.TwitSplitApp

abstract class BaseActivity<iPresenter: IPresenter> : AppCompatActivity(), IView {

    open lateinit var presenter: iPresenter

    protected fun getTwitSplitApp(): TwitSplitApp {
        return (application as? TwitSplitApp)!!
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

}