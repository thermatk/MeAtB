package com.thermatk.android.meatb.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.data.AgendaEvent;
import com.thermatk.android.meatb.data.EventDay;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

public class BocconiRealmRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<EventDay,
        BocconiRealmRecyclerViewAdapter.ViewHolder> {

    public BocconiRealmRecyclerViewAdapter(
            Context context,
            RealmResults<EventDay> realmResults,
            boolean automaticUpdate,
            boolean animateIdType) {
        super(context, realmResults, automaticUpdate, animateIdType);
    }

    public class ViewHolder extends RealmViewHolder {
        public TextView courseName;

        public ViewHolder(LinearLayout container) {
            super(container);
            this.courseName = (TextView) container.findViewById(R.id.courseName);
        }
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.test_view_row, viewGroup, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
        final EventDay eventDay = realmResults.get(position);
        final RealmList<AgendaEvent> events = eventDay.getAgendaEvents();
        if (events != null && !events.isEmpty()) {
            //multimedia.get(0).getUrl()
        } else {
           // viewHolder.image.setImageResource(R.drawable.nytimes_logo);
        }
        viewHolder.courseName.setText(eventDay.getDayString());
    }
}
