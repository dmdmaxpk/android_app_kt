package com.dmdmax.goonj.models

class City {
    private lateinit var mCity: String;
    private lateinit var mLat: String;
    private lateinit var mLng: String;
    private lateinit var mProvince: String;

    constructor(city: String, lat: String, lng: String, province: String){
        this.mCity = city;
        this.mLat = lat;
        this.mLng = lng;
        this.mProvince = province;
    }

    fun getCity(): String {
        return this.mCity;
    }

    fun getLongitude(): String {
        return this.mLng;
    }

    fun getLatitude(): String {
        return this.mLat;
    }

    fun getProvince(): String {
        return this.mProvince;
    }
}