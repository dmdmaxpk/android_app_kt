package com.dmdmax.goonj.models

import com.dmdmax.goonj.utility.Constants
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Video: Serializable {

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

    @SerializedName("added_dtm")
    private var added_dtm: String? = null

    @SerializedName("publish_dtm")
    private var publishDtm: String? = null

    @SerializedName("views_count")
    private var viewsCount = 0

    @SerializedName("is_premium")
    private val isPremium = false

    private var slug: String? = null
    private var posterUrl: String? = null
    private var thumbnailUrl: String? = null

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

    fun setAdded_dtm(added_dtm: String?) {
        this.added_dtm = added_dtm
    }

    fun setPublishDtm(publishDtm: String?) {
        this.publishDtm = publishDtm
    }

    fun setViewsCount(viewsCount: Int) {
        this.viewsCount = viewsCount
    }

    fun setmAnchorsList(mAnchorsList: List<Anchor>?) {
        this.mAnchorsList = mAnchorsList
    }

    fun setmProgramsList(mProgramsList: List<Program>?) {
        this.mProgramsList = mProgramsList
    }

    fun setTag(tag: Int?) {
        this.tag = tag
    }

    private var mAnchorsList: List<Anchor>? = null
    private var mProgramsList: List<Program>? = null

    private var tag: Int? = null

    enum class TileType {
        TILE_TYPE_AD, TILE_TYPE_THUMBNAIL, TILE_TYPE_PROGRAMS, TILE_TYPE_SHOW, TILE_TYPE_EPISODE, TILE_TYPE_PRANKS, TILE_TYPE_ANCHORS, TILE_TYPE_TOPICS, TILE_TYPE_FOOTER, TILE_TYPE_SEARCH_RESULT_LAYOUT, TILE_TYPE_CUSTOM_AD
    }

    private var tileType = TileType.TILE_TYPE_THUMBNAIL

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

    fun getThumbnail(): String? {
        return if (getTileType() == TileType.TILE_TYPE_THUMBNAIL) Constants.ThumbnailManager.getVodThumbnail(thumbnail);
        else thumbnail
    }

    fun getAddedDtm(): String? {
        return added_dtm
    }

    fun getPublishDtm(): String? {
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

    fun setAnchorsList(mAnchors: List<Anchor>?) {
        mAnchorsList = mAnchors
    }

    fun issPremiumVideo(): Boolean {
        return isPremium
    }
}