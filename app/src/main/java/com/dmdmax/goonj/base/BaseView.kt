package com.dmdmax.goonj.base

import android.content.Context
import android.view.View
import com.dmdmax.goonj.firebase.EventName
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster

open class BaseView: BaseMvcView {
    private var mRootView: View? = null

    protected open fun setRootView(mRootView: View?) {
        this.mRootView = mRootView
    }
    protected open fun <T : View?> findViewById(id: Int): T {
        return getRootView()!!.findViewById(id)
    }

    protected open fun getContext(): Context {
        return getRootView()!!.context
    }

    protected open fun getString(resId: Int): String? {
        return getContext().getString(resId)
    }


    override fun getRootView(): View? {
        return this.mRootView;
    }

    override fun getToaster(): Toaster.Companion {
        return Toaster.Companion;
    }

    override fun getLogger(): Logger.Companion {
        return Logger.Companion;
    }

    override fun getConstants(): Constants? {
        return Constants();
    }

    override fun getEventNames(): EventName? {
        return EventName();
    }
}