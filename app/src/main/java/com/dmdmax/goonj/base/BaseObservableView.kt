package com.dmdmax.goonj.base

import com.dmdmax.goonj.utility.Logger
import java.util.*
import kotlin.collections.HashSet

abstract class BaseObservableView<ListenerType>: BaseView(), ObservableView<ListenerType> {
    var mListeners: Set<ListenerType> = HashSet<ListenerType>();

    override fun registerListener(listener: ListenerType) {
        mListeners = mListeners+(listener);
    }

    override fun unregisterListener(listener: ListenerType) {
        mListeners = mListeners-(listener);
    }

    fun getListeners(): Set<ListenerType> {
        Logger.println("Listner list: "+mListeners.size);
        return Collections.unmodifiableSet(mListeners);
    }
}