package com.profiq.vr.dynepicapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dynepic.ppsdk_android.PPManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class AddNoteActivity extends AppCompatActivity {

    private PPManager manager;
    private PPManager.UserData userData;
    private EditText mTitle;
    private EditText mBody;
    private int count;

    private String key = "notes";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note_form);

        manager = PPManager.getInstance();
        userData = manager.getUserData();

        mTitle = findViewById(R.id.titleInput);
        mBody = findViewById(R.id.bodyInput);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.6));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -200;

        getWindow().setAttributes(params);
    }

    public void onButtonClickNote(View v) {
        switch (v.getId()) {
            case R.id.addNoteButton: {

                Toast.makeText(this, "Saving note to Cloud", Toast.LENGTH_SHORT).show();

                manager.data().read(userData.myData(), "notesCount", (JsonObject data, String e) -> {
                    count = Integer.parseInt(data != null ? data.get("count").toString() : null);
                });

                manager.data().read(userData.myData(), key, (JsonObject data, String e) -> {
                    ArrayList<NoteObject> notes;
                    if (e == null) {
                        JsonArray ja = data != null ? data.getAsJsonArray(key) : null;
                        notes = new Gson().fromJson(ja, new TypeToken<ArrayList<NoteObject>>() {
                        }.getType());
                    } else {
                        notes = new ArrayList<>();
                    }
                    if (notes != null) {
                        notes.add(new NoteObject(count, mTitle.getText().toString(), mBody.getText().toString()));
                    }
                    JsonObject jo = new JsonObject();
                    JsonElement element = new Gson().toJsonTree(notes, new TypeToken<ArrayList<NoteObject>>() {
                    }.getType());
                    jo.add(key, element.getAsJsonArray());
                    manager.data().write(userData.myData(), key, jo, (JsonObject data2, String error2) -> {
                        if (error2 == null) {
                            Toast.makeText(this, "Note saved to Cloud", Toast.LENGTH_SHORT).show();
                            manager.data().read(userData.myData(), "notesCount", (JsonObject data3, String e3) -> {
                                JsonObject jo2 = new JsonObject();
                                jo2.addProperty("count", count + 1);
                                manager.data().write(userData.myData(), "notesCount", jo2, (JsonObject data4, String e4) -> {
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("result", 1);
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                });
                            });
                        } else {
                            Log.e("Data write error:", error2);
                        }
                    });
                });
                break;
            }

            case R.id.discardNote: {
                finish();
                break;
            }
        }
    }
}
