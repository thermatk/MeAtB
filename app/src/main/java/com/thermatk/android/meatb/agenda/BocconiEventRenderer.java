package com.thermatk.android.meatb.agenda;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.tibolte.agendacalendarview.render.EventRenderer;
import com.thermatk.android.meatb.R;

import io.realm.REventRealmProxy;

public class BocconiEventRenderer extends EventRenderer<REventRealmProxy> {

    // region Class - EventRenderer

    @Override
    public void render(View view, REventRealmProxy event) {
        TextView txtDate = (TextView) view.findViewById(R.id.view_agenda_event_time);
        TextView txtTitle = (TextView) view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_title);
        TextView txtLocation = (TextView) view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_location);
        LinearLayout descriptionContainer = (LinearLayout) view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_description_container);

        descriptionContainer.setVisibility(View.VISIBLE);

        txtDate.setText(event.getDate());
        Context mContext = view.getContext();
        txtTitle.setTextColor(ContextCompat.getColor(mContext,android.R.color.black));

        txtTitle.setText(event.getTitle());
        txtLocation.setText(event.getLocation());
        if (event.getLocation().length() > 0) {
            txtLocation.setVisibility(View.VISIBLE);
            txtLocation.setText(event.getLocation());
        } else {
            txtLocation.setVisibility(View.GONE);
        }

        if (event.getTitle().equals(view.getResources().getString(com.github.tibolte.agendacalendarview.R.string.agenda_event_no_events))) {
            txtTitle.setTextColor(ContextCompat.getColor(mContext,android.R.color.black));
        } else {
            txtTitle.setTextColor(ContextCompat.getColor(mContext,com.github.tibolte.agendacalendarview.R.color.theme_text_icons));
        }
        descriptionContainer.setBackgroundColor(event.getColor());
        txtLocation.setTextColor(ContextCompat.getColor(mContext,com.github.tibolte.agendacalendarview.R.color.theme_text_icons));
    }

    @Override
    public int getEventLayout() {
        return R.layout.view_agenda_bocconi_event;
    }

    @Override
    public Class<REventRealmProxy> getRenderType() {
        return REventRealmProxy.class;
    }

    // endregion
}
