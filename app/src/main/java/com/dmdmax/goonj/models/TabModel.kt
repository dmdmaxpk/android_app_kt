package com.dmdmax.goonj.models

import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment

class TabModel: java.io.Serializable {
    private  var tabName: String? = null;
    private  var slug: String? = null;
    private  var carousel: String? = null;
    private  var category: String? = null;
    private  var url: String? = null;
    private  var desc: String? = null;
    private  var style: String? = null
    private var resourceId: String? = null;

    constructor(tabName: String, slug: String?, carousel: String, category: String, url: String?, desc: String?, style: String?, resourceId: String?) {
        this.tabName = tabName
        this.slug = slug
        this.carousel = carousel
        this.category = category
        this.url = url
        this.desc = desc
        this.style = style
        this.resourceId = resourceId
    }

    constructor(tabName: String, carousel: String, category: String, style: String, url: String, resourceId: String?) {
        this.tabName = tabName
        this.carousel = carousel
        this.category = category
        this.style = style
        this.url = url
        this.resourceId = resourceId
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

    fun getResourceId(): String? {
        return resourceId;
    }

    companion object {
        fun getEpisodeTab(): TabModel{
            return TabModel("episode", PaywallComedyFragment.SLUG, "null", "episode", null, null, null, null);
        }
    }
}