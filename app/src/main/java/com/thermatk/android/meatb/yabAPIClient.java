package com.thermatk.android.meatb;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class yabAPIClient {
    private String username;
    private String password;


    private AsyncHttpClient client = new AsyncHttpClient();

    public yabAPIClient(Context appContext) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(appContext);

        username = sharedPrefs.getString("bocconiusername", null);
        password = sharedPrefs.getString("bocconipassword", null);
        if(username == null || password == null) {
            Log.d(LogConst.LOG,"Couldn't load credentials");
        }
    }
    public static void getLogin(String username, String password, JsonHttpResponseHandler responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        String testLoginURL = "http://ks3-mobile.unibocconi.it/universityapp_prod/api/v6/app/init?os=android";
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
}
