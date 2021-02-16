package com.dmdmax.goonj.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PaywallPackage: Serializable {
    @SerializedName("_id")
    private var id: String? = null

    @SerializedName("package_name")
    private var name: String? = null

    @SerializedName("package_desc")
    private var desc: String? = null

    @SerializedName("display_price_point")
    private var pricePoint: String? = null

    @SerializedName("logos")
    private var logos: List<String?>? = null

    @SerializedName("default")
    private var isSelected = false

    fun getSlug(): String? {
        return slug
    }

    fun setSlug(slug: String?) {
        this.slug = slug
    }

    @SerializedName("slug")
    private var slug: String? = null

    fun isPaywallId(): String? {
        return paywallId
    }

    fun setPaywallId(paywallId: String?) {
        this.paywallId = paywallId
    }

    @SerializedName("paywall_id")
    private var paywallId: String? = null

    fun getLogos(): List<String?>? {
        return logos
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

    fun getDesc(): String? {
        return desc
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun setDesc(desc: String?) {
        this.desc = desc
    }

    fun setPricePoint(pricePoint: String?) {
        this.pricePoint = pricePoint
    }

    fun setLogos(logos: List<String?>?) {
        this.logos = logos
    }

    fun getPricePoint(): String? {
        return pricePoint
    }
}