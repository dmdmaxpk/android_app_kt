package com.dmdmax.goonj.network.responses

class BitRatesModel: java.io.Serializable {
    private var bitrate: String? = null
    private var selected = false

    fun getId(): Int? {
        return id
    }

    private var id: Int? = null

    constructor(id: Int?, bitrate: String?, selected: Boolean) {
        this.bitrate = bitrate
        this.selected = selected
        this.id = id
    }

    fun isSelected(): Boolean {
        return selected
    }

    fun getBitrate(): String? {
        return bitrate
    }

    fun setSelected(selected: Boolean) {
        this.selected = selected
    }
}