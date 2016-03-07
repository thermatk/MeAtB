package com.thermatk.android.meatb.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.adapter.BocconiRealmRecyclerViewAdapter;
import com.thermatk.android.meatb.data.EventDay;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;


public class ProfileFragment extends Fragment {
    private RealmRecyclerView realmRecyclerView;
    private BocconiRealmRecyclerViewAdapter nyTimesStoryAdapter;
    private Realm realm;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_agenda2, container, false);

        realmRecyclerView = (RealmRecyclerView) rootView.findViewById(R.id.realm_recycler_view);

//        setTitle(getResources().getString(                R.string.activity_layout_name,                getIntent().getStringExtra("Type")));

       // resetRealm();

        realm = Realm.getDefaultInstance();
        RealmResults<EventDay> nyTimesStories =
                realm.where(EventDay.class).findAll();
        nyTimesStoryAdapter = new BocconiRealmRecyclerViewAdapter(getActivity(), nyTimesStories, true, true);
        realmRecyclerView.setAdapter(nyTimesStoryAdapter);

        // Inflate the layout for this fragment
        return rootView;
    }

}
