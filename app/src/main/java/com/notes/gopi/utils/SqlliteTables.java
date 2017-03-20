package com.notes.gopi.utils;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by gopikrishna on 12/11/16.
 */

public class SqlliteTables {

    public static class NotesListingTable {

        public static final String TABLE_NAME = "notes_listing";
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String UPDATED_ON = "updated_on";
        public static final String CREATED_ON = "created_on";
        public static final String IS_DELETED = "is_deleted";


        public static void createTable(SQLiteDatabase database) {
            String CREATE_DIRECTORY_TABLE =
                    "CREATE TABLE " +
                            TABLE_NAME + "(" +
                            ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                            TITLE + " TEXT," +
                            DESCRIPTION + " TEXT," +
                            UPDATED_ON + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            CREATED_ON + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            IS_DELETED + " INTEGER DEFAULT 0" +
                            ")";
            database.execSQL(CREATE_DIRECTORY_TABLE);
        }

        public static void dropTable(SQLiteDatabase database) {
            String DROP_DIRECTORY_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
            database.execSQL(DROP_DIRECTORY_TABLE);
        }
    }
}
