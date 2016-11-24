package com.thermatk.android.meatb.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.activities.MainActivity;

import java.util.Calendar;

import static com.thermatk.android.meatb.helpers.CalendarHelper.findAndDeleteCalendars;
import static com.thermatk.android.meatb.helpers.NotificationsHelper.doNotify;


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

        return rootView;
    }

}
