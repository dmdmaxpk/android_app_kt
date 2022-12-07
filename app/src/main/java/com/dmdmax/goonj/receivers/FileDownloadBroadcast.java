package com.dmdmax.goonj.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.dmdmax.goonj.storage.DBHelper;

public class FileDownloadBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Fetching the download id received with the broadcast
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        new DBHelper(context).updateStatus(id);
        Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
    }
}
