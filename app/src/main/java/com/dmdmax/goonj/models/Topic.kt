package com.dmdmax.goonj.models

import com.google.gson.annotations.SerializedName

class Topic {
    @SerializedName("_id")
    private val id: String? = null

    @SerializedName("name")
    private var name: String? = null
    private var isSelected = false

    constructor(name: String?) {
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
}