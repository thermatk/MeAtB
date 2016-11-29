package com.thermatk.android.meatb.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.activities.MainActivity;
import com.thermatk.android.meatb.data.InboxMessage;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;

public class NotificationsHelper {

    public static void doNotify(Context context, String title, String message) {
        if (message.length() < 2) {
            // nothing to report?
        } else {
            // TODO: intent should open the needed fragment
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

            // Build notification
            // Actions are just fake
            Notification noti;
            Notification.Builder notificationBuilder = null;

            notificationBuilder = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_notify)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(pIntent);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder = notificationBuilder.setColor(ContextCompat.getColor(context, R.color.iconAccent));
            }
            noti = notificationBuilder.build();
        /*
        // mini icon remover, fails on nougat!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int smallIconViewId = context.getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());

            if (smallIconViewId != 0) {
                if (noti.contentIntent != null)
                    noti.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                if (noti.headsUpContentView != null)
                    noti.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                if (noti.bigContentView != null)
                    noti.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
            }
        }*/
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // hide the notification after its selected
            noti.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(0, noti);
        }
    }

    public static String createNotificationAfterJob() {
        // TODO: maybe notify about agenda changes too?
        String result = "";
        Realm realm = Realm.getDefaultInstance();
        long maxTime = realm.where(InboxMessage.class).findAll().max("lastUpdated").longValue();
        // TODO: timeout should be less random, connect with maxwaiting for requests + overhead!
        if (System.currentTimeMillis() - maxTime < TimeUnit.MINUTES.toMillis(4)) {
            RealmResults<InboxMessage> results = realm.where(InboxMessage.class).equalTo("lastUpdated", maxTime).findAll();
            if (results.size() == 1 ) {
                result = "One new message in inbox: " + results.get(0).getTitle();
            } else if(results.size() > 1) {
                result = "Multiple new messages in your inbox!";
            }
        }
        realm.close();
        return result;
    }
}
