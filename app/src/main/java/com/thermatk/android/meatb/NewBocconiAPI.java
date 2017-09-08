package com.thermatk.android.meatb;

import android.util.Base64;

import com.thermatk.android.meatb.data.remote.LoginAPIService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Thread safe login API client
 */

public class NewBocconiAPI {
    private static final String baseUrl = "https://portalapp.unibocconi.it/portal.api/api/";

    private static volatile Retrofit retrofit = null;

    private static Retrofit getClient() {
        if (retrofit==null) {
            synchronized (NewBocconiAPI.class) {
                if (retrofit==null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static LoginAPIService getLoginAPIService() {
        return getClient().create(LoginAPIService.class);
    }

    public static String basicAuthHeader(String username, String password) {
        return "BASIC " + Base64.encodeToString((username + ":" + password).getBytes(), 0).trim();
    }
}
