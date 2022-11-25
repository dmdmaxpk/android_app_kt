package com.dmdmax.goonj.controllers;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class VideoDownloadController {

    public String startDownloadAndSave(Context context, String url, String fileName) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Selected video is being downloaded");
        request.allowScanningByMediaScanner();
        request.setTitle(fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);

        File mediaFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        if(mediaFile.exists()) {
            Toast.makeText(context, "File already exist", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Downloading started", Toast.LENGTH_SHORT).show();
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName); //To Store file in External Public Directory use "setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)"
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
        return mediaFile.getPath();
    }

    public void insertImg(int id , Bitmap img ) {
        byte[] data = getBitmapAsByteArray(img);

    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 0, outputStream);
        return outputStream.toByteArray();
    }
}
