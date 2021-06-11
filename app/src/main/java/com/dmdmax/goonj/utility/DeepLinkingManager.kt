package com.dmdmax.goonj.utility

class DeepLinkingManager {
    object Mapper {
        const val OPEN_APP = "open_app"
        const val OPEN_NEWS = "open_news"
        const val OPEN_LIVE_BOTTOM_MENU = "open_live_menu"
        const val OPEN_LIVE_SPECIFIC_CHANNEL = "open_specific_channel"
        const val OPEN_TAB = "open_tab"
        const val OPEN_UN_SUB = "un_sub"
        const val OPEN_COMEDY_VIDEO = "open_comedy_video"
    }

    companion object {
        fun getMappedValue(url: String): String {
            val urls = url.split("/".toRegex()).toTypedArray()
            return if (urls[urls.size - 1] == "open") {
                Mapper.OPEN_APP
            } else if (urls[urls.size - 2] == "channel") {
                // Open specific channel
                Mapper.OPEN_LIVE_SPECIFIC_CHANNEL
            } else if (urls[urls.size - 1] == "live-tv") {
                // Open live tv bottom menu
                Mapper.OPEN_LIVE_BOTTOM_MENU
            } else {
                var actionIndex = getActionIndex(urls[urls.size - 1])
                if (actionIndex == -1) {
                    actionIndex = getActionIndex(urls[urls.size - 2])
                    if (actionIndex != -1) {
                        Constants.TAB_INDEX = actionIndex
                        Mapper.OPEN_TAB
                    } else {
                        Constants.TAB_INDEX = 1
                        Mapper.OPEN_TAB
                    }
                } else {
                    Constants.TAB_INDEX = actionIndex
                    Mapper.OPEN_TAB
                }
            }
        }

        private fun getActionIndex(str: String): Int {
            val categories = Constants.CATEGORIES_STRING_JSON!!.split(",".toRegex()).toTypedArray()
            for (i in categories.indices) {
                if (str == categories[i]) {
                    return i
                }
            }
            return -1
        }
    }
}