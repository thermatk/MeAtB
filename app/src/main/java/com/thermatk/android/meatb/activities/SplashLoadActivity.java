package com.thermatk.android.meatb.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.thermatk.android.meatb.LogConst;

public class SplashLoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mSharedPreferences  =  PreferenceManager.getDefaultSharedPreferences(this);
        Intent i;
        if(mSharedPreferences.getString("bocconiusername", null) == null || mSharedPreferences.getString("bocconipassword", null) == null) {
            Log.d(LogConst.LOG, "no credentials, switching to login");
            i = new Intent(this, LoginActivity.class);
        } else {
            i = new Intent(this, MainActivity.class);
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        finish();
        startActivity(i);
    }
}
