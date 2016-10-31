package com.thermatk.android.meatb.adapters;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.agenda.HeaderItem;
import com.thermatk.android.meatb.agenda.EventItem;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.AnimatorHelper;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class AgendaAdapter extends FlexibleAdapter<IFlexible> {

    private List<IFlexible> itemsList;

    public AgendaAdapter(List<IFlexible> items, Object listeners) {
        //true = Items implement hashCode() and have stableIds!
        super(items, listeners, true);
        itemsList = items;
    }

    @Override
    public void updateDataSet(List<IFlexible> items, boolean animate) {
        //NOTE: To have views/items not changed, set them into "items" before passing the final
        // list to the Adapter.

        //Overwrite the list and fully notify the change, pass false to not animate changes.
        //Watch out! The original list must a copy
        super.updateDataSet(items, animate);

        itemsList = items;
        //Add example view
        //showLayoutInfo(true);
    }

    /*
     * Add one temporary item to the top
     */
    public void showLayoutInfo(boolean scrollToPosition) {
        if (!hasSearchText() && !isEmpty()) {
            //Define Example View
            final LayoutItem item = new LayoutItem("LAY-L");
            item.setTitle(mRecyclerView.getContext().getString(R.string.app_name));
            item.setSubtitle("XDDDD");
            addItemWithDelay(0, item, 0L,
                    (scrollToPosition));
            removeItemWithDelay(item, 4000L, true);
        }
    }

    // for fastscroller
    @Override
    public String onCreateBubbleText(int position) {

        //return super.onCreateBubbleText(position);
        IFlexible current = itemsList.get(position);
        if (current instanceof EventItem) {
            return ((EventItem) itemsList.get(position)).getHeader().getTitle();
        } else if(current instanceof HeaderItem) {
            return ((HeaderItem) itemsList.get(position)).getTitle();
        } else {
            return "-";
        }
    }

}

class LayoutItem extends AbstractFlexibleItem<LayoutItem.LayoutViewHolder> {

    private String id;
    private String title;
    private String subtitle;

    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof LayoutItem) {
            LayoutItem inItem = (LayoutItem) inObject;
            return this.id.equals(inItem.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public LayoutItem(String id) {
        this.id = id;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_agenda_layout_item;
    }

    @Override
    public LayoutViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new LayoutViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, LayoutViewHolder holder, int position, List payloads) {
        holder.mTitle.setSelected(true);//For marquee
        holder.mTitle.setText(getTitle());
        holder.mSubtitle.setText(getSubtitle());
    }

    public static class LayoutViewHolder extends FlexibleViewHolder {

        public TextView mTitle;
        public TextView mSubtitle;

        public LayoutViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);
            mTitle = (TextView) view.findViewById(R.id.title);
            mSubtitle = (TextView) view.findViewById(R.id.subtitle);
        }

        @Override
        public void scrollAnimators(@NonNull List<Animator> animators, int position, boolean isForward) {
            AnimatorHelper.slideInFromTopAnimator(animators, itemView, mAdapter.getRecyclerView());
        }
    }
}