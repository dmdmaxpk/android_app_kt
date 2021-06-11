package com.dmdmax.goonj.models

import org.json.JSONObject

class Episode {
    var id: String? = null
    var name: String? = null
    var streamKey: String? = null
    var fileType: String? = null
    var imageUrl: String? = null
    var fileUrl: String? = null

    companion object {
        fun getEpisode(obj: JSONObject): Episode{
            val mEpisode = Episode()
            mEpisode.id = obj.getString("episodes_id")
            mEpisode.name = obj.getString("episodes_name")
            mEpisode.streamKey = obj.getString("stream_key")
            mEpisode.fileType = obj.getString("file_type")
            mEpisode.imageUrl = obj.getString("image_url")
            mEpisode.fileUrl = obj.getString("file_url")
            return mEpisode;
        }

        fun getVideo(obj: JSONObject, slug: String?): Video {
            val mVideo = Video(Video.TileType.TILE_TYPE_EPISODE)
            mVideo.setId(obj.getString("episodes_id"))
            mVideo.setTitle(obj.getString("episodes_name"))
            mVideo.setVideoUrl(obj.getString("file_url"))
            mVideo.setThumbnailUrl(obj.getString("image_url"))
            mVideo.setSlug(slug);
            if(slug != null)
                mVideo.setCategory(slug)
            return mVideo;
        }
    }
}