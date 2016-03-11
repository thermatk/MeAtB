package com.thermatk.android.meatb.agenda;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.tibolte.agendacalendarview.render.EventRenderer;
import com.thermatk.android.meatb.R;

public class BocconiEventRenderer extends EventRenderer<BocconiCalendarEvent> {

    // region Class - EventRenderer

    @Override
    public void render(View view, BocconiCalendarEvent event) {
        TextView txtDate = (TextView) view.findViewById(R.id.view_agenda_event_time);
        TextView txtTitle = (TextView) view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_title);
        TextView txtLocation = (TextView) view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_location);
        LinearLayout descriptionContainer = (LinearLayout) view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_description_container);
        LinearLayout locationContainer = (LinearLayout) view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_location_container);

        descriptionContainer.setVisibility(View.VISIBLE);

        txtDate.setText(event.getDate());

        txtTitle.setTextColor(view.getResources().getColor(android.R.color.black));

        txtTitle.setText(event.getTitle());
        txtLocation.setText(event.getLocation());
        if (event.getLocation().length() > 0) {
            locationContainer.setVisibility(View.VISIBLE);
            txtLocation.setText(event.getLocation());
        } else {
            locationContainer.setVisibility(View.GONE);
        }

        if (event.getTitle().equals(view.getResources().getString(com.github.tibolte.agendacalendarview.R.string.agenda_event_no_events))) {
            txtTitle.setTextColor(view.getResources().getColor(android.R.color.black));
        } else {
            txtTitle.setTextColor(view.getResources().getColor(com.github.tibolte.agendacalendarview.R.color.theme_text_icons));
        }
        descriptionContainer.setBackgroundColor(event.getColor());
        txtLocation.setTextColor(view.getResources().getColor(com.github.tibolte.agendacalendarview.R.color.theme_text_icons));
    }

    @Override
    public int getEventLayout() {
        return R.layout.view_agenda_bocconi_event;
    }

    @Override
    public Class<BocconiCalendarEvent> getRenderType() {
        return BocconiCalendarEvent.class;
    }

    // endregion
}
