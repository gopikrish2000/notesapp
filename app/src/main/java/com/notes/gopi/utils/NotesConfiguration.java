package com.notes.gopi.utils;

/**
 * Created by gopikrishna on 15/11/16.
 */
public class NotesConfiguration {

    private boolean isAutoSaveEnabled = true;
    private static NotesConfiguration ourInstance = new NotesConfiguration();

    public static NotesConfiguration getInstance() {
        return ourInstance;
    }

    private NotesConfiguration() {
    }

    public boolean isAutoSaveEnabled() {
        return isAutoSaveEnabled;
    }
}
