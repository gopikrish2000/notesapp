package com.notes.gopi.activities;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.notes.gopi.utils.NotesDbHelper;

/**
 * Created by gopikrishna on 12/11/16.
 */

public class NotesApplication extends Application {

    private NotesDbHelper notesDbHelper;
    private static NotesApplication notesApplication;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        notesApplication = this;
        initializeDb();
    }

    public static NotesApplication getInstance() {
        return notesApplication;
    }

    private void initializeDb() {
        notesDbHelper = new NotesDbHelper(this);
    }

    public SQLiteDatabase getReadableDb() {
        if (sqLiteDatabase == null) {
            sqLiteDatabase = notesDbHelper.getReadableDatabase();
        }
        return sqLiteDatabase;
    }

    public SQLiteDatabase getWritableDB() {
        if (sqLiteDatabase == null) {
            sqLiteDatabase = notesDbHelper.getWritableDatabase();
        } else if (!sqLiteDatabase.isOpen()) {
            sqLiteDatabase = notesDbHelper.getWritableDatabase();
        }
        return sqLiteDatabase;
    }

}
