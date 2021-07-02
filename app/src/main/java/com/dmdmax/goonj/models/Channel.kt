package com.dmdmax.goonj.models

import com.dmdmax.goonj.utility.Constants
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Channel: Serializable {
    private lateinit var id: String;
    private lateinit var name: String;
    private lateinit var slug: String;
    private lateinit var hlsLink: String;
    private lateinit var thumbnail: String;
    private lateinit var viewCount: String;
    private var category: String = "";

    fun setId(id: String) {
        this.id = id
    }

    fun setSlug(slug: String) {
        this.slug = slug
    }

    fun setViewCount(count: String) {
        this.viewCount = count
    }

    fun getViewCount(): String {
        return this.viewCount;
    }

    fun setCategory(category: String) {
        this.category = category
    }

    fun setName(name: String) {
        this.name = name
    }

    fun setThumbnail(thumbnail: String) {
        this.thumbnail = thumbnail
    }


    fun getId(): String {
        return id
    }

    fun getCategory(): String {
        return category
    }

    fun getName(): String {
        return name
    }

    fun getThumbnail(): String {
        return Constants.ThumbnailManager.getLiveThumbnail(thumbnail)
    }

    fun getSlug(): String {
        return slug
    }

    fun getHlsLink(): String {
        return if (hlsLink.startsWith("http")) hlsLink else Constants.LIVE_URL.toString() + hlsLink
    }

    fun setHlsLink(hlsLink: String) {
        this.hlsLink = hlsLink
    }
}