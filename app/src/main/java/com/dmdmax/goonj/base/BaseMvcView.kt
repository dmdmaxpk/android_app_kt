package com.dmdmax.goonj.base

import android.view.View
import com.dmdmax.goonj.firebase.EventName
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster

interface BaseMvcView {
    fun getRootView(): View?
    fun getToaster(): Toaster.Companion
    fun getPrefs(): GoonjPrefs
    fun getLogger(): Logger.Companion
    fun getConstants(): Constants?
    fun getEventNames(): EventName?
}