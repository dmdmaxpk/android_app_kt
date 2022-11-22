package com.dmdmax.goonj.models

import com.dmdmax.goonj.storage.DBHelper
import com.dmdmax.goonj.utility.Constants
import com.google.gson.annotations.SerializedName

class Anchor {
    @SerializedName("_id")
    private var id: String? = null

    @SerializedName("name")
    private var name: String? = null
    private var isSelected = false
    private var thumb: String? = null

    constructor(name: String?) {
        this.name = name
    }

    fun isSelected(): Boolean {
        return isSelected
    }

    fun setSelected(selected: Boolean) {
        isSelected = selected
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun setName(name: String?) {
        this.name = name
    }



    fun getId(): String? {
        return id
    }

    fun getName(): String? {
        return name
    }

    fun getThumb(): String? {
        return null; //if (thumb == null) Constants.ThumbnailManager.getIconThumbs(name!!, DBHelper.Companion.Tags.TAG_ANCHOR) else thumb
    }

    fun setThumb(thumb: String?) {
        this.thumb = thumb
    }
}