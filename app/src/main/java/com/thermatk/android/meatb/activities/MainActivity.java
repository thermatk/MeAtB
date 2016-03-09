package com.thermatk.android.meatb.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.data.InitData;
import com.thermatk.android.meatb.fragments.AgendaFragment;
import com.thermatk.android.meatb.fragments.RegisterAttendanceFragment;
import com.thermatk.android.meatb.fragments.ProfileFragment;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = PreferenceManager.getDefaultSharedPreferences(this).getString("bocconiusername", null);
        if(username == null || PreferenceManager.getDefaultSharedPreferences(this).getString("bocconipassword", null) == null) {
            Log.d(LogConst.LOG, "no credentials, switching to login");
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            finish();
            startActivity(i);
        } else {
            setContentView(R.layout.activity_main);
            drawerStart(savedInstanceState);
            if (savedInstanceState == null) {
                RegisterAttendanceFragment firstFragment = new RegisterAttendanceFragment();
                firstFragment.setArguments(getIntent().getExtras());

                if (findViewById(R.id.content_main_frame) != null) {
                    getFragmentManager().beginTransaction().replace(R.id.content_main_frame, firstFragment).commit();
                }
            }
        }
    }
    private void changeFragment(Fragment fragmentCurrent) {
        // TODO: no replacement of the same type fragment
        if (findViewById(R.id.content_main_frame) != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            getFragmentManager().beginTransaction().replace(R.id.content_main_frame, fragmentCurrent).commit();
        }
    }
    private void drawerStart(Bundle savedInstanceState) {
        // TODO: check InitData

        //////
        Realm realm = Realm.getDefaultInstance();
        InitData profile = realm.where(InitData.class).findAll().first();
        //////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerBuilder resultBuilder= new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSavedInstance(savedInstanceState);
        ///////

        ColorDrawable cd = new ColorDrawable(0xFF42A5F5);
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(cd)
                .addProfiles(
                        new ProfileDrawerItem().withName(profile.getFirstname() + " " + profile.getLastname()).withEmail(username).withIcon(profile.getPhoto_url())
                )
                .withSelectionListEnabled(false)
                .build();

        resultBuilder.withAccountHeader(headerResult);
        ///////
        // TODO: non-hacky fragments
        addDrawerItems(resultBuilder);

        Drawer result = resultBuilder.build();
        /////
        realm.close();
        ////
        // TODO: on first time only
        result.openDrawer();
    }

    private void addDrawerItems(DrawerBuilder resultBuilder) {
        PrimaryDrawerItem profileDrawerItem = new PrimaryDrawerItem().withName("Profile").withIcon(GoogleMaterial.Icon.gmd_perm_identity)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        changeFragment(new ProfileFragment());
                        setTitle("Profile");
                        return false;
                    }
                });
        PrimaryDrawerItem attendanceDrawerItem = new PrimaryDrawerItem().withName("Attendance").withIcon(GoogleMaterial.Icon.gmd_person_add)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        changeFragment(new RegisterAttendanceFragment());
                        setTitle("Attendance");
                        return false;
                    }
                });
        PrimaryDrawerItem agendaDrawerItem = new PrimaryDrawerItem().withName("Agenda").withIcon(GoogleMaterial.Icon.gmd_date_range)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        changeFragment(new AgendaFragment());
                        setTitle("Agenda");
                        return false;
                    }
                });

        resultBuilder.addDrawerItems(
                profileDrawerItem,
                new DividerDrawerItem(),
                attendanceDrawerItem,
                agendaDrawerItem
        );
    }
}
