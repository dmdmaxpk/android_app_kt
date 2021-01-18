package com.dmdmax.goonj.network.responses

import com.dmdmax.goonj.utility.Constants
import com.google.gson.annotations.SerializedName

class Channel {
    @SerializedName("_id")
    private var id: String? = null

    @SerializedName("name")
    private var name: String? = null

    @SerializedName("slug")
    private val slug: String? = null

    @SerializedName("hls_link")
    private var hlsLink: String? = null

    @SerializedName("ad_tag")
    private val adTag: String? = null

    @SerializedName("thumbnail")
    private val thumbnail: String? = null

    @SerializedName("views_count")
    private val viewsCount = 0

    @SerializedName("is_streamable")
    private val is_streamable = false

    @SerializedName("package_id")
    private val packageId: Array<String>? = null;

    fun setId(id: String?) {
        this.id = id
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getPackageIds(): Array<String>? {
        return packageId
    }

    private var status = Status.STATUS_NOT_PLAYING

    fun getId(): String? {
        return id
    }

    fun getName(): String? {
        return name
    }

    fun getThumbnail(): String? {
        return Constants.ThumbnailManager.getLiveThumbnail(thumbnail)
    }

    fun getStatus(): Status? {
        return status
    }

    fun getSlug(): String? {
        return slug
    }

    fun getHlsLink(): String? {
        return if (hlsLink!!.startsWith("http")) hlsLink else Constants.LIVE_URL.toString() + hlsLink
    }

    fun getAdTagSource(): String? {
        return adTag
    }

    fun getViewsCount(): Int {
        return viewsCount
    }

    fun setStatus(status: Status) {
        this.status = status
    }

    enum class Status {
        STATUS_PLAYING, STATUS_NOT_PLAYING
    }

    fun setHlsLink(hlsLink: String?) {
        this.hlsLink = hlsLink
    }

    fun isAllowed(subscribedPackageId: String): Boolean {
        return findIfAllowed(subscribedPackageId)
    }

    private fun findIfAllowed(subscribedPackageId: String): Boolean {
        for (str in packageId!!) {
            if (str == subscribedPackageId) return true
        }
        return false
    }
}