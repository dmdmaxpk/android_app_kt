package com.dmdmax.goonj.utility

import android.util.Log

class Logger {
    companion object {
        private const val tag = "new_goonj";
        public fun println(log : String){
            Log.d(tag, log);
        }
    }

    public fun println(log : String){
        Log.d(tag, log);
    }
}