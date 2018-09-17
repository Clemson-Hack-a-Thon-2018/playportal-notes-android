package com.profiq.vr.dynepicapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dynepic.ppsdk_android.PPManager;
import com.dynepic.ppsdk_android.models.User;

import java.util.ArrayList;
import java.util.Objects;


public class FriendList extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friend_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        PPManager manager = PPManager.getInstance();
        ListView listView = getView() != null ? getView().findViewById(R.id.friendListView) : null;
        FragmentManager fragmentManager = getActivity() != null ? getActivity().getSupportFragmentManager() : null;
        manager.friends().get((ArrayList<User> friendsList, String e) -> {
            if (friendsList != null && e == null) {
                for (User f : friendsList) {
                    System.out.println("friend: " + f.getHandle() + " - " + f.getFirstName() + " " + f.getLastName());
                }
                if (listView != null) {
                    listView.setAdapter(new FriendListAdapter(Objects.requireNonNull(getContext()), friendsList, fragmentManager));
                }
            } else {
                Log.e("get friends error:", e);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
