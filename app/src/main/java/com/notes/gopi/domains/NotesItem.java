package com.notes.gopi.domains;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gopikrishna on 11/11/16.
 */

public class NotesItem implements Parcelable {

    private int id;
    private String title;
    private String description;
    private String createdDate;
    private String updatedDate;
    private int isDeleted;


    public NotesItem(String title, String description, String createdDate) {
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
    }

    public NotesItem(int id, String title, String description, String createdDate, String updatedDate, int isDeleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.isDeleted = isDeleted;
    }

    public NotesItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.createdDate);
        dest.writeString(this.updatedDate);
        dest.writeInt(this.isDeleted);
    }

    protected NotesItem(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.createdDate = in.readString();
        this.updatedDate = in.readString();
        this.isDeleted = in.readInt();
    }

    public static final Creator<NotesItem> CREATOR = new Creator<NotesItem>() {
        public NotesItem createFromParcel(Parcel source) {
            return new NotesItem(source);
        }

        public NotesItem[] newArray(int size) {
            return new NotesItem[size];
        }
    };

    @Override
    public String toString() {
        return "NotesItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
