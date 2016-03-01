package com.thermatk.android.meatb.com.thermatk.android.meatb.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;
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

        //////// NavDrawer
        drawerStart();
        //////////
    }
    private void drawerStart() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerBuilder resultBuilder= new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar);
        ///////

        //picasso Loader
        picassoDrawerLoader();
        ColorDrawable cd = new ColorDrawable(0xFF42A5F5);
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(cd)
                .addProfiles(
                        new ProfileDrawerItem().withName("Random name").withEmail("3323141")
                )
                .withSelectionListEnabled(false)
                .build();

        resultBuilder.withAccountHeader(headerResult);
        ///////

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName("Attendance");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withName("Agenda");

        resultBuilder.addDrawerItems(
                item1,
                new DividerDrawerItem(),
                item2
        );
        Drawer result = resultBuilder.build();

        // TODO: on first time only
        result.openDrawer();
    }

    private void picassoDrawerLoader() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

        });
    }
}
