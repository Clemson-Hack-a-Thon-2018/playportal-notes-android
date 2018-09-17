package com.profiq.vr.dynepicapp;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dynepic.ppsdk_android.PPManager;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserProfile extends Fragment implements View.OnClickListener {
    private PPManager manager;
    private PPManager.UserData userData;

    private int imagesToLoad = 0;
    private ProgressBar bar;
    private ImageView userPhoto;
    private ImageView coverImage;

    private static String key = "counter";
    private TextView text;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_user_profile, container, false);
        Button addButton = view.findViewById(R.id.add);
        Button subtractButton = view.findViewById(R.id.substract);
        addButton.setOnClickListener(this);
        subtractButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        manager = PPManager.getInstance();
        userData = manager.getUserData();
        Bundle bundle = this.getArguments();

        TextView userName = Objects.requireNonNull(getView()).findViewById(R.id.userName);
        TextView userCountry = getView().findViewById(R.id.country);
        TextView userHandle = getView().findViewById(R.id.userHandle);

        userPhoto = getView().findViewById(R.id.userPhoto);
        coverImage = getView().findViewById(R.id.cover);
        bar = getView().findViewById(R.id.progress_bar);

        userPhoto.setVisibility(View.INVISIBLE);
        coverImage.setVisibility(View.INVISIBLE);

        Button addButton = getView().findViewById(R.id.add);
        Button subtractButton = getView().findViewById(R.id.substract);

        text = getView().findViewById(R.id.textAdd);

        //Access from friend list (selected friend profile)
        if (bundle != null) {
            addButton.setVisibility(View.GONE);
            subtractButton.setVisibility(View.GONE);
            text.setVisibility(View.GONE);

            loadImageByID(bundle.getString("profilePic"), userPhoto);
            loadImageByID(bundle.getString("cover"), coverImage);

            userName.setText(Objects.requireNonNull(bundle.getString("name")).toUpperCase());
            userCountry.setText(bundle.getString("country"));
            userHandle.setText(bundle.getString("handle"));
        }
        // Access from login page (logged in user profile)
        else {
            addButton.setVisibility(View.VISIBLE);
            subtractButton.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);

            loadImageByID(userData.getProfilePic(), userPhoto);
            loadImageByID(userData.getCoverPhoto(), coverImage);

            String fullName = userData.getFirstName() + " " + userData.getLastName();
            userName.setText(fullName.toUpperCase());
            userCountry.setText(userData.getCountry());
            userHandle.setText(userData.getHandle());

            manager.data().read(userData.myData(), key, (JsonObject data, String e) -> {
                System.out.println("ISNULL " + data);
                if (data == null) {
                    JsonObject init = new JsonObject();
                    init.addProperty("prop", 0);

                    manager.data().write(userData.myData(), key, init, (JsonObject data2, String error) -> {
                        if (error == null) {
                            Log.d("Wrote bucketName:", userData.myData() + " key:" + key + " value:" + init.toString());

                            int myValue = 0;
                            text.setText(String.valueOf(myValue));
                            readData();
                        } else {
                            Log.e("Data write error:", error);
                        }
                    });
                } else {
                    readData();
                }
                if (e != null) {
                    System.out.println("WHAT?");
                }
            });
        }
        super.onActivityCreated(savedInstanceState);
    }


    public void readData() {
        // READ from bucket
        manager.data().read(userData.myData(), key, (JsonObject data, String e) -> {
            if (e == null) {
                Log.d("Read bucketName:", userData.myData() + " key:" + key + " value:" + (data != null ? data.toString() : null));

                int myValue = 0;
                if ((data != null ? data.get("prop") : null) != null) {
                    myValue = Integer.parseInt(data.get("prop").toString());
                }
                text.setText(String.valueOf(myValue));

            } else {
                Log.e("Data read error:", e);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add: {
                String key = "counter";
                TextView text = Objects.requireNonNull(getView()).findViewById(R.id.textAdd);

                // READ from bucket
                manager.data().read(userData.myData(), key, (JsonObject data, String e) -> {
                    if (e == null) {
                        Log.d("Read bucketName:", userData.myData() + " key:" + key + " value:" + (data != null ? data.toString() : null));

                        int myValue;
                        if ((data != null ? data.get("prop") : null) == null) {
                            myValue = 1;
                        } else {
                            myValue = Integer.parseInt(data.get("prop").toString()) + 1;
                        }
                        text.setText(String.valueOf(myValue));

                        JsonObject jo = new JsonObject();
                        jo.addProperty("prop", myValue);

                        //WRITE to bucket
                        manager.data().write(userData.myData(), key, jo, (JsonObject data2, String error) -> {
                            if (error == null) {
                                Log.d("Wrote bucketName:", userData.myData() + " key:" + key + " value:" + jo.toString());
                            } else {
                                Log.e("Data write error:", error);
                            }
                        });

                    } else {
                        Log.e("Data read error:", e);
                    }
                });
                break;
            }

            case R.id.substract: {
                String key = "counter";
                TextView text = Objects.requireNonNull(getView()).findViewById(R.id.textAdd);

                manager.data().read(userData.myData(), key, (JsonObject data, String e) -> {
                    if (e == null) {
                        Log.d("Read bucketName:", userData.myData() + " key:" + key + " value:" + (data != null ? data.toString() : null));

                        int myValue;
                        if ((data != null ? data.get("prop") : null) == null) {
                            myValue = -1;
                        } else {
                            myValue = Integer.parseInt(data.get("prop").toString()) - 1;
                        }
                        text.setText(String.valueOf(myValue));

                        JsonObject jo = new JsonObject();
                        jo.addProperty("prop", myValue);

                        //WRITE to bucket
                        manager.data().write(userData.myData(), key, jo, (JsonObject data2, String error) -> {
                            if (error == null) {
                                Log.d("Wrote bucketName:", userData.myData() + " key:" + key + " value:" + jo.toString());
                            } else {
                                Log.e("Data write error:", error);
                            }
                        });

                    } else {
                        Log.e("Data read error:", e);
                    }
                });
                break;
            }
        }
    }

    public void loadImageByID(String ID, ImageView intoImage) {
        Picasso p = new Picasso.Builder(Objects.requireNonNull(getContext())).downloader(manager.imageDownloader()).build();
        p.load(manager.getBaseUrl() + "/image/v1/static/" + ID).into(intoImage, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("Picasso ", "success");
                imagesToLoad++;
                if (imagesToLoad == 2) {
                    bar.setVisibility(View.GONE);
                    userPhoto.setVisibility(View.VISIBLE);
                    coverImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("Picasso ", "error:" + e);
            }
        });
    }
}
