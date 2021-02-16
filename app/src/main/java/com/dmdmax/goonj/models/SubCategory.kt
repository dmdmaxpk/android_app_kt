package com.dmdmax.goonj.models

class SubCategory {
    private lateinit var id: String;
    private lateinit var name: String;
    private var isSelected = false

    constructor(id: String, name: String) {
        this.name = name
        this.id = id;
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
}