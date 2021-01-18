package com.dmdmax.goonj.screens.dialogs

import android.app.AlertDialog
import android.content.Context
import com.dmdmax.goonj.R

class DialogManager {
    fun getNoNetworkDialog(context: Context?): AlertDialog.Builder? {
        val builder = AlertDialog.Builder(context, R.style.MyAlertDialogTheme)
        builder.setTitle("Alert!")
        builder.setMessage("Weak or no internet connection. Please try again later")
        builder.setCancelable(false)
        return builder
    }
}