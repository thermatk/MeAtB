package com.thermatk.android.meatb.com.thermatk.android.meatb.activities;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(PreferenceManager.getDefaultSharedPreferences(this).getString("bocconiusername", null) == null || PreferenceManager.getDefaultSharedPreferences(this).getString("bocconipassword", null) == null) {
            Log.d(LogConst.LOG, "no credentials, switching to login");
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            finish();
            startActivity(i);
        }
        Log.d(LogConst.LOG, "continue 1");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawer result= new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .build();
    }
}
