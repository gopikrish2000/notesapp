package com.notes.gopi.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.notes.gopi.activities.NotesApplication;
import com.notes.gopi.domains.NotesItem;
import com.notes.gopi.utils.SqlliteTables.NotesListingTable;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.notes.gopi.utils.CommonUtils.getCurrentDateTimeForDb;
import static com.notes.gopi.utils.CommonUtils.isNullOrEmpty;

/**
 * Created by gopikrishna on 12/11/16.
 */

public class DatabaseManager {

    public static Observable<Long> insertNotes(NotesItem notes) {
        return Observable.create(subscriber -> {
            long result;
            try {
                String currentDateTimeForDb = getCurrentDateTimeForDb();
                ContentValues contentValues = new ContentValues();
                contentValues.put(NotesListingTable.TITLE, notes.getTitle());
                contentValues.put(NotesListingTable.DESCRIPTION, notes.getDescription());
                contentValues.put(NotesListingTable.UPDATED_ON, currentDateTimeForDb);
                contentValues.put(NotesListingTable.CREATED_ON, currentDateTimeForDb);
                result = NotesApplication.getInstance().getWritableDB().insert(NotesListingTable.TABLE_NAME, null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("exception in insertion", e.getLocalizedMessage());
                result = -1;
            }
            subscriber.onNext(result);
        });
    }

    public static Observable<Boolean> updateNotes(NotesItem notes) {
        return Observable.create(subscriber -> {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(NotesListingTable.TITLE, notes.getTitle());
                contentValues.put(NotesListingTable.DESCRIPTION, notes.getDescription());
                contentValues.put(NotesListingTable.UPDATED_ON, getCurrentDateTimeForDb());

                int i = NotesApplication.getInstance().getWritableDB().update(NotesListingTable.TABLE_NAME,
                        contentValues,
                        NotesListingTable.ID + " = ?",
                        new String[]{String.valueOf(notes.getId())});
                subscriber.onNext(i != -1);
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onNext(false);
            }
        });
    }

    public static boolean hardDeleteNotes(int id) {
        int i = NotesApplication.getInstance().getWritableDB().delete(NotesListingTable.TABLE_NAME, NotesListingTable.ID + " =?",
                new String[]{String.valueOf(id)});
        return i != -1;
    }

    public static Observable<Boolean> softDeleteNotes(int id) {
        return Observable.create(subscriber -> {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(NotesListingTable.IS_DELETED, "1");

                int i = NotesApplication.getInstance().getWritableDB().update(NotesListingTable.TABLE_NAME,
                        contentValues,
                        NotesListingTable.ID + " = ?",
                        new String[]{String.valueOf(id)});
                subscriber.onNext(i != -1);
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onNext(false);
            }
        });
    }

    public static Observable<List<NotesItem>> getAllNotes() {
        return Observable.create(subscriber -> {
            List<NotesItem> notesItems = new ArrayList<>();
            Cursor cursor = NotesApplication.getInstance().getReadableDb()
                    .rawQuery("SELECT * FROM " + NotesListingTable.TABLE_NAME + " WHERE " + NotesListingTable.IS_DELETED + " = ? ORDER BY " + NotesListingTable.UPDATED_ON + " DESC ", new String[]{"0"});
            try {
                while (cursor.moveToNext()) {
                    NotesItem notesItem = new NotesItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            cursor.getString(4), cursor.getInt(5));
                    notesItems.add(notesItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
            subscriber.onNext(notesItems);
        });
    }

    public static Observable<List<NotesItem>> getAllSearchedNotes(String searchTerm) {
        if (isNullOrEmpty(searchTerm)) {
            return Observable.just(new ArrayList<>());
        }
        return Observable.create(subscriber -> {
            List<NotesItem> notesItems = new ArrayList<>();
            String regexSearchTerm = "%" + searchTerm + "%";
            Cursor cursor = NotesApplication.getInstance().getReadableDb()
                    .rawQuery("SELECT * FROM " + NotesListingTable.TABLE_NAME + " WHERE " + NotesListingTable.IS_DELETED + " = ? AND (" + NotesListingTable.TITLE + " LIKE ?  OR "
                            + NotesListingTable.DESCRIPTION + " LIKE ? )"
                            + " ORDER BY " + NotesListingTable.UPDATED_ON + " DESC ", new String[]{"0", regexSearchTerm, regexSearchTerm});
            try {
                while (cursor.moveToNext()) {
                    NotesItem notesItem = new NotesItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            cursor.getString(4), cursor.getInt(5));
                    notesItems.add(notesItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
            System.out.println("notesItems in getAllSearchedNotes = " + notesItems);
            subscriber.onNext(notesItems);
        });
    }

    public static Observable<NotesItem> getNoteById(long id) {
        return Observable.create(subscriber -> {
            Cursor cursor = NotesApplication.getInstance().getReadableDb()
                    .rawQuery("SELECT * FROM " + NotesListingTable.TABLE_NAME + " WHERE " + NotesListingTable.ID + " = ? lIMIT 1", new String[]{id + ""});
            try {
                while (cursor.moveToNext()) {
                    NotesItem notesItem = new NotesItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            cursor.getString(4), cursor.getInt(5));
                    subscriber.onNext(notesItem);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
            subscriber.onNext(null);
        });
    }

}
