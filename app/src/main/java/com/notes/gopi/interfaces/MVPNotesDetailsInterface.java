package com.notes.gopi.interfaces;

import com.notes.gopi.domains.NotesItem;

/**
 * Created by gopikrishna on 13/11/16.
 */

public interface MVPNotesDetailsInterface {

    void setHeaderTitle(String title);

    void updateUI(boolean isEditFlow);

    void returnToListingPage(String type, NotesItem notesItem);
}
