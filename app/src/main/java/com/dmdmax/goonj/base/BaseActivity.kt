package com.dmdmax.goonj.base

import androidx.fragment.app.FragmentActivity

open class BaseActivity: FragmentActivity() {

    private var mBaseController: BaseController? = null;

    protected open fun getCompositionRoot(): BaseController {
        if (mBaseController == null) {
            mBaseController = BaseController(this)
        }
        return mBaseController!!;
    }
}