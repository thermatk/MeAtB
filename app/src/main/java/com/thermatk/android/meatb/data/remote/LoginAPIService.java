package com.thermatk.android.meatb.data.remote;

/**
 * Created by thermatk on 08.09.17.
 */

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import com.thermatk.android.meatb.data.model.UserApi;

public interface LoginAPIService {
    @POST("Auth/User/Login")
    Call<UserApi> getLogin(@Header("Authorization") String authString);
}
