package com.thermatk.android.meatb.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.data.InboxMessage;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

public class InboxAdapter extends RealmBasedRecyclerViewAdapter<InboxMessage,
        InboxAdapter.ViewHolder> {
    RealmResults<InboxMessage> realmResults;
    public InboxAdapter(
            Context context,
            RealmResults<InboxMessage> realmResults,
            boolean automaticUpdate,
            boolean animateIdType) {
        super(context, realmResults, automaticUpdate, animateIdType);
        this.realmResults =realmResults;
    }

    public class ViewHolder extends RealmViewHolder {
        public TextView title;
        public TextView time;
        public TextView description;
        public TextView supertitle;
        public CardView cardView;

        public ViewHolder(LinearLayout container) {
            super(container);

            this.cardView = (CardView) container.findViewById(R.id.inbox_cardview);
            this.title = (TextView) container.findViewById(R.id.title);
            this.time = (TextView) container.findViewById(R.id.event_time);
            this.description = (TextView) container.findViewById(R.id.description);
            this.supertitle = (TextView) container.findViewById(R.id.supertitle);
        }
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.recycler_inbox_row, viewGroup, false);
        return new ViewHolder((LinearLayout) v);
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, final int position) {

        final InboxMessage message = realmResults.get(position);
        viewHolder.title.setText(message.getTitle());
        viewHolder.time.setText(DateFormat.format("yyyy-MM-dd kk:mm", message.getDate()));
        viewHolder.description.setText(message.getDescription());
        viewHolder.supertitle.setText(message.getSupertitle());

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LogConst.LOG, realmResults.get(position).getDescription());
                // TODO: show extended message
                /*courseIntent.putExtra("courseId", item.getId());
                mContext.startActivity(courseIntent);*/
            }
        });
    }
}
