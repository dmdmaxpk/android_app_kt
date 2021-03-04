package com.dmdmax.goonj.gps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.screens.dialogs.DialogManager
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Logger
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*


class GPSHelper {

    private var fusedLocationClient: FusedLocationProviderClient
    private var mContext: Context;

    var latitude: Double = 0.0;
    var longitude: Double = 0.0;
    var altitude: Double = 0.0;

    constructor(context: Context){
        this.mContext = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
    }

    public interface LocationUpdatedListener{
        fun onUpdated(lat: Double, lng: Double, alt: Double);
    }

    @SuppressLint("MissingPermission")
    fun updateLocation(listener: LocationUpdatedListener?) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if(location != null){
                latitude = location.latitude;
                longitude = location.longitude;
                altitude = location.altitude;
                listener?.onUpdated(this.latitude, this.longitude, this.altitude);
            }else{
                listener?.onUpdated(0.0, 0.0, 0.0);
            }
        }
    }

    fun isLocationEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is a new method provided in API 28
            val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            lm.isLocationEnabled
        } else {
            // This was deprecated in API 28
            val mode: Int = Settings.Secure.getInt(mContext.contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF)
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }

    fun displaySwitchOnSettingsDialog(){
        DialogManager().displayLocationOffDialog(mContext, object : DialogManager.LocationPermissionClickListener {
            override fun onPositiveButtonClick() {
                mContext.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

            override fun onNegativeButtonClick() {
                Logger.println("displaySwitchOnSettingsDialog - onNegativeButtonClick")
            }

            override fun onDontAskClick() {
                GoonjPrefs(mContext).setDontAsk(true);
            }
        });
    }

    fun getCity(): String? {
        val gCoder = Geocoder(mContext as BaseActivity, Locale.getDefault())
        try {
            val addresses: List<Address> = gCoder.getFromLocation(latitude, longitude, 1);
            Logger.println("Address: "+addresses);
            if(addresses.size > 0){
                val cityName = addresses[0].locality + ", " + addresses[0].countryName
                return cityName;
            }else{
                return "Error";
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null;
    }

}