package com.dmdmax.goonj.utility

import android.content.Context
import java.util.*

class DeviceInfo {
    companion object{
        private val PREFERENCES = "DeviceInfo.PREFERENCES"
        private val KEY_DEVICEID = "DeviceId"

        /**
         * @return unique identifier string for the device
         */
        @Synchronized
        fun getDeviceId(context: Context?): String? {
            val prefs = context!!.getSharedPreferences(PREFERENCES, 0)
            var value = prefs.getString(KEY_DEVICEID, null)
            if (value == null) {
                value = UUID.randomUUID().toString()
                prefs.edit().putString(KEY_DEVICEID, value).commit()
            }
            return value
        }
    }
}