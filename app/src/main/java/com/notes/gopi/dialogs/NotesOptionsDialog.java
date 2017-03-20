package com.notes.gopi.dialogs;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.notes.gopi.R;
import com.notes.gopi.domains.NotesItem;

import rx.subjects.PublishSubject;

import static com.notes.gopi.utils.CommonUtils.dismissDialog;

/**
 * Created by gopikrishna on 12/11/16.
 */

public class NotesOptionsDialog {

    private PublishSubject<Boolean> deleteClickSubject;

    public PublishSubject<Boolean> createOptionsDialog(Activity context, NotesItem notesItem) {
        String names[] = {"Delete", "Get Info"};
        View convertView = (View) context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        deleteClickSubject = PublishSubject.create();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(convertView);
        alertDialog.setTitle(notesItem.getTitle());
        ListView lv = (ListView) convertView.findViewById(R.id.dialog_listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, names);
        lv.setAdapter(adapter);
        AlertDialog dialog = alertDialog.show();
        lv.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            if (position == 0) {
                dismissDialog(dialog);
                deleteClickSubject.onNext(true);
            } else if (position == 1) {
                Toast.makeText(context, "This Note Last Updated at " + notesItem.getUpdatedDate(), Toast.LENGTH_SHORT).show();
                dismissDialog(dialog);
            }
        });
        return deleteClickSubject;
    }
}
