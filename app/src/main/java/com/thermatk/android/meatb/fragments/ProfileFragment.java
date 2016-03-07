package com.thermatk.android.meatb.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.adapter.BocconiRealmRecyclerViewAdapter;
import com.thermatk.android.meatb.data.EventDay;

import java.util.Calendar;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;


public class ProfileFragment extends Fragment {
    private RealmRecyclerView realmRecyclerView;
    private BocconiRealmRecyclerViewAdapter eventsAdapter;
    private Realm realm;

    MaterialCalendarView materialCalendarView;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_agenda2, container, false);

        realm = Realm.getDefaultInstance();
        ////////////////


        materialCalendarView = (MaterialCalendarView) rootView.findViewById(R.id.calendarView);



        materialCalendarView.addDecorator(new DisableAllDays());
        materialCalendarView.addDecorator(new EnableEventDays());


        Calendar calendar = Calendar.getInstance();
        materialCalendarView.setSelectedDate(calendar.getTime());

        calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, 1);
        materialCalendarView.setMinimumDate(calendar.getTime());

        calendar.set(calendar.get(Calendar.YEAR) + 2, Calendar.OCTOBER, 31);
        materialCalendarView.setMaximumDate(calendar.getTime());

        ////////////
        realmRecyclerView = (RealmRecyclerView) rootView.findViewById(R.id.realm_recycler_view);

        RealmResults<EventDay> agendaEvents = realm.where(EventDay.class).findAll();
        //// change to events


        eventsAdapter = new BocconiRealmRecyclerViewAdapter(getActivity(), agendaEvents, true, true);
        realmRecyclerView.setAdapter(eventsAdapter);
        //////



        // Inflate the layout for this fragment
        return rootView;
    }
    private static class DisableAllDays implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return true;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
        }
    }

    private static class EnableEventDays implements DayViewDecorator {

        private final Drawable highlightDrawable;
        private static final int color = Color.parseColor("#228BC34A");

        public EnableEventDays() {
            highlightDrawable = new ColorDrawable(color);
        }
        @Override
        public boolean shouldDecorate(CalendarDay day) {
            boolean hasEvent = false;
            Realm realm = Realm.getDefaultInstance();
            RealmResults<EventDay> agendaEvents = realm.where(EventDay.class).equalTo("dateString", DateFormat.format("dd-MM-yyyy", day.getDate()).toString()).findAll();
            if(agendaEvents.size() == 1) {
                hasEvent = true;
            }

            return hasEvent;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(highlightDrawable);
            view.setDaysDisabled(false);
        }
    }
}
