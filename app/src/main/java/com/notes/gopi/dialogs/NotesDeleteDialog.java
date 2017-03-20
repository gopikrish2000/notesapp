package com.notes.gopi.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.notes.gopi.R;

import rx.subjects.PublishSubject;

import static com.notes.gopi.utils.CommonUtils.dismissDialog;

/**
 * Created by gopikrishna on 12/11/16.
 */

public class NotesDeleteDialog {

    private PublishSubject<Boolean> okClickSubject;

    public PublishSubject<Boolean> createDeleteItemAlertDialog(Activity context) {
        okClickSubject = PublishSubject.create();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(context.getResources().getDrawable(R.drawable.ic_action_warning));
        alertDialog.setTitle(R.string.delete_dialog);
        alertDialog.setMessage(R.string.delete_message);
        alertDialog.setPositiveButton(R.string.ok, (DialogInterface dialogInterface, int which) -> {
            okClickSubject.onNext(true);
            dismissDialog(dialogInterface);
        });
        alertDialog.setNegativeButton(R.string.cancel, (DialogInterface dialogInterface, int which) -> {
            dismissDialog(dialogInterface);
        });
        alertDialog.show();
        return okClickSubject;
    }
}
