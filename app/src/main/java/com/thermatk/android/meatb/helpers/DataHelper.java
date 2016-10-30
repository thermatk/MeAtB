package com.thermatk.android.meatb.helpers;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;

import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.data.AgendaEvent;
import com.thermatk.android.meatb.data.EventDay;
import com.thermatk.android.meatb.data.InboxMessage;
import com.thermatk.android.meatb.data.InitData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.thermatk.android.meatb.helpers.CalendarHelper.addEvent;
import static com.thermatk.android.meatb.helpers.CalendarHelper.getReminderState;
import static com.thermatk.android.meatb.helpers.CalendarHelper.setReminder;

public class DataHelper {
    public static void writeInitData(JSONObject rawJSON) {
        // TODO: make async https://realm.io/docs/java/latest/#asynchronous-transactions
        String firstname = null;
        String lastname = null;
        String photo_url = null;
        String carreerTitle = null;
        String carreerDescription = null;
        String carreerNotes = null;
        long carreerId = 0;
        String careerDateStart = null;

        try {
            JSONObject authSession;
            authSession = rawJSON.getJSONObject("auth_session");
            firstname = authSession.get("firstname").toString();
            lastname = authSession.get("lastname").toString();
            photo_url= java.net.URLDecoder.decode(authSession.get("photo_url").toString(), "UTF-8");
            JSONObject careers= authSession.getJSONArray("careers").getJSONObject(0);
            carreerTitle = careers.get("title").toString();
            carreerDescription = careers.get("description").toString();
            carreerNotes = careers.get("notes").toString();
            carreerId = Long.parseLong(careers.get("id").toString());
            careerDateStart = careers.get("date_start").toString();

            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            //TODO: check existence
            InitData data = realm.createObject(InitData.class);
            data.setFirstname(firstname);
            data.setLastname(lastname);
            data.setPhoto_url(photo_url);
            data.setCarreerTitle(carreerTitle);
            data.setCareerDateStart(careerDateStart);
            data.setCarreerDescription(carreerDescription);
            data.setCarreerId(carreerId);
            data.setCarreerNotes(carreerNotes);
            data.setRawData(rawJSON.toString());
            data.setLastUpdated(System.currentTimeMillis());
            realm.commitTransaction();
            realm.close();
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void writeInboxData(JSONArray response) {
        Realm realm = Realm.getDefaultInstance();
        long currentTime = System.currentTimeMillis();



        for (int i = 0; i < response.length(); i++) {
            JSONObject oneMessage;
            long dateLong;
            Date date;
            try {
                oneMessage = response.getJSONObject(i);
                String strTemp = oneMessage.getString("date_sent");

                dateLong = getDateAPI(strTemp);
                //TODO: do some better timezone hacks
                // add 4 hours to be sure we're in the right day
                //dateLong +=  (DateUtils.HOUR_IN_MILLIS * 4);
                date = new Date(dateLong);

                realm.beginTransaction();

                InboxMessage message = realm.createObject(InboxMessage.class,i + 1);///// TODO: do not forget about the id problem
                message.setDate(date);
                message.setDateLong(dateLong);
                message.setLastUpdated(currentTime);
                message.setInternalId(oneMessage.getLong("id"));
                message.setDescription(oneMessage.getString("description"));
                message.setTitle(oneMessage.getString("title"));
                message.setFavorite(oneMessage.getBoolean("is_favorite"));
                message.setFeatured(oneMessage.getBoolean("is_featured"));
                message.setUnread(oneMessage.getBoolean("is_unread"));
                message.setSupertitle(oneMessage.getString("supertitle"));
                message.setDownloadedFull(false);

                realm.commitTransaction();
                ///////
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }





        realm.close();
    }

    public static void writeAgendaData(JSONArray response, Context context) {



        Realm realm = Realm.getDefaultInstance();
        long currentTime = System.currentTimeMillis();
        //// Days loop
        for (int i = 0; i < response.length(); i++) {
            JSONObject oneDay;
            long dateLong;
            Date date;
            try {
                oneDay = response.getJSONObject(i);
                String strTemp = oneDay.getString("date");

                dateLong = getDateAPI(strTemp);
                //TODO: do some better timezone hacks
                // add 4 hours to be sure we're in the right day
                //dateLong +=  (DateUtils.HOUR_IN_MILLIS * 4);
                date = new Date(dateLong);
                //
                // TODO: find previously done days and compare

                realm.beginTransaction();
                EventDay day = realm.createObject(EventDay.class, i+1);///// TODO: do not forget about the id problem
                day.setDate(date);
                day.setDateLong(dateLong);
                day.setDateString(DateFormat.format("dd-MM-yyyy", date).toString());
                day.setDayString(oneDay.toString());
                day.setLastUpdated(currentTime);
                //Log.d(LogConst.LOG,Long.toString(day.getDateLong()));
                RealmList<AgendaEvent> agendaList = new RealmList<>();
                ///////Events loop
                JSONArray eventsArray = oneDay.getJSONArray("events");

                for (int j = 0; j < eventsArray.length(); j++) {

                    JSONObject oneEvent;
                    Date date_end;
                    Date date_start;
                    long date_end_long = 0;
                    long date_start_long;
                    String duration;
                    String description;
                    long id;
                    String supertitle;
                    String title;
                    long type;
                    long courseId;

                    oneEvent = eventsArray.getJSONObject(j);
                    title = oneEvent.getString("title");
                    supertitle = oneEvent.getString("supertitle");
                    description = oneEvent.getString("description");

                    id = oneEvent.getLong("id");
                    type = oneEvent.getInt("type");

                    /// Date fun
                    strTemp = oneEvent.getString("date_end");

                    if(strTemp.equals("null")) {
                        date_end = null;
                    } else {
                        date_end_long = getDateAPI(strTemp);
                        date_end = new Date(date_end_long);
                    }
                    strTemp = oneEvent.getString("date_start");
                    date_start_long = getDateAPI(strTemp);
                    date_start = new Date(date_start_long);

                    if (date_end == null){
                        duration = DateFormat.format("kk:mm", date_start) + " - ∞";
                    } else {
                        duration  = DateFormat.format("kk:mm", date_start) + " - " +DateFormat.format("kk:mm", date_end);
                    }
                    //////
                    ///course id cut fun
                    strTemp = description.substring(0,description.indexOf(' '));
                    if(strTemp.matches("[-+]?\\d*\\.?\\d+")){
                        courseId = Long.parseLong(strTemp);
                    } else {
                        courseId = 0L;
                    }
                    ///

                    AgendaEvent agendaEvent = realm.createObject(AgendaEvent.class);

                    agendaEvent.setCourseId(courseId);
                    agendaEvent.setDate_end(date_end);
                    agendaEvent.setDate_start(date_start);
                    agendaEvent.setDuration(duration);
                    agendaEvent.setDate_start_long(date_start_long);
                    agendaEvent.setDate_end_long(date_end_long);
                    agendaEvent.setDescription(description);
                    agendaEvent.setId(id);
                    agendaEvent.setEventString(oneEvent.toString());
                    agendaEvent.setSupertitle(supertitle);
                    agendaEvent.setType(type);
                    agendaEvent.setTitle(title);
                    // TODO: add calendar
                    if(getReminderState(context)) {
                        long startTime = date_start.getTime();

                        long endTime = 0;
                        if(date_end == null) {
                            // add 90 minutes
                            endTime = startTime + 90*60*1000;
                        } else {
                            endTime = date_end.getTime();
                        }
                        long eventId = addEvent(context,startTime,endTime,title,"me@B class reminder",supertitle);
                        agendaEvent.setCalendarId(eventId);
                        setReminder(context,eventId,10);
                    }


                    agendaList.add(agendaEvent);
                }
                day.setAgendaEvents(agendaList);
                realm.commitTransaction();
                ///////
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        realm.close();
    }

    private static long getDateAPI(String dateAPI) {
        return Long.parseLong(dateAPI.substring(dateAPI.indexOf('(')+1,dateAPI.indexOf('+')));
    }

    /* REWRITE
    public static void getAgendaEventList(List<CalendarEvent> eventList, Context mContext) {
        /////

        Realm realm = Realm.getDefaultInstance();
        RealmResults<EventDay> days = realm.where(EventDay.class).findAll();
        //TODO: only days in scope
        //Log.d(LogConst.LOG, Integer.toString(days.size()));
        for(EventDay day: days) {
            //Log.d(LogConst.LOG,day.getDate().toString());
            RealmList<AgendaEvent> events = day.getAgendaEvents();
            for(AgendaEvent event: events) {

                Calendar startTime = Calendar.getInstance();
                Calendar endTime = Calendar.getInstance();
                String dateString = event.getDuration();
                startTime.setTimeInMillis(event.getDate_start_long());
                endTime.setTimeInMillis(event.getDate_end_long());

                BocconiCalendarEvent eventBocconi = new BocconiCalendarEvent(event.getTitle(), event.getDescription(), event.getSupertitle(),
                        getColorWrapper(mContext, R.color.orange_dark), startTime, endTime, false, dateString);
                eventList.add(eventBocconi);
            }
        }
        realm.close();

    }

    public static void writeAgendaCalendarViewPersistence(List<CalendarEvent> doneEvents, List<IDayItem> doneDays,List<IWeekItem> doneWeeks) {

        Realm realm = Realm.getDefaultInstance();
        long currentTime = System.currentTimeMillis();
        realm.beginTransaction();

        ////// clear the realm
        realm.delete(RDay.class);
        realm.delete(RCal.class);
        realm.delete(REvent.class);
        realm.delete(RWeek.class);
        ///////
        RCal rCal = realm.createObject(RCal.class);


        // Days
        RealmList<RDay> rDays = new RealmList<>();
        for (IDayItem day : doneDays) {
            RDay rDay = realm.createObject(RDay.class);
            rDay.setDate(day.getDate());
            rDay.setValue(day.getValue());
            rDay.setDayOfTheWeek(day.getDayOfTheWeek());
            rDay.setFirstDayOfTheMonth(day.isFirstDayOfTheMonth());
            rDay.setSelected(day.isSelected());
            rDay.setMonth(day.getMonth());
            rDays.add(rDay);
        }
        rCal.setrDays(rDays);

        RealmList<RWeek> rWeeks = new RealmList<>();
        for (IWeekItem week : doneWeeks) {
            RWeek rWeek = realm.createObject(RWeek.class);

            rWeek.setWeekInYear(week.getWeekInYear());
            rWeek.setYear(week.getYear());
            rWeek.setMonth(week.getMonth());
            rWeek.setDate(week.getDate());
            rWeek.setLabel(week.getLabel());

            RealmList<RDay> rWDays = new RealmList<>();
            for(IDayItem day : week.getDayItems()) {
                RDay wday  = realm.where(RDay.class).equalTo("date", day.getDate()).findFirst();
                rWDays.add(wday);
            }
            rWeek.setDayItemsR(rWDays);
            rWeeks.add(rWeek);
        }
        rCal.setrWeeks(rWeeks);

        RealmList<REvent> rEvents = new RealmList<>();
        for (CalendarEvent basicevent : doneEvents) {
            BocconiCalendarEvent castEvent = (BocconiCalendarEvent) basicevent;
            REvent rEvent = realm.createObject(REvent.class);
            rEvent.setId(castEvent.getId());
            rEvent.setColor(castEvent.getColor());
            rEvent.setAllDay(castEvent.isAllDay());
            rEvent.setDuration(castEvent.getDuration());
            rEvent.setTitle(castEvent.getTitle());
            rEvent.setDescription(castEvent.getDescription());
            rEvent.setLocation(castEvent.getLocation());
            Calendar startTime = castEvent.getStartTime();
            if(startTime!= null) {
                rEvent.setStartTimeD(startTime.getTime());
            }

            Calendar endTime = castEvent.getEndTime();
            if(endTime!= null) {
                rEvent.setEndTimeD(endTime.getTime());
            }
            rEvent.setDate(castEvent.getDate());

            rEvent.setInstanceDayD(castEvent.getInstanceDay().getTime());
            rEvent.setPlaceholder(castEvent.isPlaceHolder());

            RDay eday  = realm.where(RDay.class).equalTo("date", castEvent.getDayReference().getDate()).findFirst();
            rEvent.setDayReference(eday);
            RWeek eweek = realm.where(RWeek.class).equalTo("date", castEvent.getWeekReference().getDate()).findFirst();
            rEvent.setWeekReference(eweek);
            rEvents.add(rEvent);
        }
        rCal.setrEvents(rEvents);
        rCal.setLastUpdated(currentTime);

        realm.commitTransaction();
        realm.close();
    }


    // support v4 workaround
    private static int getColorWrapper(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return ContextCompat.getColor(context,id);
        }
    }
    */
}
