package com.dmdmax.goonj.utility

import android.util.Log
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.SliderModel
import com.dmdmax.goonj.models.TabModel
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.utility.Constants.ThumbnailManager.getLiveThumbnail
import org.json.JSONArray
import java.util.*

class JSONParser {

    companion object {

        fun getLiveChannels(json: String?): ArrayList<Channel> {
            val list: ArrayList<Channel> = ArrayList<Channel>()
            try {
                val rootArray = JSONArray(json)
                for (i in 0 until rootArray.length()) {
                    try {
                        val model = Channel()
                        model.setId(rootArray.getJSONObject(i).getString("_id"))
                        model.setName(rootArray.getJSONObject(i).getString("name"))
                        model.setThumbnail(getLiveThumbnail(rootArray.getJSONObject(i).getString("thumbnail")))
                        model.setHlsLink(Constants.LIVE_URL + rootArray.getJSONObject(i).getString("hls_link"))
                        model.setSlug(rootArray.getJSONObject(i).getString("slug"))
                        list.add(model)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

        fun getSlider(json: String?): ArrayList<SliderModel> {
            val list: ArrayList<SliderModel> = ArrayList<SliderModel>()
            try {
                val rootArray = JSONArray(json)
                for (i in 0 until rootArray.length()) {
                    try {
                        val model = SliderModel();
                        model.setId(rootArray.getJSONObject(i).getString("_id"))
                        model.setName(rootArray.getJSONObject(i).getString("name"))
                        model.setThumb(rootArray.getJSONObject(i).getString("filename"))
                        list.add(model)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

        fun getTabsList(json: String?): ArrayList<TabModel> {
            var json = json
            json = json!!.trim { it <= ' ' }
            val list: ArrayList<TabModel> = ArrayList<TabModel>()
            try {
                val rootArray = JSONArray(json)
                for (i in 0 until rootArray.length()) {
                    list.add(TabModel(
                            rootArray.getJSONObject(i).getString("tabName"),
                            if (rootArray.getJSONObject(i).has("slug")) rootArray.getJSONObject(i).getString("slug") else null,
                            if (rootArray.getJSONObject(i).has("carousel")) rootArray.getJSONObject(i).getString("carousel") else "",
                            rootArray.getJSONObject(i).getString("category"),
                            if (rootArray.getJSONObject(i).has("url")) rootArray.getJSONObject(i).getString("url") else null,
                            if (rootArray.getJSONObject(i).has("desc")) rootArray.getJSONObject(i).getString("desc") else null,
                            if (rootArray.getJSONObject(i).has("style")) rootArray.getJSONObject(i).getString("style") else null)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

        fun getFeed(json: String?): List<Video>? {
            val list: MutableList<Video> = ArrayList()
            try {
                val rootArray = JSONArray(json)
                for (i in 0 until rootArray.length()) {
                    try {
                        val model = Video(Video.TileType.TILE_TYPE_THUMBNAIL)
                        model.setId(rootArray.getJSONObject(i).getString("_id"))
                        model.setCategory(rootArray.getJSONObject(i).getString("category"))
                        model.setProgram(rootArray.getJSONObject(i).getString("program"))
                        model.setTitle(rootArray.getJSONObject(i).getString("title"))
                        model.setAnchor(rootArray.getJSONObject(i).getString("anchor"))
                        model.setPublishDtm(rootArray.getJSONObject(i).getString("publish_dtm"))
                        model.setDuration(rootArray.getJSONObject(i).getInt("duration"))
                        model.setThumbnail(rootArray.getJSONObject(i).getString("thumbnail"))
                        model.setFileName(rootArray.getJSONObject(i).getString("file_name"))
                        model.setSource(rootArray.getJSONObject(i).getString("source"))
                        model.setDescription(rootArray.getJSONObject(i).getString("description"))
                        model.setTopics(Utility.getStringList(rootArray.getJSONObject(i).getJSONArray("topics")))
                        list.add(model)
                    } catch (e: Exception) {
                        Logger.println("Exception Message: " + e.message)
                    }
                }
            } catch (e: Exception) {
                Logger.println("Reason: " + e.message)
                e.printStackTrace()
            }
            return list
        }
    }
}