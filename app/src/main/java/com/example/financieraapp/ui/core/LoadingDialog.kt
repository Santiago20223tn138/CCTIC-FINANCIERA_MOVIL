package com.example.financieraapp.ui.core

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.example.financieraapp.R

class LoadingDialog(private val activity: Activity) {

    private var dialog: AlertDialog? = null

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_progress_bar, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.show()
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }
}
