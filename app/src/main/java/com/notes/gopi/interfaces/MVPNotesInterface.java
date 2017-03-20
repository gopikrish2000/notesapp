package com.notes.gopi.interfaces;

import com.notes.gopi.domains.NotesItem;

import java.util.List;

/**
 * Created by gopikrishna on 10/11/16.
 */

public interface MVPNotesInterface {

    void showProgress();

    void hideProgress();

    void showAllNotes(List<NotesItem> notesItems);

    void showAllNotes(List<NotesItem> notesItems, boolean isFromSearchFlow);

    void removeNotesOfPosition(int index);
}
