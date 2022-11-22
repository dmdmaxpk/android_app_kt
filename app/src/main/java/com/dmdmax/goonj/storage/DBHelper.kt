package com.dmdmax.goonj.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import com.dmdmax.goonj.models.OfflineVideos
import com.dmdmax.goonj.utility.Logger
import java.io.ByteArrayOutputStream
import java.io.Console
import java.util.*


open class DBHelper: SQLiteOpenHelper {

    companion object {
        const val DATABASE_NAME = "GoonjDatabase.db"
        private const val TABLE_OFFLINE_VIDEOS = "tableOfflineVidoes"

        private const val COLUMN_OFFLINE_VIDEO_ID = "videoId";
        private const val COLUMN_OFFLINE_VIDEO_THUMBNAIL = "thumbnail"
        private const val COLUMN_OFFLINE_VIDEO_TITLE = "title"
        private const val COLUMN_OFFLINE_VIDEO_DESCRIPTION = "description"
        private const val COLUMN_OFFLINE_VIDEO_LOCAL_PATH = "localPath"
    }

    constructor(context: Context) : super(context, DBHelper.DATABASE_NAME, null, 1) {

    }

    override fun onCreate(db: SQLiteDatabase?) {
        try {
            db!!.execSQL("CREATE TABLE $TABLE_OFFLINE_VIDEOS(" +
                    "$COLUMN_OFFLINE_VIDEO_ID TEXT PRIMARY KEY, " +
                    "$COLUMN_OFFLINE_VIDEO_THUMBNAIL BLOB, " +
                    "$COLUMN_OFFLINE_VIDEO_TITLE VARCHAR(255), " +
                    "$COLUMN_OFFLINE_VIDEO_DESCRIPTION TEXT, " +
                    "$COLUMN_OFFLINE_VIDEO_LOCAL_PATH TEXT" +
                    ")")
            //db!!.execSQL("CREATE TABLE $TABLE_FOLLOWING($COLUMN_ID VARCHAR(10) PRIMARY KEY, $COLUMN_TITLE VARCHAR(150), $COLUMN_TAG TINYINT)")
        } catch (e: Exception) {
            Logger.println("DB Exception: " + e.message)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_OFFLINE_VIDEOS")
        onCreate(db)
    }

    @Throws(SQLiteException::class)
    fun addEntry(id: String, thumbnail: Bitmap?, title: String, desc: String, localPath: String) {
        val db = writableDatabase

        val cv = ContentValues()
        cv.put(COLUMN_OFFLINE_VIDEO_ID, id)

        if(thumbnail != null)
            cv.put(COLUMN_OFFLINE_VIDEO_THUMBNAIL, getBytes(thumbnail))

        cv.put(COLUMN_OFFLINE_VIDEO_TITLE, title)
        cv.put(COLUMN_OFFLINE_VIDEO_DESCRIPTION, desc)
        cv.put(COLUMN_OFFLINE_VIDEO_LOCAL_PATH, localPath)
        db.insert(TABLE_OFFLINE_VIDEOS, null, cv)
        Logger.println("Record Added");
        Logger.println("$id - $title - $desc - $localPath");
    }

    fun getOfflineVideos(): ArrayList<OfflineVideos>? {
        val db = readableDatabase
        val list = ArrayList<OfflineVideos>()
        try {
            val cursor = db.rawQuery("SELECT * FROM $TABLE_OFFLINE_VIDEOS", null);
            while (cursor.moveToNext()) {
                val obj = OfflineVideos();
                obj.id = cursor.getString(0);
                obj.image = cursor.getBlob(1);
                obj.title = cursor.getString(2);
                obj.desc = cursor.getString(3);
                obj.localPath = cursor.getString(4);
                list.add(obj);
            }
            cursor.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return list
    }

    open fun getBytes(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.WEBP, 0, stream)
        return stream.toByteArray()
    }

    open fun getImage(image: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }
}