package com.dmdmax.goonj.screens.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.dmdmax.goonj.R

class DialogManager {

    fun getNoNetworkDialog(context: Context): AlertDialog.Builder? {
        val builder = AlertDialog.Builder(context, R.style.MyAlertDialogTheme)
        builder.setTitle("Alert!")
        builder.setMessage("Weak or no internet connection. Please try again later")
        builder.setCancelable(false)
        return builder
    }

    interface LocationPermissionClickListener{
        fun onPositiveButtonClick();
        fun onNegativeButtonClick();
    }

    fun grantLocationPermission(context: Context, listener: LocationPermissionClickListener?) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
        view.findViewById<TextView>(R.id.title).text = "Location Permission"
        view.findViewById<TextView>(R.id.message).text = "${context.resources.getString(R.string.app_name)} wants location permission in order to show you the Namaz time. Would you like to give that permission?"

        val builder = AlertDialog.Builder(context, R.style.MyAlertDialogTheme)
        builder.setCancelable(false)
        builder.setView(view);
        builder.setNegativeButton("No", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                listener!!.onNegativeButtonClick();
            }
        });
        builder.setPositiveButton("Yes, sure", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                listener!!.onPositiveButtonClick();
            }
        });
        builder.show();
    }

    fun displayLocationOffDialog(context: Context, listener: LocationPermissionClickListener?) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
        view.findViewById<TextView>(R.id.title).text = "GPS Disabled"
        view.findViewById<TextView>(R.id.message).text = "GPS on your device is disabled, please click Switch On GPS button in order to switch it on."

        val builder = AlertDialog.Builder(context, R.style.MyAlertDialogTheme)
        builder.setCancelable(false);
        builder.setView(view);
        builder.setNegativeButton("No, thanks", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                listener!!.onNegativeButtonClick();
            }
        });
        builder.setPositiveButton("Switch On GPS", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                listener!!.onPositiveButtonClick();
            }
        });
        builder.show();
    }
}