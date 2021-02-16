package com.dmdmax.goonj.models

import com.dmdmax.goonj.storage.DBHelper
import com.dmdmax.goonj.utility.Constants
import com.google.gson.annotations.SerializedName

class SliderModel {

    private var id: String? = null
    private var name: String? = null
    private var thumb: String? = null

    constructor(){}

    constructor(id: String?, name: String?, thumb: String?) {
        this.id = id;
        this.name = name;
        this.thumb = thumb;
    }

    fun setId(id: String?) {
        this.id = id;
    }

    fun setName(name: String?) {
        this.name = name;
    }

    fun setThumb(thumb: String?) {
        this.thumb = thumb;
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
}