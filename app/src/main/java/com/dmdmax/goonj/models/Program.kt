package com.dmdmax.goonj.models

import com.dmdmax.goonj.storage.DBHelper
import com.dmdmax.goonj.utility.Constants
import com.google.gson.annotations.SerializedName

class Program {
    @SerializedName("_id")
    private val id: String? = null

    @SerializedName("name")
    private var name: String;
    private var isSelected = false

    constructor(name: String) {
        this.name = name
    }

    fun isSelected(): Boolean {
        return isSelected
    }

    fun setSelected(selected: Boolean) {
        isSelected = selected
    }

    fun getId(): String? {
        return id
    }

    fun getName(): String? {
        return name
    }

    fun getThumb(): String? {
        return Constants.ThumbnailManager.getIconThumbs(name, DBHelper.Companion.Tags.TAG_PROGRAM);
    }
}