package com.notes.gopi.presenters;

import android.app.Activity;

import com.notes.gopi.R;
import com.notes.gopi.db.DatabaseManager;
import com.notes.gopi.dialogs.NotesDeleteDialog;
import com.notes.gopi.domains.NotesItem;
import com.notes.gopi.intdefs.NotesActionType;
import com.notes.gopi.interfaces.BasePresenterInterface;
import com.notes.gopi.interfaces.MVPNotesDetailsInterface;

import rx.subscriptions.CompositeSubscription;

import static com.notes.gopi.utils.CommonUtils.doUnsubscribe;
import static com.notes.gopi.utils.CommonUtils.isNullOrEmpty;
import static com.notes.gopi.utils.RxApiUtil.build;

/**
 * Created by gopikrishna on 11/11/16.
 */

public class NotesDetailsPresenter implements BasePresenterInterface {

    private Activity context;
    private MVPNotesDetailsInterface mvpNotesDetailsInterface;
    private boolean isSubscribed;
    private CompositeSubscription lifeCycle;
    private boolean isAddFlow;
    private NotesItem notesItem;
    private boolean isSearchFlow;

    public NotesDetailsPresenter(MVPNotesDetailsInterface mvpNotesInterface, Activity context, boolean isAddFlow, NotesItem notesItem, int itemPosition, boolean isSearchFlow) {
        this.mvpNotesDetailsInterface = mvpNotesInterface;
        this.context = context;
        this.isSubscribed = true;
        lifeCycle = new CompositeSubscription();
        this.isAddFlow = isAddFlow;
        this.notesItem = notesItem;
        this.isSearchFlow = isSearchFlow;
    }

    public void onDoneClick(String description, String titleText) {
        if (isNullOrEmpty(description)) {
            performDeleteAction();  // if no content delete the existing item.
        } else {
            titleText = getUpdatedTitleText(description, titleText);
            if (isAddFlow) { // create new notes with the content.
                NotesItem notesItem = new NotesItem();
                notesItem.setTitle(titleText);
                notesItem.setDescription(description);
                lifeCycle.add(build(DatabaseManager.insertNotes(notesItem)).subscribe(insertedId -> {
                    if (insertedId < 0) {
                        return;
                    }
                    lifeCycle.add(build(DatabaseManager.getNoteById(insertedId)).subscribe(noteFromDb -> {
                        mvpNotesDetailsInterface.returnToListingPage(NotesActionType.ADD, noteFromDb);
                    }));
                }));
            } else {   // update the existing notes with updated content.
                notesItem.setTitle(titleText);
                notesItem.setDescription(description);
                String actionType = isSearchFlow ? NotesActionType.SEARCH : NotesActionType.UPDATE;
                lifeCycle.add(build(DatabaseManager.updateNotes(notesItem)).subscribe(s -> {
                    mvpNotesDetailsInterface.returnToListingPage(actionType, notesItem);
                }));
            }
        }
    }

    public void onDeleteOptionMenuClicked() {
        NotesDeleteDialog deleteDialog = new NotesDeleteDialog();
        lifeCycle.add(deleteDialog.createDeleteItemAlertDialog(context).subscribe(bool -> {
            performDeleteAction();
        }));
    }

    // Replace all empty lines with Spaces. So that more content can be shown in Listing Page.
    private String getUpdatedTitleText(String description, String titleText) {
        if (!isNullOrEmpty(titleText)) {
            return titleText.trim();
        }
        String[] split = description.split("[\\r\\n]+");
        if (isNullOrEmpty(split)) {
            return context.getResources().getString(R.string.title);
        }
        for (String line : split) {
            if (!isNullOrEmpty(line)) {
                titleText = line.trim();
                break;
            }
        }
        return titleText;
    }

    public boolean performDeleteAction() {
        int id = (notesItem == null) ? 0 : notesItem.getId();
        if (isAddFlow || id == 0) {   // In AddFlow item is not created yet in Database. So no need to delete. Just forward to Listing page.
            mvpNotesDetailsInterface.returnToListingPage(NotesActionType.CLOSE, null);
            return true;
        }
        //Item already exists delete it.
        lifeCycle.add(build(DatabaseManager.softDeleteNotes(id)).subscribe(isSuccess -> {
            if (isSuccess) {
                mvpNotesDetailsInterface.returnToListingPage(NotesActionType.DELETE, null);
            }
        }));
        return false;
    }

    public void onEditButtonClick() {
        mvpNotesDetailsInterface.updateUI(true);
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    @Override
    public void unSubscribe() {
        isSubscribed = false;
        doUnsubscribe(lifeCycle);
    }
}
