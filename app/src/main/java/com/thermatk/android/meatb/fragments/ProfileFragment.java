package com.thermatk.android.meatb.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thermatk.android.meatb.ApplicationMain;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.data.InitData;

import io.realm.Realm;



public class ProfileFragment extends Fragment{

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
        getActivity().setTitle("Profile");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);


        Realm realm = Realm.getDefaultInstance();
        InitData profile = realm.where(InitData.class).findAll().first();

        ImageView profPic = (ImageView) rootView.findViewById(R.id.profPic);
        Glide.with(this).load(profile.getPhoto_url()).transform(new ApplicationMain.CircleTransform(getActivity())).into(profPic);
        // TODO: apply transform?
        TextView name = (TextView) rootView.findViewById(R.id.textViewName);
        TextView career = (TextView) rootView.findViewById(R.id.textViewCareer);
        TextView description = (TextView) rootView.findViewById(R.id.textViewDescription);
        TextView notes = (TextView) rootView.findViewById(R.id.textViewNotes);
        name.setText(profile.getFirstname()+ " " + profile.getLastname());
        career.setText(profile.getCarreerTitle());
        description.setText(profile.getCarreerDescription());
        notes.setText(profile.getCarreerNotes());


        realm.close();
        return rootView;
    }

}
