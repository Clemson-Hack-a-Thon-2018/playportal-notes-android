package com.profiq.vr.dynepicapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dynepic.ppsdk_android.PPManager;

public class LoginActivity extends AppCompatActivity {

    private PPManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String id = getString(R.string.id);
        String sec = getString(R.string.sec);
        String uri = getString(R.string.uri);
        String name = getString(R.string.name);

        manager = PPManager.getInstance();
        manager.setContextAndActivity(this, this);
        manager.configure(id, sec, uri, "v", name, (status) -> {
            // lambda function to catch configure response
            if (manager.isAuthenticated() && manager.getUserData().hasUser()) {
                Intent intent = new Intent(this, MainApp.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onSignInClick(View v) {
        //Determine if user is already authenticated
        if (!manager.isAuthenticated()) {
            Intent myIntent = new Intent(this, MainApp.class);
            manager.showSSOLogin(myIntent);
        }
    }
}




