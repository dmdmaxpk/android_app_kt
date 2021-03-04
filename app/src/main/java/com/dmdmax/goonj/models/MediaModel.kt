package com.dmdmax.goonj.models

class MediaModel {

    private var vodId: String? = null
    private var url: String? = null

    fun isLive(): Boolean {
        return isLive
    }

    fun setLive(live: Boolean) {
        isLive = live
    }

    private var isLive = false
    private var durationInSeconds = 0

    fun getId(): String? {
        return vodId
    }

    fun setId(vodId: String?) {
        this.vodId = vodId
    }

    fun getUrl(): String? {
        return url
    }

    fun setUrl(url: String?) {
        this.url = url
    }

    fun getDurationInSeconds(): Int {
        return durationInSeconds
    }

    fun setDurationInSeconds(durationInSeconds: Int) {
        this.durationInSeconds = durationInSeconds
    }
}