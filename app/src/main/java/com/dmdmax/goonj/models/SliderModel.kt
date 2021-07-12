package com.dmdmax.goonj.models

import com.dmdmax.goonj.storage.DBHelper
import com.dmdmax.goonj.utility.Constants
import com.google.gson.annotations.SerializedName

class SliderModel {

    private var id: String? = null
    private var name: String? = null
    private var thumb: String? = null
    private var category: String = "drama"
    private var live: Boolean = false;
    private var mChannel: Channel? = null;

    constructor(){}

    fun setId(id: String?) {
        this.id = id;
    }

    fun setName(name: String?) {
        this.name = name;
    }

    fun setThumb(thumb: String?) {
        this.thumb = thumb;
    }

    fun setCategory(category: String) {
        this.category = category;
    }

    fun setLive(isLive: Boolean) {
        this.live = isLive;
    }

    fun setChannel(channel: Channel) {
        this.mChannel = channel;
    }

    fun getId(): String? {
        return id;
    }

    fun getName(): String? {
        return name;
    }

    fun getThumb(): String? {
        return thumb;
    }

    fun getCategory(): String {
        return this.category;
    }

    fun isLive(): Boolean {
        return this.live;
    }

    fun getChannel(): Channel? {
        return this.mChannel;
    }
}