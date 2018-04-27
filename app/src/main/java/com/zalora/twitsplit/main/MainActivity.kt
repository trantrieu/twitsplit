package com.zalora.twitsplit.main

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.zalora.twitsplit.R
import com.zalora.twitsplit.base.BaseActivity
import com.zalora.twitsplit.main.adapter.MessageAdapter
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity<MainContract.IMainPresenter>(), MainContract.IMainView {

    @Inject
    override lateinit var presenter: MainContract.IMainPresenter

    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DaggerMainComponent.builder()
                .mainModule(MainModule(this))
                .twitSplitComponent(getTwitSplitApp().getTwitSplitComponent())
                .build()
                .inject(this)

        postBtn.setOnClickListener {
            postData()
        }

        input.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                postData()
            }

            return@setOnEditorActionListener false
        }

        messageAdapter = MessageAdapter(this)
        recyclerView.adapter = messageAdapter
    }

    private fun postData() {
        val message = input.text.toString()
        presenter.postData(message)
        input.setText("")
    }

    override fun displayMessageList(list: List<String>) {
        messageAdapter.setListPart(list)
    }

    override fun displayError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }
}
