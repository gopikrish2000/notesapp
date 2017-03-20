package com.notes.gopi.intdefs;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by gopikrishna on 13/11/16.
 */

@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {
        NotesActionType.ADD,
        NotesActionType.UPDATE,
        NotesActionType.DELETE,
        NotesActionType.CLOSE,
        NotesActionType.SEARCH
})
public @interface NotesActionType {
    String ADD = "ADD";
    String UPDATE = "UPDATE";
    String DELETE = "DELETE";
    String CLOSE = "CLOSE";
    String SEARCH = "SEARCH";
}