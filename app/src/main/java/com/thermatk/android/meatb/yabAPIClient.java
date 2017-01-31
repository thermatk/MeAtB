package com.thermatk.android.meatb;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.thermatk.android.meatb.data.InitData;

import java.util.Date;

import io.realm.Realm;

public class yabAPIClient {
    private String username;
    private String password;

    private static final String apiUrl = "https://ks3-mobile.unibocconi.it/universityapp_https_prod/api/v6/";
    private AsyncHttpClient client;

    public yabAPIClient(Context appContext, boolean async) {
        if(async) {
            client = new AsyncHttpClient();
        } else {
            client = new SyncHttpClient();
        }
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(appContext);

        username = sharedPrefs.getString("bocconiusername", null);
        password = sharedPrefs.getString("bocconipassword", null);

        if(username == null || password == null) {
            Log.d(LogConst.LOG,"Couldn't load credentials");
        }
    }
    public static void getLogin(String username, String password, JsonHttpResponseHandler responseHandler, boolean async) {
        AsyncHttpClient client;
        if(async) {
            client = new AsyncHttpClient();
        } else {
            client = new SyncHttpClient();
        }
        String testLoginURL = apiUrl + "app/init?os=android";
        client.addHeader("auth_secret", "b0cc0n1s3cr3t");
        client.setBasicAuth(username, password);
        client.get(testLoginURL, responseHandler);
    }

    public void registerAttendance(String codice, JsonHttpResponseHandler responseHandler) {
        String registerAttendanceURL = "https://app-public.unibocconi.it/PresencesPubl/RilevaPresenza/RilevaPresenza?username="+username+"&password="+password+"&codicePin="+codice;
        Log.d(LogConst.LOG,"URL: " + registerAttendanceURL);
        client.addHeader("Referer", "https://app-public.unibocconi.it/Presencespubl/RilevaPresenza");
        client.addHeader("X-Requested-With", "XMLHttpRequest");
        client.get(registerAttendanceURL, responseHandler);
    }

    public void getAgenda(String dateStart, String dateEnd, JsonHttpResponseHandler responseHandler) {

        Realm realm = Realm.getDefaultInstance();
        InitData profile = realm.where(InitData.class).findAll().first();
        String careerId = profile.getCarreerId() + "";
        realm.close();

        String agendaURL= apiUrl + "students/"+careerId+"/agenda?rl=en&start="+dateStart+"&end="+dateEnd;
        client.addHeader("auth_secret", "b0cc0n1s3cr3t");
        client.setBasicAuth(username, password);
        client.get(agendaURL, responseHandler);
    }

    public void getAgendaForAYear(JsonHttpResponseHandler responseHandler) {
        String dateStart=DateFormat.format("yyyyMMdd", new Date()).toString();
        String dateEnd=DateFormat.format("yyyyMMdd", new Date(System.currentTimeMillis() + DateUtils.YEAR_IN_MILLIS)).toString();

        getAgenda(dateStart, dateEnd, responseHandler);
    }

    public void getInbox(JsonHttpResponseHandler responseHandler) {
        String inboxURL= apiUrl + "inbox?rl=en";
        client.addHeader("auth_secret", "b0cc0n1s3cr3t");
        client.setBasicAuth(username, password);
        client.get(inboxURL, responseHandler);
    }

    public void getNewAtBToken(JsonHttpResponseHandler responseHandler) {
        String loginURL = "https://portalapp.unibocconi.it/portal.api/api/Auth/User/Login";
        client.setBasicAuth(username, password);
        client.post(loginURL, responseHandler);
    }
}
