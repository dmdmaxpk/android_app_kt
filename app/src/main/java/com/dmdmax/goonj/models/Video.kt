package com.dmdmax.goonj.models

import com.dmdmax.goonj.utility.Constants
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Video: Serializable {

    private var key: String? = null;

    @SerializedName("_id")
    private var id: String? = null;

    @SerializedName("guests")
    private var guests: Array<String>? = null;

    @SerializedName("topics")
    private var topics: Array<String?>? = null;

    @SerializedName("title")
    private var title: String? = null

    @SerializedName("description")
    private var description: String? = null

    @SerializedName("category")
    private var category: String? = null

    @SerializedName("sub_category")
    private var subCategory: String? = null

    @SerializedName("source")
    private var source: String? = null

    @SerializedName("program")
    private var programs: String? = null

    @SerializedName("anchor")
    private var anchor: String? = null

    @SerializedName("file_name")
    private var file_name: String? = null

    @SerializedName("duration")
    private var duration = 0

    @SerializedName("thumbnail")
    private var thumbnail: String? = null

    @SerializedName("small_thumbnail")
    private var small_thumbnail: String? = null

    @SerializedName("added_dtm")
    private var added_dtm: String? = null

    @SerializedName("publish_dtm")
    private lateinit var publishDtm: String

    @SerializedName("views_count")
    private var viewsCount = 0

    @SerializedName("is_premium")
    private val isPremium = false

    private var slug: String? = null
    private var posterUrl: String? = null
    private var thumbnailUrl: String? = null
    private var isLive: Boolean = false

    fun setLive(live: Boolean){
        this.isLive = live;
    }

    fun getLive(): Boolean {
        return this.isLive;
    }

    fun getVideoUrl(): String? {
        return videoUrl
    }

    fun setVideoUrl(videoUrl: String?) {
        this.videoUrl = videoUrl
    }

    private var videoUrl: String? = null

    fun getSlug(): String? {
        return slug
    }

    fun setSlug(slug: String?) {
        this.slug = slug
    }

    fun getKey(): String? {
        return key
    }

    fun setKey(key: String?) {
        this.key = key
    }

    fun getPosterUrl(): String? {
        return posterUrl
    }

    fun setPosterUrl(posterUrl: String?) {
        this.posterUrl = posterUrl
    }

    fun getThumbnailUrl(): String? {
        return thumbnailUrl
    }

    fun setThumbnailUrl(thumbnailUrl: String?) {
        this.thumbnailUrl = thumbnailUrl
    }

    constructor(tileType: TileType) {
        this.tileType = tileType
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun Video() {}

    fun setGuests(guests: Array<String>) {
        this.guests = guests
    }

    fun setTopics(topics: Array<String?>?) {
        this.topics = topics
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun setCategory(category: String?) {
        this.category = category
    }

    fun setSubCategory(subCategory: String?) {
        this.subCategory = subCategory
    }

    fun setSource(source: String?) {
        this.source = source
    }

    fun setProgram(programs: String?) {
        this.programs = programs
    }

    fun setAnchor(anchor: String?) {
        this.anchor = anchor
    }

    fun setFileName(file_name: String?) {
        this.file_name = file_name
    }

    fun setDuration(duration: Int) {
        this.duration = duration
    }

    fun setThumbnail(thumbnail: String?) {
        this.thumbnail = thumbnail
    }

    fun setSmallThumbnail(thumbnail: String?) {
        this.small_thumbnail = thumbnail
    }

    fun setAdded_dtm(added_dtm: String?) {
        this.added_dtm = added_dtm
    }

    fun setPublishDtm(publishDtm: String) {
        this.publishDtm = publishDtm
    }

    fun setViewsCount(viewsCount: Int) {
        this.viewsCount = viewsCount
    }

    fun setmAnchorsList(mAnchorsList: List<Anchor>) {
        this.mAnchorsList = mAnchorsList
    }

    fun setChannelsList(mChannelsList: ArrayList<Channel>) {
        this.mChannelsList = mChannelsList
    }

    fun getChannelsList(): ArrayList<Channel> {
       return this.mChannelsList;
    }

    fun setmProgramsList(mProgramsList: List<Program>?) {
        this.mProgramsList = mProgramsList
    }

    fun setTag(tag: Int?) {
        this.tag = tag
    }

    private lateinit var mChannelsList: ArrayList<Channel>;
    private lateinit var mAnchorsList: List<Anchor>;
    private var mProgramsList: List<Program>? = null

    private var tag: Int? = null

    enum class TileType {
         TILE_TYPE_THUMBNAIL, TILE_TYPE_THUMBNAIL_FLIP, TILE_TYPE_RELATED_CHANNELS,TILE_TYPE_PROGRAMS, TILE_TYPE_SHOW, TILE_TYPE_EPISODE, TILE_TYPE_PRANKS, TILE_TYPE_ANCHORS, TILE_TYPE_TOPICS, TILE_TYPE_FOOTER, TILE_TYPE_SEARCH_RESULT_LAYOUT, TILE_TYPE_CUSTOM_AD
    }

    private lateinit var tileType: TileType;

    fun getTileType(): TileType {
        return tileType
    }

    fun setTileType(tileType: TileType) {
        this.tileType = tileType
    }

    fun getTag(): Int {
        return tag!!
    }

    fun setTag(tag: Int) {
        this.tag = tag
    }

    fun getId(): String? {
        return id
    }

    fun getGuests(): Array<String>? {
        return guests
    }

    fun getTopics(): Array<String?>? {
        return topics
    }

    fun getTitle(): String? {
        return title
    }

    fun getDescription(): String? {
        return description
    }

    fun getCategory(): String? {
        return category
    }

    fun getSubCategory(): String? {
        return subCategory
    }

    fun getSource(): String? {
        return source
    }

    fun getProgram(): String? {
        return programs
    }

    fun getAnchor(): String? {
        return anchor
    }

    fun getFileName(): String? {
        return file_name
    }

    fun getDuration(): Int {
        return duration
    }

    fun getThumbnail(slug: String?): String? {
        return if (getTileType() == TileType.TILE_TYPE_THUMBNAIL || getTileType() == TileType.TILE_TYPE_THUMBNAIL_FLIP ) Constants.ThumbnailManager.getVodThumbnail(thumbnail);
        else thumbnail
    }

    fun getSmallThumbnail(slug: String?): String? {
        return if (getTileType() == TileType.TILE_TYPE_THUMBNAIL)
            Constants.ThumbnailManager.getVodThumbnail(small_thumbnail);
        else small_thumbnail
    }

    fun getAddedDtm(): String? {
        return added_dtm
    }

    fun getPublishDtm(): String {
        return publishDtm
    }

    fun getViewsCount(): Int {
        return viewsCount
    }

    fun getProgramsList(): List<Program>? {
        return mProgramsList
    }

    fun setProgramsList(mPrograms: List<Program>?) {
        mProgramsList = mPrograms
    }

    fun getAnchorsList(): List<Anchor>? {
        return mAnchorsList
    }

    fun issPremiumVideo(): Boolean {
        return isPremium
    }
}