package com.thermatk.android.meatb;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class yabAPIClient {
    public static void getLogin(String username, String password, JsonHttpResponseHandler responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        String testLoginURL = "http://ks3-mobile.unibocconi.it/universityapp_prod/api/v6/app/init?os=android";
        client.addHeader("auth_secret", "b0cc0n1s3cr3t");
        client.setBasicAuth(username, password);
        client.get(testLoginURL, responseHandler);
    }
}
