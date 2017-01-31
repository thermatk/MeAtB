package com.thermatk.android.meatb.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bumptech.glide.Glide;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.thermatk.android.meatb.ApplicationMain;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.activities.MainActivity;
import com.thermatk.android.meatb.data.InitData;

import io.realm.Realm;


public class ProfileController extends Controller {
    IDrawerItem drawerItem;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle("Profile");
        Drawer drawer = mainActivity.result;
        drawer.setSelection(drawer.getDrawerItem("Profile"),false);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.controller_profile, container, false);


        Realm realm = Realm.getDefaultInstance();
        InitData profile = realm.where(InitData.class).findAll().first();

        ImageView profPic = (ImageView) rootView.findViewById(R.id.profPic);
        Glide.with(getActivity()).load(profile.getPhoto_url()).transform(new ApplicationMain.CircleTransform(getActivity())).into(profPic);
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
