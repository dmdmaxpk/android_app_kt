package com.dmdmax.goonj.screens.fragments.hometabs

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.screens.dialogs.DialogManager
import com.dmdmax.goonj.screens.views.LiveTvView

class LiveTvFragment: BaseFragment() {

    private lateinit var mView: LiveTvView;
    private val REQUEST_CODE = 1422;

    companion object {
        val ARGS_TAB: String = "args"

        fun newInstance(args: Bundle?): LiveTvFragment {
            val fragment = LiveTvFragment();
            if (args != null) {
                fragment.arguments = args;
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = getCompositionRoot().getViewFactory().getLiveTvView(container!!);
        return mView.getRootView();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView.initialize();
        mView.displayPrayerTime();
    }

    override fun onStart() {
        super.onStart()
        mView.getLogger().println("onStart")
        /*if(!mView.getPrefs().isDontAsk()){
            Handler().postDelayed(Runnable{
                if (
                        !ContextCompat.checkSelfPermission(context as BaseActivity, Manifest.permission.ACCESS_FINE_LOCATION).equals(PackageManager.PERMISSION_GRANTED) ||
                        !ContextCompat.checkSelfPermission(context as BaseActivity, Manifest.permission.ACCESS_COARSE_LOCATION).equals(PackageManager.PERMISSION_GRANTED)
                ) {
                    openLocationPermissionDialog();
                }else{
                    // Location permission has already been granted
                    mView.displayPrayerTime();
                }
            }, 1000);
        }*/
    }

    override fun onStop() {
        super.onStop()
        mView.cancelTimer();
    }

    private fun openLocationPermissionDialog(){
        DialogManager().grantLocationPermission(context as BaseActivity, object: DialogManager.LocationPermissionClickListener{
            override fun onPositiveButtonClick() {
                requestLocationPermission();
            }

            override fun onNegativeButtonClick() {
                mView.getToaster().printToast(context, "Location permission cancelled");
            }

            override fun onDontAskClick() {
                mView.getPrefs().setDontAsk(true);
            }
        });
    }

    private fun requestLocationPermission(){
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mView.getLogger().println("onRequestPermissionsResult - Fragment");
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (
                            (ContextCompat.checkSelfPermission(context as BaseActivity, Manifest.permission.ACCESS_FINE_LOCATION).equals(PackageManager.PERMISSION_GRANTED)) &&
                            (ContextCompat.checkSelfPermission(context as BaseActivity, Manifest.permission.ACCESS_COARSE_LOCATION).equals(PackageManager.PERMISSION_GRANTED))

                    ) {
                        mView.getToaster().printToast(context, "Location permission granted");
                        mView.displayPrayerTime();
                    }
                } else {
                    mView.getToaster().printToast(context, "Location permission not granted");
                }
            }
        }
    }
}