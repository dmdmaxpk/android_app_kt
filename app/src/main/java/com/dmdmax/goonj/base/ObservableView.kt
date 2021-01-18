package com.dmdmax.goonj.base

interface ObservableView<ListenerTpe>: BaseMvcView {
    fun registerListener(listener: ListenerTpe)
    fun unregisterListener(listener: ListenerTpe)
}