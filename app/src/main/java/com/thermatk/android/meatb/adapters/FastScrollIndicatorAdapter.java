package com.thermatk.android.meatb.adapters;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;
import com.thermatk.android.meatb.lists.SampleItem;
import com.turingtechnologies.materialscrollbar.ICustomAdapter;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.List;

/**
 * Created by thermatk on 04.03.16.
 */

public class FastScrollIndicatorAdapter<Item extends IItem> extends AbstractAdapter<Item> implements INameableAdapter, ICustomAdapter {
    @Override
    public Character getCharacterForElement(int position) {
        IItem item = getItem(position);
        if (item instanceof SampleItem && ((SampleItem) item).name != null) {
            //based on the position we set the headers text
            return ((SampleItem) item).name.getText().charAt(0);
        }
        return ' ';
    }

    @Override
    public String getCustomStringForElement(int position) {
        IItem item = getItem(position);
        return "";
    }

    /**
     * REQUIRED FOR THE FastAdapter. Set order to < 0 to tell the FastAdapter he can ignore this one.
     **/

    /**
     * @return
     */
    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public List<Item> getAdapterItems() {
        return null;
    }

    @Override
    public Item getAdapterItem(int position) {
        return null;
    }

    @Override
    public int getAdapterPosition(IItem item) {
        return -1;
    }

    @Override
    public int getGlobalPosition(int position) {
        return -1;
    }

}
