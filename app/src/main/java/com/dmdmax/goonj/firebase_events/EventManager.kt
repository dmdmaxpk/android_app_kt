package com.dmdmax.goonj.firebase_events

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics

open class EventManager<out T: Any, in A>(creator: (A) -> T) {

    /*private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics;
    private lateinit var mContext: Context;

    fun getInstance(context: Context): T {
        val checkInstance = instance
        if (checkInstance != null) {
            return checkInstance
        }

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null) {
                checkInstanceAgain
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }*/






    /*public val mManager: EventManager;

    private constructor(context: Context) {
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        this.mContext = context;
    }

    companion object {

    }*/
}