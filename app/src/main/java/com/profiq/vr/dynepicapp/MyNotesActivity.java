package com.profiq.vr.dynepicapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dynepic.ppsdk_android.PPManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

public class MyNotesActivity extends Fragment {

    private PPManager manager;
    private PPManager.UserData userData;
    private ListView notesList;

    private ProgressBar progressBar;
    private TextView noNotesAvailableText;
    private FragmentTransaction fragmentTransaction;

    private final static String key = "notes";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_notes, container, false);
    }

    @SuppressLint("CommitTransaction")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        manager = PPManager.getInstance();
        notesList = Objects.requireNonNull(getView()).findViewById(R.id.notesList);
        userData = manager.getUserData();
        fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        getView().findViewById(R.id.fab).setOnClickListener(v -> startActivityForResult(new Intent(getContext(), AddNoteActivity.class), 1));
        progressBar = getView().findViewById(R.id.notes_progress_bar);
        noNotesAvailableText = getView().findViewById(R.id.noNotesAvailable);

        manager.data().read(userData.myData(), "notesCount", (JsonObject data, String e) -> {
            if (e != null) {
                JsonObject jo = new JsonObject();
                jo.addProperty("count", 0);
                manager.data().write(userData.myData(), "notesCount", jo, (JsonObject data2, String e2) -> {
                });
            }
        });

        manager.data().read(userData.myData(), key, (JsonObject data, String e) -> {
            if (e == null) {
                JsonArray ja = data != null ? data.getAsJsonArray(key) : null;
                ArrayList<NoteObject> notes = new Gson().fromJson(ja, new TypeToken<ArrayList<NoteObject>>() {
                }.getType());
                if ((notes != null ? notes.size() : 0) < 1) {
                    progressBar.setVisibility(View.GONE);
                    noNotesAvailableText.setVisibility(View.VISIBLE);
                } else {
                    noNotesAvailableText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    notesList.setAdapter(new NotesAdapter(Objects.requireNonNull(getContext()), notes, this, fragmentTransaction));
                }
            } else {
                progressBar.setVisibility(View.GONE);
                noNotesAvailableText.setVisibility(View.VISIBLE);
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            fragmentTransaction.detach(this).attach(this).commit();
        }
    }
}
