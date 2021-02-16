package com.dmdmax.goonj.screens.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
        val builder = AlertDialog.Builder(context, R.style.MyAlertDialogTheme)
        builder.setTitle("Location Permission!")
        builder.setMessage("${context.resources.getString(R.string.app_name)} wants location permission in order to show you the Namaz time. Would you like to give that permission?");
        builder.setCancelable(false)
        builder.setNegativeButton("No", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                listener!!.onNegativeButtonClick();
            }
        });
        builder.setPositiveButton("Yes, sure", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                listener!!.onPositiveButtonClick();
            }
        });
        builder.show();
    }

    fun displayLocationOffDialog(context: Context, listener: LocationPermissionClickListener?) {
        val builder = AlertDialog.Builder(context, R.style.MyAlertDialogTheme)
        builder.setTitle("Location")
        builder.setMessage("GPS is not enabled, please click below to switch your GPS on");
        builder.setCancelable(false)
        builder.setNegativeButton("No", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                listener!!.onNegativeButtonClick();
            }
        });
        builder.setPositiveButton("Sure", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                listener!!.onPositiveButtonClick();
            }
        });
        builder.show();
    }
}