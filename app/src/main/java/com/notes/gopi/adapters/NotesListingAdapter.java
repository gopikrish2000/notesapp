package com.notes.gopi.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.notes.gopi.R;
import com.notes.gopi.domains.NotesItem;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.subjects.PublishSubject;

import static com.notes.gopi.utils.CommonUtils.getDateTimeFromString;
import static com.notes.gopi.utils.CommonUtils.getFormattedDate;
import static com.notes.gopi.utils.CommonUtils.isNullOrEmpty;

/**
 * Created by gopikrishna on 11/11/16.
 */

public class NotesListingAdapter extends RecyclerView.Adapter<NotesListingAdapter.NotesViewHolder> {

    private List<NotesItem> notesItemList;
    private PublishSubject<Pair<NotesItem, Integer>> itemClickSubject;
    private PublishSubject<Pair<NotesItem, Integer>> itemLongClickSubject;

    public NotesListingAdapter(List<NotesItem> notesItemList) {
        this.notesItemList = notesItemList;
        itemClickSubject = PublishSubject.create();
        itemLongClickSubject = PublishSubject.create();
    }

    public PublishSubject<Pair<NotesItem, Integer>> getItemClickSubject() {
        return itemClickSubject;
    }

    public PublishSubject<Pair<NotesItem, Integer>> getItemLongClickSubject() {
        return itemLongClickSubject;
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_listing_item, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, int position) {
        NotesItem notesItem = notesItemList.get(position);
        holder.itemTitle.setText(notesItem.getTitle());
        holder.itemDescription.setText(getFormatted(notesItem.getDescription()));
        String createdDateString = notesItem.getUpdatedDate();
        String formattedString = getFormattedDate(getDateTimeFromString(createdDateString));
        holder.itemDate.setText(formattedString);

        RxView.longClicks(holder.parent).throttleFirst(1, TimeUnit.SECONDS).subscribe(s -> {
            itemLongClickSubject.onNext(Pair.create(notesItem, holder.getAdapterPosition()));
        });

        RxView.clicks(holder.parent).throttleFirst(1, TimeUnit.SECONDS).subscribe(s -> {
            itemClickSubject.onNext(Pair.create(notesItem, holder.getAdapterPosition()));
        });
    }

    private String getFormatted(String description) {
        if (isNullOrEmpty(description)) {
            return "";
        }
        return description.replaceAll("[\\t|\\n|\\r]", " ");
    }

    @Override
    public int getItemCount() {
        return notesItemList.size();
    }

    class NotesViewHolder extends ViewHolder {
        private TextView itemTitle;
        private TextView itemDate;
        private TextView itemDescription;
        private View parent;

        NotesViewHolder(View view) {
            super(view);
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemDate = (TextView) view.findViewById(R.id.item_date);
            itemDescription = (TextView) view.findViewById(R.id.item_description);
            parent = view;
        }
    }
}
