package com.dmdmax.goonj.models

class BottomMenu {
    private lateinit var mTitle: String;
    private var mFocusedImageView: Int
    private var mNonFocusedImageView: Int
    private var mIsFocused: Boolean = false

    constructor(mTitle: String, mFocusedImageView: Int, mNonFocusedImageView: Int, mIsFocused: Boolean){
        this.mTitle = mTitle;
        this.mFocusedImageView = mFocusedImageView;
        this.mNonFocusedImageView = mNonFocusedImageView;
        this.mIsFocused = mIsFocused;
    }

    fun getTitle(): String {
        return mTitle;
    }

    fun getFocusedImage(): Int {
        return mFocusedImageView;
    }

    fun getNonFocusedImage(): Int {
        return mNonFocusedImageView;
    }

    fun setFocused(isFocus: Boolean) {
        this.mIsFocused = isFocus;
    }

    fun isFocused(): Boolean {
        return mIsFocused;
    }
}