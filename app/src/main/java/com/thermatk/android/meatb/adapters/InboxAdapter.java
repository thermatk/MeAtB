package com.thermatk.android.meatb.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.data.InboxMessage;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by thermatk on 25.04.16.
 */
public class InboxAdapter extends RealmBasedRecyclerViewAdapter<InboxMessage,
        InboxAdapter.ViewHolder> {
    public InboxAdapter(
            Context context,
            RealmResults<InboxMessage> realmResults,
            boolean automaticUpdate,
            boolean animateIdType) {
        super(context, realmResults, automaticUpdate, animateIdType);
    }

    public class ViewHolder extends RealmViewHolder {
        public TextView title;

        public ViewHolder(LinearLayout container) {
            super(container);
            this.title = (TextView) container.findViewById(R.id.title);
        }
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.view_inbox_row, viewGroup, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {

        final InboxMessage message = realmResults.get(position);
        viewHolder.title.setText(message.getTitle());
    }
}
