package com.example.scoreup.ui.fragments

import android.app.Dialog
import androidx.fragment.app.Fragment
import com.example.scoreup.R
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseFragment: Fragment() {
    private lateinit var mProgressDialog: Dialog

    fun showProgressDialog(text: String){
        mProgressDialog = Dialog(requireActivity())

        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        mProgressDialog.tv_dp_please_wait.text = text

        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
}