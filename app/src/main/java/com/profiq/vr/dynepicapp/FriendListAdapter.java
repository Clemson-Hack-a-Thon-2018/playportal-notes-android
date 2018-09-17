package com.profiq.vr.dynepicapp;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dynepic.ppsdk_android.PPManager;
import com.dynepic.ppsdk_android.models.User;

import java.util.ArrayList;

public class FriendListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context mContext;
    private ArrayList<User> friendList;
    private PPManager manager;
    private FragmentManager fragmentManager;

    FriendListAdapter(Context context, ArrayList<User> userList, FragmentManager fragmentManager) {
        this.mContext = context;
        this.friendList = userList;
        this.fragmentManager = fragmentManager;
        this.manager = PPManager.getInstance();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.friendList.size();
    }

    @Override
    public User getItem(int position) {
        return this.friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.friend_list_item, null);

        User selectedFriend = friendList.get(position);


        final TextView friendName = convertView.findViewById(R.id.friendName);
        final TextView handle = convertView.findViewById(R.id.handle);
        final ImageView profileImage = convertView.findViewById(R.id.profileImage);

        profileImage.setClipToOutline(true);
        String fullName = selectedFriend.getFirstName().toUpperCase() + " " + selectedFriend.getLastName().toUpperCase();
        friendName.setText(fullName);

        handle.setText(selectedFriend.getHandle());

        manager.loadImageByID(mContext, selectedFriend.getProfilePic(), profileImage);
        convertView.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            Fragment fragment = null;
            try {
                fragment = UserProfile.class.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bundle.putString("name", selectedFriend.getFirstName() + " " + selectedFriend.getLastName());
            bundle.putString("handle", selectedFriend.getHandle());
            bundle.putString("country", selectedFriend.getCountry());
            bundle.putString("profilePic", selectedFriend.getProfilePic());
            bundle.putString("cover", selectedFriend.getCoverPhoto());
            if (fragment != null) {
                fragment.setArguments(bundle);
            }
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        });
        return convertView;
    }
}
