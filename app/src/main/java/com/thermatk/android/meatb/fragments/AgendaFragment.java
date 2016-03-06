package com.thermatk.android.meatb.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.loopj.android.http.JsonHttpResponseHandler;

import com.thermatk.android.meatb.agenda.BocconiCalendarEvent;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.agenda.BocconiEventRenderer;
import com.thermatk.android.meatb.data.AgendaEvent;
import com.thermatk.android.meatb.data.DataWriter;
import com.thermatk.android.meatb.data.EventDay;
import com.thermatk.android.meatb.yabAPIClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class AgendaFragment extends Fragment implements CalendarPickerController {

    Realm realm;
    AgendaCalendarView mAgendaCalendarView;

    public AgendaFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_agenda, container, false);
        mAgendaCalendarView = (AgendaCalendarView) rootView.findViewById(R.id.agenda_calendar_view);
        /*Button mSignInButton = (Button) rootView.findViewById(R.id.button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });*/
        // TODO: RealmChangeListener
        // TODO: call populate database


        realm = Realm.getDefaultInstance();
        //// check if first time in agenda

        RealmResults<EventDay> days = realm.where(EventDay.class).findAll();
        if (days.size() == 0) {
            sendRequest();
            /// TODO: callback, with RealmChangeListener?
        } else {
            /////
            fillRecyclerView(savedInstanceState, rootView);
        }

        ////

        return rootView;
    }

    private void fillRecyclerView(Bundle savedInstanceState, View aView){

        // minimum and maximum date of our calendar
        // 2 month behind, one year ahead, example: March 2015 <-> May 2015 <-> May 2016
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);

        List<CalendarEvent> eventList = new ArrayList<>();
        trueList(eventList);
        Log.d(LogConst.LOG, Integer.toString(eventList.size()));
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
        mAgendaCalendarView.addEventRenderer(new BocconiEventRenderer());
        //create our FastAdapter which will manage everything
        /*fastItemAdapter = new FastItemAdapter<>();

        final FastScrollIndicatorAdapter<SampleItem> fastScrollIndicatorAdapter = new FastScrollIndicatorAdapter<>();

        //configure our fastAdapter
        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<SampleItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SampleItem> adapter, SampleItem item, int position) {
                Toast.makeText(v.getContext(), (item).name.getText(v.getContext()), Toast.LENGTH_LONG).show();
                return false;
            }
        });

        //configure the itemAdapter
        fastItemAdapter.withFilterPredicate(new IItemAdapter.Predicate<SampleItem>() {
            @Override
            public boolean filter(SampleItem item, CharSequence constraint) {
                //return true if we should filter it out
                //return false to keep it
                return !item.name.getText().toLowerCase().contains(constraint.toString().toLowerCase());
            }
        });

        //get our recyclerView and do basic setup
        RecyclerView recyclerView = (RecyclerView) aView.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastScrollIndicatorAdapter.wrap(fastItemAdapter));

        //add a FastScrollBar (Showcase compatibility)
        //DragScrollBar materialScrollBar = new DragScrollBar(this, recyclerView, true);
        //materialScrollBar.setHandleColour(ContextCompat.getColor(this, R.color.accent));
        //materialScrollBar.addIndicator(new AlphabetIndicator(this), true);

        //fill with some sample data

        //////////

        List<SampleItem> items = new ArrayList<>();
        int x=0;

        RealmResults<EventDay> days = realm.where(EventDay.class).findAll();
        Log.d(LogConst.LOG,Integer.toString(days.size()));
        for(EventDay day: days) {
            Log.d(LogConst.LOG,day.getDate().toString());
            RealmList<AgendaEvent> events = day.getAgendaEvents();
            for(AgendaEvent event: events) {
                items.add(new SampleItem().withName(event.getTitle() + " Starts " + DateFormat.format("yyyyMMdd kk:mm:ss", event.getDate_start())).withIdentifier(100 + x));
                x++;
            }
        }

        fastItemAdapter.add(items);
        ////////////
        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState);
        */

    }
    private void trueList(List<CalendarEvent> eventList) {
        /////

        RealmResults<EventDay> days = realm.where(EventDay.class).findAll();
        Log.d(LogConst.LOG,Integer.toString(days.size()));
        for(EventDay day: days) {
            Log.d(LogConst.LOG,day.getDate().toString());
            RealmList<AgendaEvent> events = day.getAgendaEvents();
            for(AgendaEvent event: events) {

                Calendar startTime = Calendar.getInstance();
                Calendar endTime = Calendar.getInstance();
                Date start = event.getDate_start();
                Date end = event.getDate_end();
                String dateString;
                if (end == null){
                    end =  new Date(start.getTime() + DateUtils.HOUR_IN_MILLIS);
                    dateString = DateFormat.format("kk:mm", event.getDate_start()) + " - ∞";
                } else {
                    dateString  = DateFormat.format("kk:mm", event.getDate_start()) + " - " +DateFormat.format("kk:mm", event.getDate_end());
                }
                startTime.setTimeInMillis(start.getTime());
                endTime.setTimeInMillis(end.getTime());

                BocconiCalendarEvent eventBocconi = new BocconiCalendarEvent(event.getTitle(), event.getDescription(), event.getSupertitle(),
                        ContextCompat.getColor(getActivity(), R.color.orange_dark), startTime, endTime, false, dateString);
                        //new SampleItem().withName(event.getTitle() + " Starts " + DateFormat.format("yyyyMMdd kk:mm:ss", event.getDate_start()))
                eventList.add(eventBocconi);
            }
        }

    }

    private void mockList(List<CalendarEvent> eventList) {
        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        startTime1.setTimeInMillis(1457270486000L);
        endTime1.setTimeInMillis(1457270686000L);
        BocconiCalendarEvent event1 = new BocconiCalendarEvent("Some lecture", "A wonderful lecture!", "Velodromo",
                ContextCompat.getColor(getActivity(), R.color.orange_dark), startTime1, endTime1, false, "11-55");
        eventList.add(event1);

        Calendar startTime2 = Calendar.getInstance();
        Calendar endTime2 = Calendar.getInstance();
        startTime2.setTimeInMillis(1457270186000L);
        endTime2.setTimeInMillis(1457502971000L);

        BocconiCalendarEvent event2 = new BocconiCalendarEvent("test 1", "Some description", "Bocconi",
                ContextCompat.getColor(getActivity(), R.color.yellow), startTime2, endTime2, false, "11-55");
        eventList.add(event2);
        // Example on how to provide your own layout
        Calendar startTime3 = Calendar.getInstance();
        Calendar endTime3 = Calendar.getInstance();
        startTime3.setTimeInMillis(1457502972000L);
        endTime3.setTimeInMillis(1457502982000L);

        BocconiCalendarEvent event3 = new BocconiCalendarEvent("Some seminar", "saadsdsa", "Leoni",
                ContextCompat.getColor(getActivity(), R.color.blue_dark), startTime3, endTime3, false, "11-55");
        eventList.add(event3);

    }


    public void sendRequest() {
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DataWriter.writeAgendaData(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                Log.i(LogConst.LOG, "AgendaRequest failed " + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONArray response) {
                Log.i(LogConst.LOG, "AgendaRequest failed " + response);
            }
        };
        yabAPIClient agendaClient = new yabAPIClient(getActivity());
        agendaClient.getAgendaForAYear(responseHandler);

    }

    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {

    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }
}
