package com.dmdmax.goonj.models

class MediaModel {

    private var vodId: String? = null
    private var url: String? = null
    private lateinit var filename: String;

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


    fun getFilename(): String {
        return filename
    }

    fun setFilename(name: String) {
        this.filename = name;
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