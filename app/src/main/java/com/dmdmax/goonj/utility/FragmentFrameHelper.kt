package com.dmdmax.goonj.utility

import android.app.Activity
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.dmdmax.goonj.R

class FragmentFrameHelper {
    private var mActivity: Activity? = null
    private var mFragmentManager: FragmentManager? = null

    constructor(activity: Activity?, fragmentManager: FragmentManager?) {
        this.mActivity = activity
        this.mFragmentManager = fragmentManager
    }

    fun replaceFragment(newFragment: Fragment) {
        replaceFragment(newFragment, true, false)
    }

    fun replaceFragmentDontAddToBackstack(newFragment: Fragment) {
        replaceFragment(newFragment, false, false)
    }

    fun replaceFragmentAndClearBackstack(newFragment: Fragment) {
        replaceFragment(newFragment, false, true)
    }


    private fun replaceFragment(newFragment: Fragment, addToBackStack: Boolean, clearBackStack: Boolean) {
        if (clearBackStack) {
            if (mFragmentManager!!.isStateSaved) {
                // If the state is saved we can't clear the back stack. Simply not doing this, but
                // still replacing fragment is a bad idea. Therefore we abort the entire operation.
                return
            }
            // Remove all entries from back stack
            mFragmentManager!!.popBackStackImmediate("null", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        val ft = mFragmentManager!!.beginTransaction()
        if (addToBackStack) {
            ft.addToBackStack(null)
        }

        // Change to a new fragment
        ft.replace(R.id.content_frame, newFragment, null)
        if (mFragmentManager!!.isStateSaved) {
            // We acknowledge the possibility of losing this transaction if the app undergoes
            // save&restore flow after it is committed.
            ft.commitAllowingStateLoss()
        } else {
            ft.commit()
        }
    }

    fun getActivity(): Activity? {
        return mActivity
    }
}