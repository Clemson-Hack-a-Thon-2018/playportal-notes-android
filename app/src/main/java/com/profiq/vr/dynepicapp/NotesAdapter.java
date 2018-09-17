package com.profiq.vr.dynepicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.dynepic.ppsdk_android.PPManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class NotesAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context mContext;
    private ArrayList<NoteObject> notesList;
    private PPManager manager;
    private PopupMenu pm;
    private PPManager.UserData userData;
    private String key = "notes";
    private Fragment parentFragment;
    private FragmentTransaction fragmentTransaction;

    NotesAdapter(Context context, ArrayList<NoteObject> notes, Fragment parentFragment, FragmentTransaction fragmentTransaction) {
        this.mContext = context;
        this.notesList = notes;
        this.parentFragment = parentFragment;
        this.fragmentTransaction = fragmentTransaction;
        this.manager = PPManager.getInstance();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.userData = manager.getUserData();
    }

    @Override
    public int getCount() {
        return this.notesList.size();
    }

    @Override
    public NoteObject getItem(int position) {
        return this.notesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return notesList.get(position).getItemID();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.note_item, null);

        NoteObject selectedNote = notesList.get(position);

        Button button = convertView.findViewById(R.id.note_menu_button);
        button.setOnClickListener(view -> {

            pm = new PopupMenu(mContext, view);
            pm.getMenu().add(1, 1, 1, "Delete");
            pm.setOnMenuItemClickListener(item -> {
                manager.data().read(userData.myData(), key, (JsonObject data, String e) -> {
                    if (e == null) {
                        JsonArray ja = data != null ? data.getAsJsonArray("notes") : null;
                        ArrayList<NoteObject> notes = new Gson().fromJson(ja, new TypeToken<ArrayList<NoteObject>>() {
                        }.getType());
                        ArrayList<NoteObject> notesFiltered = new ArrayList<>();
                        if (notes != null) {
                            for (NoteObject note : notes) {
                                if (note.getItemID() != selectedNote.getItemID()) {
                                    notesFiltered.add(note);
                                }
                            }
                        }
                        JsonObject jo = new JsonObject();
                        JsonElement element = new Gson().toJsonTree(notesFiltered, new TypeToken<ArrayList<NoteObject>>() {
                        }.getType());
                        jo.add("notes", element.getAsJsonArray());
                        manager.data().write(userData.myData(), key, jo, (JsonObject data2, String error2) -> {
                            if (error2 == null) {
                                Toast.makeText(mContext, "Note deleted", Toast.LENGTH_SHORT).show();
                                fragmentTransaction.detach(parentFragment);
                                fragmentTransaction.attach(parentFragment);
                                fragmentTransaction.commit();
                            }
                        });
                    }
                });
                return true;
            });
            pm.show();
        });

        final TextView noteTitle = convertView.findViewById(R.id.note_title);
        String titleText = selectedNote.getItemTitle();
        noteTitle.setText(titleText);

        convertView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ShowNoteActivity.class);

            intent.putExtra("id", selectedNote.getItemID());
            intent.putExtra("title", selectedNote.getItemTitle());
            intent.putExtra("body", selectedNote.getItemBody());

            mContext.startActivity(intent);
        });

        return convertView;
    }
}
