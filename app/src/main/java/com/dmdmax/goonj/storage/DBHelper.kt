package com.dmdmax.goonj.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dmdmax.goonj.network.responses.Anchor
import com.dmdmax.goonj.network.responses.Program
import com.dmdmax.goonj.network.responses.Topic
import com.dmdmax.goonj.utility.Logger
import java.net.URLEncoder
import java.util.*

open class DBHelper: SQLiteOpenHelper {

    companion object {
        const val DATABASE_NAME = "GoonjDatabase.db"
        private const val TABLE_FOLLOWING = "tableUserFollowing"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_TAG = "tag"

        object Tags {
            val TAG_TOPIC = 0
            val TAG_ANCHOR = 1
            val TAG_PROGRAM = 2
            val TAG_CATEGORY = 3
        }
    }

    constructor(context: Context) : super(context, DBHelper.DATABASE_NAME, null, 1) {

    }

    override fun onCreate(db: SQLiteDatabase?) {
        try {
            db!!.execSQL("CREATE TABLE $TABLE_FOLLOWING($COLUMN_ID VARCHAR(10) PRIMARY KEY, $COLUMN_TITLE VARCHAR(150), $COLUMN_TAG TINYINT)")
        } catch (e: Exception) {
            Logger.println("DB Exception: " + e.message)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_FOLLOWING")
        onCreate(db)
    }

    fun insertUserFollowing(title: String?, tag: Int) {
        val db = writableDatabase
        try {
            val contentValues = ContentValues()
            contentValues.put(COLUMN_TITLE, title)
            contentValues.put(COLUMN_TAG, tag)
            db.insert(TABLE_FOLLOWING, null, contentValues)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }


    fun deleteUserFollowing(title: String): Int {
        val db = writableDatabase
        var effectedRows = 0
        try {
            effectedRows = db.delete(TABLE_FOLLOWING, COLUMN_TITLE + " = ? ", arrayOf(title))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return effectedRows
    }

    fun getUserFollowings(tag: Int): ArrayList<String>? {
        val db = readableDatabase
        val list = ArrayList<String>()
        try {
            val cur = db.rawQuery(
                "SELECT " + COLUMN_TITLE + " from " + TABLE_FOLLOWING + " WHERE " + COLUMN_TAG + "=" + tag,
                null
            )
            while (cur.moveToNext()) {
                list.add(cur.getString(cur.getColumnIndex(COLUMN_TITLE)))
            }
            cur.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return list
    }

    fun getAnchors(): List<Anchor>? {
        val db = readableDatabase
        val list: ArrayList<Anchor> = ArrayList<Anchor>()
        try {
            val cur = db.rawQuery(
                "SELECT " + COLUMN_TITLE + " from " + TABLE_FOLLOWING + " WHERE " + COLUMN_TAG + "=" + Tags.TAG_ANCHOR,
                null
            )
            while (cur.moveToNext()) {
                list.add(Anchor(cur.getString(cur.getColumnIndex(COLUMN_TITLE))))
            }
            cur.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return list
    }

    fun getTopics(): List<Topic>? {
        val db = readableDatabase
        val list: ArrayList<Topic> = ArrayList<Topic>()
        try {
            val cur = db.rawQuery(
                "SELECT " + COLUMN_TITLE + " from " + TABLE_FOLLOWING + " WHERE " + COLUMN_TAG + "=" + Tags.TAG_TOPIC,
                null
            )
            while (cur.moveToNext()) {
                list.add(Topic(cur.getString(cur.getColumnIndex(COLUMN_TITLE))))
            }
            cur.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return list
    }

    fun getPrograms(): List<Program>? {
        val db = readableDatabase
        val list: ArrayList<Program> = ArrayList<Program>()
        try {
            val cur = db.rawQuery(
                "SELECT " + COLUMN_TITLE + " from " + TABLE_FOLLOWING + " WHERE " + COLUMN_TAG + "=" + Tags.TAG_PROGRAM,
                null
            )
            while (cur.moveToNext()) {
                list.add(Program(cur.getString(cur.getColumnIndex(COLUMN_TITLE))))
            }
            cur.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return list
    }

    fun isAnchorTopicProgramFollowed(title: String): Boolean {
        val db = readableDatabase
        try {
            val cur = db.rawQuery(
                "SELECT * from " + TABLE_FOLLOWING + " WHERE " + COLUMN_TITLE + "= '" + title + "'",
                null
            )
            return if (cur != null && cur.count > 0) {
                cur.close()
                true
            } else {
                cur!!.close()
                false
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return false
    }

    fun getUserFollowingCommaSeparatedString(): String? {
        return if (getRows() == 0) "" else "anchor=" + getUserFollowingString(Tags.TAG_ANCHOR) + "&topics=" + getUserFollowingString(
            Tags.TAG_TOPIC
        ) + "&program=" + getUserFollowingString(Tags.TAG_PROGRAM)
    }

    private fun getRows(): Int {
        val db = readableDatabase
        try {
            val cur = db.rawQuery("SELECT COUNT(*) from " + TABLE_FOLLOWING, null)
            if (cur != null && cur.moveToNext()) {
                val rows = cur.getInt(0)
                cur.close()
                return rows
            }
            return 0
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return 0
    }

    fun getUserFollowingString(tag: Int): String {
        val db = readableDatabase
        var allFollowings = ""
        try {
            val cur = db.rawQuery(
                "SELECT " + COLUMN_TITLE + " from " + TABLE_FOLLOWING + " WHERE " + COLUMN_TAG + "=" + tag,
                null
            )
            while (cur.moveToNext()) {
                allFollowings =
                    if (!cur.isLast) allFollowings + cur.getString(cur.getColumnIndex(COLUMN_TITLE)) + "," else allFollowings + cur.getString(
                        cur.getColumnIndex(
                            COLUMN_TITLE
                        )
                    )
            }
            cur.close()
            return URLEncoder.encode(allFollowings, "utf-8")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return ""
    }
}