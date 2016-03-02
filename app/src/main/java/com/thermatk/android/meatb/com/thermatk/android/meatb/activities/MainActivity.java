package com.thermatk.android.meatb.com.thermatk.android.meatb.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.data.InitData;

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
            drawerStart();
        }
    }
    private void drawerStart() {
        // TODO: check InitData

        //////
        Realm realm = Realm.getDefaultInstance();
        InitData profile = realm.where(InitData.class).findAll().first();
        //////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerBuilder resultBuilder= new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar);
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
        PrimaryDrawerItem profileDrawerItem = new PrimaryDrawerItem().withName("Profile").withIcon(GoogleMaterial.Icon.gmd_perm_identity);
        PrimaryDrawerItem attendanceDrawerItem = new PrimaryDrawerItem().withName("Attendance").withIcon(GoogleMaterial.Icon.gmd_person_add);
        PrimaryDrawerItem agendaDrawerItem = new PrimaryDrawerItem().withName("Agenda").withIcon(GoogleMaterial.Icon.gmd_date_range);

        resultBuilder.addDrawerItems(
                profileDrawerItem,
                new DividerDrawerItem(),
                attendanceDrawerItem,
                agendaDrawerItem
        );
        Drawer result = resultBuilder.build();

        // TODO: on first time only
        result.openDrawer();
    }
}
