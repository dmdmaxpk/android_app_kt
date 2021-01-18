package com.dmdmax.goonj.base

import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {
    private var mBaseController: BaseController? = null;

    protected open fun getCompositionRoot(): BaseController {
        if (mBaseController == null) {
            mBaseController = BaseController(activity!!);
        }
        return mBaseController!!;
    }
}