package com.thermatk.android.meatb.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.data.AgendaEvent;
import com.thermatk.android.meatb.data.DataWriter;
import com.thermatk.android.meatb.data.InitData;
import com.thermatk.android.meatb.yabAPIClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;


public class AgendaFragment extends Fragment {

    public AgendaFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_agenda, container, false);

        /*Button mSignInButton = (Button) rootView.findViewById(R.id.button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });*/
        // TODO: RealmChangeListener
        // TODO: call populate database

        return rootView;
    }

    private void sendRequest() {
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DataWriter.writeAgendaData(response);
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
        yabAPIClient agendaClient = new yabAPIClient(getActivity());
        agendaClient.getAgendaForAYear(responseHandler);

    }

}
