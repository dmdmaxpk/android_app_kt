package com.dmdmax.goonj.utility

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

class Toaster {
    companion object {
        @SuppressLint("ShowToast")
        public fun printToast(mContext: Context?, message: String){
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }
}