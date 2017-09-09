package com.thermatk.android.meatb.activities;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.KeyboardUtil;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.controllers.AgendaController;
import com.thermatk.android.meatb.controllers.InboxController;
import com.thermatk.android.meatb.controllers.NewWebController;
import com.thermatk.android.meatb.controllers.ProfileController;
import com.thermatk.android.meatb.controllers.QRCodeController;
import com.thermatk.android.meatb.controllers.RegisterAttendanceController;
import com.thermatk.android.meatb.data.model.UserApi;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewActivity extends AppCompatActivity {

    @BindView(R.id.controller_container) ViewGroup container;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private Router router;
    private SharedPreferences mSharedPreferences;
    public Drawer result;

    public String username = null;
    public UserApi userApi;

    // for webview backstack
    private boolean inWebview = false;
    private WebView webView;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        router.saveInstanceState(outState);
        outState = result.saveInstanceState(outState);
        outState.putParcelable("userApi", userApi);
        Log.d(LogConst.LOG, "saving state");
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LogConst.LOG, "Activity stops");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LogConst.LOG, "Activity destroyed");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences  =  PreferenceManager.getDefaultSharedPreferences(this);
        username = mSharedPreferences.getString("bocconiusername", null);

        if(userApi == null) {
            // get from:
            // 1. saved state
            // 2. intent
            // 3. memory?
            // 4. get a new one!
            if (savedInstanceState == null) {
                userApi = getIntent().getParcelableExtra("userApi");
            } else {
                userApi = savedInstanceState.getParcelable("userApi");
            }
            if (userApi == null) {
                getNewToken();
            }
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        drawerStart(savedInstanceState);

        router = Conductor.attachRouter(this, container, savedInstanceState);
        if (savedInstanceState == null) {
            Log.d(LogConst.LOG, "no saved state");

            // schedule job?
            // scheduleJob();
            if (!router.hasRootController()) {
                Log.d(LogConst.LOG, "init router: NewActivity");
                router.setRoot(RouterTransaction.with(new ProfileController()));
            }
        } else {

        }
    }

    private void changeController(Controller controller) {
        router.pushController(
                RouterTransaction.with(controller)
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler()));
    }

    private void getNewToken() {

    }

    private void drawerStart(Bundle savedInstanceState) {

        DrawerBuilder resultBuilder= new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSavedInstance(savedInstanceState)
                .withDelayOnDrawerClose(0)
                .withDelayDrawerClickEvent(250)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // fix keyboard
                        KeyboardUtil.hideKeyboard(NewActivity.this);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                });
        ///////

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(new ColorDrawable(0xFF42A5F5))
                .addProfiles(
                        // photo!
                        new ProfileDrawerItem().withName(userApi.getNome() + " " + userApi.getCognome()).withEmail(username).withIcon(ContextCompat.getDrawable(this,R.drawable.ic_user))
                )
                .withSelectionListEnabled(false)
                .build();

        resultBuilder.withAccountHeader(headerResult);
        ///////
        // TODO: non-hacky fragments
        addDrawerItems(resultBuilder);

        result = resultBuilder.build();

        if(isFirstLaunch()) {
            result.openDrawer();
            SharedPreferences.Editor editSP = mSharedPreferences.edit();

            editSP.putBoolean("isFirstLaunch", false);
            editSP.apply();
        }
    }

    private void setWebview(boolean state) {
        webView = null;
        inWebview = state;
    }

    public void setWebview(WebView aWebView) {
        webView = aWebView;
    }

    private void addDrawerItems(DrawerBuilder resultBuilder) {
        PrimaryDrawerItem profileDrawerItem = new PrimaryDrawerItem().withName("Profile").withIcon(GoogleMaterial.Icon.gmd_perm_identity).withTag("Profile")
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        setWebview(false);
                        changeController(new ProfileController());
                        return false;
                    }
                });
        PrimaryDrawerItem attendanceDrawerItem = new PrimaryDrawerItem().withName("Attendance").withIcon(GoogleMaterial.Icon.gmd_person_add).withTag("Attendance")
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        setWebview(false);
                        changeController(new RegisterAttendanceController());
                        return false;
                    }
                });
        PrimaryDrawerItem agendaDrawerItem = new PrimaryDrawerItem().withName("Agenda").withIcon(GoogleMaterial.Icon.gmd_date_range).withTag("Agenda")
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        setWebview(false);
                        changeController(new AgendaController());
                        return false;
                    }
                });
        PrimaryDrawerItem inboxDrawerItem = new PrimaryDrawerItem().withName("Inbox").withIcon(GoogleMaterial.Icon.gmd_email).withTag("Inbox")
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        setWebview(false);
                        changeController(new InboxController());
                        return false;
                    }
                });

        PrimaryDrawerItem qrDrawerItem = new PrimaryDrawerItem().withName("Request QR").withIcon(GoogleMaterial.Icon.gmd_vpn_key).withTag("QR")
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        setWebview(true);
                        changeController(new QRCodeController());
                        return false;
                    }
                });


        PrimaryDrawerItem newAtBDrawerItem = new PrimaryDrawerItem().withName("new@B").withIcon(GoogleMaterial.Icon.gmd_dvr).withTag("newAtB")
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        setWebview(true);
                        changeController(new NewWebController());
                        return false;
                    }
                });

        resultBuilder.addDrawerItems(
                profileDrawerItem,
                new DividerDrawerItem(),
                attendanceDrawerItem,
                agendaDrawerItem,
                inboxDrawerItem,
                qrDrawerItem,
                newAtBDrawerItem
        );
    }

    private boolean isFirstLaunch() {
        return mSharedPreferences.getBoolean("isFirstLaunch", true);
    }

}