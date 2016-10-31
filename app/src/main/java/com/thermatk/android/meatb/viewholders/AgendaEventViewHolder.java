package com.thermatk.android.meatb.viewholders;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thermatk.android.meatb.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.AnimatorHelper;
import eu.davidea.viewholders.FlexibleViewHolder;


public final class AgendaEventViewHolder extends FlexibleViewHolder {
    public TextView mTitle;
    public TextView mSubtitle;
    public Context mContext;

    public AgendaEventViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.mContext = view.getContext();
        this.mTitle = (TextView) view.findViewById(R.id.view_agenda_event_title);
        this.mSubtitle = (TextView) view.findViewById(R.id.view_agenda_event_location);
    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(mContext, "Click on " + mTitle.getText() + " position " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
        super.onClick(view);
    }

    @Override
    public boolean onLongClick(View view) {
        Toast.makeText(mContext, "LongClick on " + mTitle.getText() + " position " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
        return super.onLongClick(view);
    }

    @Override
    public void scrollAnimators(@NonNull List<Animator> animators, int position, boolean isForward) {
        //Linear layout
        // animator fun!
        switch (position % 2) {
            case 0:
                AnimatorHelper.slideInFromRightAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f);
                break;
            case 1:
                AnimatorHelper.slideInFromLeftAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f);
                break;
            default:
                AnimatorHelper.slideInFromLeftAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f);
                break;
        }
    }
}
