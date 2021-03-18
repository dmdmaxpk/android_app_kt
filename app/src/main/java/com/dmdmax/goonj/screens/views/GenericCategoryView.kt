package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView
import com.dmdmax.goonj.models.Category
import com.dmdmax.goonj.models.TabModel
import com.dmdmax.goonj.models.Video

interface GenericCategoryView: ObservableView<GenericCategoryView.Listener> {

    interface Listener {
        fun  onItemClick(video: Video);
    }

    fun  initialize();
    fun loadVideos(category: TabModel);
}