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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.activities.MainActivity;

import java.util.Calendar;


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
        Button mSignInButton = (Button) rootView.findViewById(R.id.button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(getActivity(), (int) System.currentTimeMillis(), intent, 0);

                // Build notification
                // Actions are just fake
                Notification noti;
                Notification.Builder notificationBuilder = new Notification.Builder(getActivity())
                        .setContentTitle("InboxUpdate" + "test@gmail.com")
                        .setContentText("Yayyaya" + Calendar.getInstance().getTimeInMillis()).setSmallIcon(R.drawable.ic_notify).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentIntent(pIntent);
                if (Build.VERSION.SDK_INT < 16) {
                    noti = notificationBuilder.getNotification();
                } else {
                    noti = notificationBuilder.build();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int smallIconViewId = getActivity().getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());

                    if (smallIconViewId != 0) {
                        if (noti.contentIntent != null)
                            noti.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                        if (noti.headsUpContentView != null)
                            noti.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                        if (noti.bigContentView != null)
                            noti.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
                    }
                }
                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                // hide the notification after its selected
                noti.flags |= Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(0, noti);
            }
        });

        return rootView;
    }

}
