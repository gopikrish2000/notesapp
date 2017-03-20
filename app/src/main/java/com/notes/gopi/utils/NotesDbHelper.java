package com.notes.gopi.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gopikrishna on 12/11/16.
 */

public class NotesDbHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "notes.db";
    private static final int DB_VERSION = 1;

    public NotesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SqlliteTables.NotesListingTable.createTable(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SqlliteTables.NotesListingTable.dropTable(db);
        SqlliteTables.NotesListingTable.createTable(db);
    }
}
