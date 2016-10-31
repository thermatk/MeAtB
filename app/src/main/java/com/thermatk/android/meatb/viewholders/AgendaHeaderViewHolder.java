package com.thermatk.android.meatb.viewholders;

import android.view.View;
import android.widget.TextView;

import com.thermatk.android.meatb.R;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;

public class AgendaHeaderViewHolder extends FlexibleViewHolder {

    public TextView mTitle;
    public TextView mSubtitle;

    public AgendaHeaderViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter, true);//True for sticky
        mTitle = (TextView) view.findViewById(R.id.title);
        mSubtitle = (TextView) view.findViewById(R.id.subtitle);
            /*mTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HeaderTitle", "Registered internal click on Header TitleTextView! " + mTitle.getText() + " position=" + getFlexibleAdapterPosition());
                }
            });*/
    }
}