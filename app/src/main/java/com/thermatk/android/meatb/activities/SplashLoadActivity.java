package com.thermatk.android.meatb.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.Window;

import com.thermatk.android.meatb.LogConst;

public class SplashLoadActivity extends AppCompatActivity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isLollipop = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);

        if (isLollipop) requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);


        super.onCreate(savedInstanceState);

        
        if (isLollipop) {
            getWindow().setExitTransition(new Fade());
        }

        SharedPreferences mSharedPreferences  =  PreferenceManager.getDefaultSharedPreferences(this);
        Intent i;
        if(mSharedPreferences.getString("bocconiusername", null) == null || mSharedPreferences.getString("bocconipassword", null) == null) {
            Log.d(LogConst.LOG, "no credentials, switching to login");
            i = new Intent(this, LoginActivity.class);
        } else {
            i = new Intent(this, NewActivity.class);
        }

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
        ActivityCompat.startActivity(this, i, options.toBundle());
        ActivityCompat.finishAfterTransition(this);
    }
}
