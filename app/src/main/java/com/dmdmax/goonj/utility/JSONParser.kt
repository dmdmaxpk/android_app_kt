package com.dmdmax.goonj.utility

import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.SliderModel
import com.dmdmax.goonj.models.TabModel
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.payments.BinjeePaymentHelper
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.utility.Constants.ThumbnailManager.getVodThumbnail
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

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
                        model.setThumbnail((rootArray.getJSONObject(i).getString("thumbnail")))
                        model.setHlsLink(Constants.LIVE_URL + rootArray.getJSONObject(i).getString("hls_link"))
                        model.setSlug(rootArray.getJSONObject(i).getString("slug"))
                        model.setCategory(rootArray.getJSONObject(i).getString("category"))
                        model.setViewCount(rootArray.getJSONObject(i).getString("views_count"))
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
                        model.setThumb(Constants.CDN_STATIC_URL + "dramas/" + model.getName()!!.replace(" ", "%20")+".jpg");
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

        fun getFeed(json: String?, slug: String?): ArrayList<Video> {
            val list: ArrayList<Video> = ArrayList()
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
                        if(slug != null){
                            model.setSlug(slug);
                        }
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

        fun getVideos(videos: String?, slug: String): ArrayList<Video> {
            val videoList: ArrayList<Video> = ArrayList();

            try {
                val rootArr = JSONArray(videos)
                for (i in 0 until rootArr.length()) {
                    val video = Video(Video.TileType.TILE_TYPE_THUMBNAIL)
                    video.setId(rootArr.getJSONObject(i).getString("videos_id"))
                    video.setTitle(rootArr.getJSONObject(i).getString("title"))
                    video.setDescription(rootArr.getJSONObject(i).getString("description"))
                    video.setCategory(slug)
                    video.setSlug(slug)

                    if (rootArr.getJSONObject(i).has("thumbnail_url")) {
                        video.setThumbnailUrl(rootArr.getJSONObject(i).getString("thumbnail_url"))
                        video.setPosterUrl(rootArr.getJSONObject(i).getString("poster_url"))
                        if (rootArr.getJSONObject(i).has("video_url")) {
                            video.setVideoUrl(rootArr.getJSONObject(i).getString("video_url"))
                        }

                        if (rootArr.getJSONObject(i).has("file_url")) {
                            video.setVideoUrl(rootArr.getJSONObject(i).getString("file_url"))
                        }
                    }
                    videoList.add(video)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return videoList;
        }

        fun getBinjeeCategories(videos: String?): ArrayList<Video> {
            val videoList: ArrayList<Video> = ArrayList();

            try {
                val rootArr = JSONObject(videos).getJSONArray("info")
                for (i in 0 until rootArr.length()) {
                    val video = Video(Video.TileType.TILE_TYPE_THUMBNAIL)
                    video.setId(rootArr.getJSONObject(i).getString("subcat_id"))
                    video.setTitle(rootArr.getJSONObject(i).getString("title"))
                    video.setDescription(rootArr.getJSONObject(i).getString("description"))
                    video.setSlug(PaywallBinjeeFragment.SLUG)
                    video.setPosterUrl(rootArr.getJSONObject(i).getString("thumbnail_url"))

                    videoList.add(video)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return videoList;
        }

        fun getCategory(json: String?, slug: String, key: String): ArrayList<Video> {
            val videoList: ArrayList<Video> = ArrayList();

            try {
                val rootArr = JSONArray(json)
                for (i in 0 until rootArr.length()) {
                    val video = Video(Video.TileType.TILE_TYPE_THUMBNAIL);
                    video.setId(rootArr.getJSONObject(i).getString("_id"));
                    video.setTitle(rootArr.getJSONObject(i).getString("name"));
                    video.setPosterUrl(Constants.CDN_STATIC_URL + slug + "/" + video.getTitle()!!.replace(" ", "%20")+".jpg");
                    video.setKey(key);
                    video.setCategory(slug);
                    videoList.add(video)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return videoList;
        }

        fun getLiveDetailsModel(response: String?): Channel? {
            var model: Channel? = Channel()
            try {
                val root = JSONObject(response)
                model!!.setId(root.getString("_id"))
                model.setHlsLink(root.getString("hls_link"))
            } catch (e: java.lang.Exception) {
                model = null;
                e.printStackTrace()
            }
            return model
        }

        fun getVodDetailsModel(response: String?): Video? {
            var model: Video? = Video(Video.TileType.TILE_TYPE_THUMBNAIL)
            try {
                val root = JSONObject(response)
                model!!.setId(root.getString("_id"))
                model.setCategory(root.getString("category"))
                model.setProgram(root.getString("program"))
                model.setTitle(root.getString("title"))
                model.setAnchor(root.getString("anchor"))
                model.setPublishDtm(root.getString("publish_dtm"))
                model.setDuration(root.getInt("duration"))
                model.setThumbnail(getVodThumbnail(root.getString("thumbnail")))
                model.setFileName(root.getString("file_name"))
                model.setSource(root.getString("source"))
                model.setDescription(root.getString("description"))
                model.setTopics(Utility.getStringList(root.getJSONArray("topics")))
            } catch (e: java.lang.Exception) {
                model = null
                e.printStackTrace()
            }
            return model
        }
    }
}