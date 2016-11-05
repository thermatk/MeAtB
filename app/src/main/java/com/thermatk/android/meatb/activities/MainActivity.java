package com.thermatk.android.meatb.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.data.InitData;
import com.thermatk.android.meatb.fragments.AgendaFragment;
import com.thermatk.android.meatb.fragments.InboxFragment;
import com.thermatk.android.meatb.fragments.RegisterAttendanceFragment;
import com.thermatk.android.meatb.fragments.ProfileFragment;

import io.realm.Realm;

import static com.thermatk.android.meatb.helpers.CalendarHelper.addCalendar;
import static com.thermatk.android.meatb.helpers.CalendarHelper.getReminderState;
import static com.thermatk.android.meatb.helpers.CalendarHelper.setReminderState;
import static com.thermatk.android.meatb.refresher.RefreshAllJob.scheduleJob;

public class MainActivity extends AppCompatActivity {
    private String username = null;
    private SharedPreferences mSharedPreferences;
    Drawer result;
    SwitchDrawerItem calendarSwitchDrawerItem;
    private Activity mActivity;

    private final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mSharedPreferences  =  PreferenceManager.getDefaultSharedPreferences(this);
        username = mSharedPreferences.getString("bocconiusername", null);


        setContentView(R.layout.activity_main);
        drawerStart(savedInstanceState);
        if (savedInstanceState == null) {
            // schedule job?
            scheduleJob();
            ProfileFragment firstFragment = new ProfileFragment();
            firstFragment.setArguments(getIntent().getExtras());

            if (findViewById(R.id.content_main_frame) != null) {
                getFragmentManager().beginTransaction().replace(R.id.content_main_frame, firstFragment).commit();
            }
        }
    }
    private void changeFragment(final Fragment fragmentCurrent) {

        // TODO: no replacement of the same type fragment
        if (findViewById(R.id.content_main_frame) != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setupWindowAnimations(fragmentCurrent);
                    getFragmentManager().beginTransaction().replace(R.id.content_main_frame, fragmentCurrent).commit();
                }
            }, 200);
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
                .withSavedInstance(savedInstanceState)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // fix keyboard
                        KeyboardUtil.hideKeyboard(MainActivity.this);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                });
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

        result = resultBuilder.build();
        /////
        realm.close();
        ////
        if(isFirstLaunch()) {
            result.openDrawer();
            SharedPreferences.Editor editSP = mSharedPreferences.edit();

            editSP.putBoolean("isFirstLaunch", false);
            editSP.apply();
        }
    }

    private void addDrawerItems(DrawerBuilder resultBuilder) {
        PrimaryDrawerItem profileDrawerItem = new PrimaryDrawerItem().withName("Profile").withIcon(GoogleMaterial.Icon.gmd_perm_identity)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        changeFragment(new ProfileFragment());
                        return false;
                    }
                });
        PrimaryDrawerItem attendanceDrawerItem = new PrimaryDrawerItem().withName("Attendance").withIcon(GoogleMaterial.Icon.gmd_person_add)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        changeFragment(new RegisterAttendanceFragment());
                        return false;
                    }
                });
        PrimaryDrawerItem agendaDrawerItem = new PrimaryDrawerItem().withName("Agenda").withIcon(GoogleMaterial.Icon.gmd_date_range)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        changeFragment(new AgendaFragment());
                        return false;
                    }
                });
        PrimaryDrawerItem inboxDrawerItem = new PrimaryDrawerItem().withName("Inbox").withIcon(GoogleMaterial.Icon.gmd_email)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        changeFragment(new InboxFragment());
                        return false;
                    }
                });
        calendarSwitchDrawerItem = new SwitchDrawerItem().withName("Calendar Reminders").withIcon(GoogleMaterial.Icon.gmd_alarm_on).
                withChecked(
                        getReminderState(this) && (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_CALENDAR)== PackageManager.PERMISSION_GRANTED))
                .withSelectable(false).withOnCheckedChangeListener(onCalendarSwitchListener);

        resultBuilder.addDrawerItems(
                profileDrawerItem,
                new DividerDrawerItem(),
                attendanceDrawerItem,
                agendaDrawerItem,
                inboxDrawerItem,
                new DividerDrawerItem(),
                calendarSwitchDrawerItem
        );
    }

    private boolean isFirstLaunch() {
        return mSharedPreferences.getBoolean("isFirstLaunch", true);
    }

    private OnCheckedChangeListener onCalendarSwitchListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            Log.i(LogConst.LOG, "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            if(isChecked) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED) {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(mActivity,
                                new String[]{Manifest.permission.WRITE_CALENDAR},
                                MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
                }else {
                    long calendarId = addCalendar(getApplicationContext());
                    setReminderState(mSharedPreferences,true,calendarId); //TODO: check if have calendar and need one
                }
            } else {
                setReminderState(mSharedPreferences,false,-1);
                // delete calendars?
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
    String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // task you need to do.
                    Log.d(LogConst.LOG, "Granted Calendar!");
                    long calendarId = addCalendar(getApplicationContext());
                    setReminderState(mSharedPreferences,true,calendarId);

                } else {
                    Log.d(LogConst.LOG, "Denied Calendar!");
                    calendarSwitchDrawerItem.withChecked(false);
                    result.updateItem(calendarSwitchDrawerItem);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    setReminderState(mSharedPreferences,false,-1);

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void setupWindowAnimations(Fragment fragment) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade;
            fade = new Fade();
            fade.setDuration(1000);
            fragment.setEnterTransition(fade);

            Slide slide = new Slide();
            slide.setDuration(1000);
            fragment.setReturnTransition(slide);
         }
    }
}
