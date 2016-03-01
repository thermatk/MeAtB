package com.thermatk.android.meatb;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.thermatk.android.meatb.com.thermatk.android.meatb.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class yabAPIClient {
    public static void getLogin(String username, String password, JsonHttpResponseHandler responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        String testLoginURL = "http://ks3-mobile.unibocconi.it/universityapp_prod/api/v6/app/init?os=android";
        client.addHeader("auth_secret", "b0cc0n1s3cr3t");
        client.setBasicAuth(username, password);
        client.get(testLoginURL, responseHandler);
    }
}
