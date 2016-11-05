package com.thermatk.android.meatb.helpers;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.yabAPIClient;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class JobHelper {
    // TODO: funny corner case when background service deleted everything but interface doesn't know
    public static void runAgendaUpdate(final Context context) {
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(DataHelper.isAgendaInitial()) {
                    DataHelper.writeAgendaDataInitial(response,context);
                } else {
                    // TODO: compare before and after
                    DataHelper.wipeAgenda(context);
                    DataHelper.writeAgendaDataInitial(response,context);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                Log.i(LogConst.LOG, "AgendaRequest failed " + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONArray response) {
                Log.i(LogConst.LOG, "AgendaRequest failed " + response);
            }
        };
        yabAPIClient agendaClient = new yabAPIClient(context,false);
        agendaClient.getAgendaForAYear(responseHandler);
    }

    public static void runInboxUpdate(final Context context) {

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DataHelper.writeInboxData(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                Log.i(LogConst.LOG, "InboxRequest failed " + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONArray response) {
                Log.i(LogConst.LOG, "InboxRequest failed " + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable error) {
                Log.i(LogConst.LOG, "InboxRequest failed " + response);
            }
        };
        yabAPIClient inboxClient = new yabAPIClient(context,false);
        inboxClient.getInbox(responseHandler);
    }
}
