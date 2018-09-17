package com.profiq.vr.dynepicapp;

import android.support.annotation.NonNull;

public class NoteObject implements Comparable<NoteObject> {
    private int mID;
    private String mTitle;
    private String mBody;

    NoteObject(int id, String title, String body) {
        this.mID = id;
        this.mTitle = title;
        this.mBody = body;
        System.out.println("New note added ID: " + id);
    }

    public int getItemID() {
        return this.mID;
    }

    public String getItemTitle() {
        return this.mTitle;
    }

    public String getItemBody() {
        return this.mBody;
    }

    @Override
    public int compareTo(@NonNull NoteObject noteObject) {
        return Integer.compare(getItemID(), noteObject.getItemID());
    }
}
