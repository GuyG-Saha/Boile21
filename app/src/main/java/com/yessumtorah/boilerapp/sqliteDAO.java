package com.yessumtorah.boilerapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class sqliteDAO extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_DATE_TITLE + " TEXT," +
                    FeedEntry.COLUMN_LENGTH_TITLE + " INT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "mySessions.db";

    public sqliteDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Sessions";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_DATE_TITLE = "date";
        public static final String COLUMN_LENGTH_TITLE = "length";
    }
}
