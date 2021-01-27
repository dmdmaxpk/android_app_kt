package com.dmdmax.goonj.network.responses

import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Utility
import com.google.gson.annotations.SerializedName

class Category {
    private lateinit var id: String;
    private lateinit var name: String;
    private lateinit var subCats: ArrayList<SubCategory>;
    private var isSelected = false

    constructor(id: String, name: String, subCategory: ArrayList<SubCategory>) {
        this.name = name;
        this.id = id;
        this.subCats = subCategory;
    }

    fun isSelected(): Boolean {
        return isSelected
    }

    fun setSelected(selected: Boolean) {
        isSelected = selected
    }

    fun getId(): String {
        return id
    }

    fun getName(): String {
        return name
    }

    fun getSubCats(): ArrayList<SubCategory>{
        return this.subCats;
    }
}