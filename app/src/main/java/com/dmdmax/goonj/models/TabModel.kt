package com.dmdmax.goonj.models

import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment

class TabModel: java.io.Serializable {
    private  var tabName: String? = null;
    private  var slug:kotlin.String? = null;
    private  var carousel:kotlin.String? = null;
    private  var category:kotlin.String? = null;
    private  var url:kotlin.String? = null;
    private  var desc:kotlin.String? = null;
    private  var style:kotlin.String? = null

    constructor(tabName: String, slug: String?, carousel: String, category: String, url: String?, desc: String?, style: String?) {
        this.tabName = tabName
        this.slug = slug
        this.carousel = carousel
        this.category = category
        this.url = url
        this.desc = desc
        this.style = style
    }

    constructor(tabName: String, carousel: String, category: String, style: String, url: String) {
        this.tabName = tabName
        this.carousel = carousel
        this.category = category
        this.style = style
        this.url = url
    }

    fun getStyle(): String? {
        return style
    }

    fun getTabName(): String? {
        return tabName
    }

    fun getCategory(): String? {
        return category
    }

    fun getCarousel(): String? {
        return carousel
    }

    fun getUrl(): String? {
        return url
    }

    fun getDesc(): String? {
        return desc
    }

    fun getSlug(): String? {
        return slug
    }

    companion object {
        fun getEpisodeTab(): TabModel{
            return TabModel("episode", PaywallComedyFragment.SLUG, "null", "episode", null, null, null);
        }
    }
}