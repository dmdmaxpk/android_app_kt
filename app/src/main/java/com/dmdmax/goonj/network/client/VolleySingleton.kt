package com.dmdmax.goonj.network.client

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.dmdmax.goonj.utility.Utility

class VolleySingleton {
    private  lateinit var mRequestQueue: RequestQueue;
    private lateinit var mContext: Context

    private object HOLDER {
        val INSTANCE = VolleySingleton()
    }

    companion object {
        val instance: VolleySingleton by lazy { HOLDER.INSTANCE }
    }

    public fun getInstance(context: Context): VolleySingleton {
        this.mContext = context
        return instance;
    }

    private fun getRequestQueue(): RequestQueue {
        if(!this::mRequestQueue.isInitialized){
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue
    }

    public fun <T> addToRequestQueue(req: Request<T>) {
        req.retryPolicy = DefaultRetryPolicy(
                40 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        req.tag = Utility.TAG
        getRequestQueue().add(req)
    }
}