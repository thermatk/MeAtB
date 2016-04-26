package com.thermatk.android.meatb.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.adapters.InboxAdapter;
import com.thermatk.android.meatb.data.DataUtilities;
import com.thermatk.android.meatb.data.InboxMessage;
import com.thermatk.android.meatb.yabAPIClient;

import org.json.JSONArray;
import org.json.JSONObject;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmResults;


public class InboxFragment extends Fragment{

    private RealmRecyclerView realmRecyclerView;
    private InboxAdapter inboxAdapter;
    private Realm realm;

    public InboxFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Inbox");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);



        realm = Realm.getDefaultInstance();
        realmRecyclerView = (RealmRecyclerView) rootView.findViewById(R.id.realm_recycler_view);

        RealmResults<InboxMessage> inboxMessages = realm.where(InboxMessage.class).findAll();
        boolean wasUpdated = inboxMessages.size()>0;
        if(!wasUpdated) {
            sendRequest();
        } else {
            attachAdapter(inboxMessages);
        }

        return rootView;
    }

    public void attachAdapter(RealmResults<InboxMessage> inboxMessages){
        inboxAdapter = new InboxAdapter(getActivity(), inboxMessages, true, true);
        realmRecyclerView.setAdapter(inboxAdapter);
    }

    public void sendRequest() {

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DataUtilities.writeInboxData(response);
                RealmResults<InboxMessage> inboxMessages = realm.where(InboxMessage.class).findAll();
                attachAdapter(inboxMessages);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                Log.i(LogConst.LOG, "InboxRequest failed " + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONArray response) {
                Log.i(LogConst.LOG, "AgendaRequest failed " + response);
            }
        };
        yabAPIClient inboxClient = new yabAPIClient(getActivity());
        inboxClient.getInbox(responseHandler);
    }

}