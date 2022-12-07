package com.dmdmax.goonj.controllers;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dmdmax.goonj.R;
import com.dmdmax.goonj.models.OfflineVideos;
import com.dmdmax.goonj.storage.DBHelper;
import com.dmdmax.goonj.utility.Logger;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class VideoDownloadController {

    public String startDownloadAndSave(Context context, String url, String fileName, OfflineVideos offlineVideo) {

        File mediaFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName.replaceAll("\\|", ""));
        if(mediaFile.exists()) {
            Toast.makeText(context, "File already exist", Toast.LENGTH_SHORT).show();
        }else {
            startDownload(context, url, mediaFile, offlineVideo);
        }
        return mediaFile.getPath();
    }

    public void startDownload(Context context, String url, File file, OfflineVideos offlineVideo) {
        offlineVideo.setLocalPath(file.getPath());

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                .setTitle(offlineVideo.getTitle())// Title of the Download Notification
                .setDescription("Downloading in progress...")// Description of the Download Notification
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true);// Set if download is allowed on roaming network

        DownloadManager downloadManager= (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        long id = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
        Toast.makeText(context, "Downloading started", Toast.LENGTH_SHORT).show();
        offlineVideo.setDownloadId(id);
        new DBHelper(context).addEntry(offlineVideo.getId(), null, offlineVideo.getTitle(), offlineVideo.getTitle(), offlineVideo.getLocalPath(), offlineVideo.getDownloadId());
        //context.registerReceiver(@Nullable BroadcastReceiver receiver,IntentFilter filter);











        /*FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context).setDownloadConcurrentLimit(3).build();

        Fetch fetch = Fetch.Impl.getInstance(fetchConfiguration);

        final Request request = new Request(url, fileName);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);

        fetch.enqueue(request, updatedRequest -> {
            Toast.makeText(context, "Downloading started", Toast.LENGTH_SHORT).show();
        }, error -> {
            Logger.Companion.println(error.toString());
            Toast.makeText(context, "Failed to download, try again", Toast.LENGTH_SHORT).show();
        });

        fetch.addListener(new FetchListener() {
            @Override
            public void onAdded(@NonNull Download download) {
                //Logger.Companion.println("onAdded");
            }

            @Override
            public void onQueued(@NonNull Download download, boolean b) {
                //Logger.Companion.println("onQueued");
            }

            @Override
            public void onWaitingNetwork(@NonNull Download download) {

            }

            @Override
            public void onCompleted(@NonNull Download download) {
                //Logger.Companion.println("onCompleted");
                downloadSuccess(1552);
            }

            @Override
            public void onError(@NonNull Download download, @NonNull Error error, @Nullable Throwable throwable) {
                Logger.Companion.println("onError");
            }

            @Override
            public void onDownloadBlockUpdated(@NonNull Download download, @NonNull DownloadBlock downloadBlock, int i) {

            }

            @Override
            public void onStarted(@NonNull Download download, @NonNull List<? extends DownloadBlock> list, int i) {
                //Logger.Companion.println("onStarted");
                startDownloadNotification(1552, context, fileName);
            }

            @Override
            public void onProgress(@NonNull Download download, long l, long l1) {

            }

            @Override
            public void onPaused(@NonNull Download download) {

            }

            @Override
            public void onResumed(@NonNull Download download) {

            }

            @Override
            public void onCancelled(@NonNull Download download) {

            }

            @Override
            public void onRemoved(@NonNull Download download) {

            }

            @Override
            public void onDeleted(@NonNull Download download) {

            }
        });*/
    }

    public void insertImg(int id , Bitmap img ) {
        byte[] data = getBitmapAsByteArray(img);

    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 0, outputStream);
        return outputStream.toByteArray();
    }

    public void downloadSuccess(int id) {
        /*mBuilder.setContentText("Download completed").setProgress(0,0,true);
        mNotifyManager.notify(id, mBuilder.build());
        Logger.Companion.println("Notified Success - "+ id);*/
    }

    public void startDownloadNotification(int id, Context context, String fileName) {
        //mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id + "");
        builder.setContentTitle(fileName)
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_download)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        notificationManager.notify(id, builder.build());

        // Do the job here that tracks the progress.
        // Usually, this should be in a
        // worker thread
        // To show progress, update PROGRESS_CURRENT and update the notification with:
        // builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        // notificationManager.notify(notificationId, builder.build());

        // When done, update the notification one more time to remove the progress bar
//        builder.setContentText("Download complete")
//                .setProgress(0,0,false);
//        notificationManager.notify(notificationId, builder.build());

        Logger.Companion.println("Notified - " +id);
    }
}
