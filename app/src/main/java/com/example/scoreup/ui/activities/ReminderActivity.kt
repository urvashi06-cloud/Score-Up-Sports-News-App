package com.example.scoreup.ui.activities

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreup.R
import com.example.scoreup.adapters.ReminderAdapter
import com.example.scoreup.firestore.FirestoreClass
import com.example.scoreup.models.RemindMatch
import kotlinx.android.synthetic.main.activity_reminder.*

class ReminderActivity : BaseActivity() {

    private lateinit var mAdapter: ReminderAdapter
    private val mListRemindMatches: ArrayList<RemindMatch> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        setUpActionBar()

        rv_reminder_items.layoutManager = LinearLayoutManager(this)
        rv_reminder_items.setHasFixedSize(true)
        showProgressDialog(resources.getString(R.string.please_wait_text))
        FirestoreClass().getListOfRemindMatches(this)
    }

    private fun setUpActionBar() {
        setSupportActionBar(tb_reminder_back)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow)

        tb_reminder_back.setNavigationOnClickListener { onBackPressed() }
    }

    fun successGetListOfRemindMatches(listRemindMatches: ArrayList<RemindMatch>) {
        for (i in listRemindMatches) {
            mListRemindMatches.add(i)
        }
        if (mListRemindMatches.isNotEmpty()) {
            rv_reminder_items.visibility = View.VISIBLE
            tv_reminder_nothing_to_display_yet.visibility = View.GONE
            mAdapter = ReminderAdapter(this, mListRemindMatches, this)
            rv_reminder_items.adapter = mAdapter
            hideProgressDialog()
        } else {
            rv_reminder_items.visibility = View.GONE
            tv_reminder_nothing_to_display_yet.visibility = View.VISIBLE
            hideProgressDialog()
        }
    }

    fun getListOfRemindMatches() {
        showProgressDialog(resources.getString(R.string.please_wait_text))
        mAdapter.clear()
        FirestoreClass().getListOfRemindMatches(this)
    }
}