package com.dmdmax.goonj.controllers;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.dmdmax.goonj.utility.Toaster;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VideoDownloadController {

    public String startDownloadAndSave(Context context, String url, String fileName) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Selected video is being downloaded");
        request.allowScanningByMediaScanner();
        request.setTitle(fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);

        File mediaFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        /*if(mediaFile.exists()) {
            Toast.makeText(context, "File already exist", Toast.LENGTH_SHORT).show();
        }else*/ {
            Toast.makeText(context, "Downloading started", Toast.LENGTH_SHORT).show();

            //Set the local destination for the downloaded file to a path within the application's external files directory
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName); //To Store file in External Public Directory use "setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)"
            //request.setDestinationInExternalFilesDir(context, FOLDER_NAME, fileName);
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
        return mediaFile.getPath();
    }

    public boolean doesAlreadyExist(Context context, String url, String fileName) {
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName).exists();
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
