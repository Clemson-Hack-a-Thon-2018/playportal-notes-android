package com.profiq.vr.dynepicapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutActivity extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_about, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        TextView about = getView() != null ? getView().findViewById(R.id.about_text) : null;
        if (about != null) {
            about.setMovementMethod(LinkMovementMethod.getInstance());
        }
        super.onActivityCreated(savedInstanceState);
    }
}