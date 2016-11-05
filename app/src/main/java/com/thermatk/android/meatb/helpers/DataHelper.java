package com.thermatk.android.meatb.helpers;

import android.content.Context;
import android.text.format.DateFormat;

import com.thermatk.android.meatb.data.AgendaEvent;
import com.thermatk.android.meatb.data.EventDay;
import com.thermatk.android.meatb.data.InboxMessage;
import com.thermatk.android.meatb.data.InitData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        long id;
        if (isInboxInitial()) {
            id = 1;
        } else {
            id = realm.where(InboxMessage.class).findAll().max("id").longValue();
        }

        for (int i = 0; i < response.length(); i++) {
            JSONObject oneMessage;
            try {
                oneMessage = response.getJSONObject(i);
                if(existsInboxInternalId(realm, oneMessage.getLong("id"))) {
                    // TODO: whatif id is the same but content changed?
                } else {
                    realm.beginTransaction();

                    InboxMessage message = realm.createObject(InboxMessage.class, id);///// TODO: do not forget about the id problem
                    id++;
                    fillInboxMessage(message, oneMessage);
                    message.setLastUpdated(currentTime);

                    realm.commitTransaction();
                }
                ///////
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        realm.close();
    }

    public static boolean existsInboxInternalId(Realm realm, long internalId) {
        RealmResults<InboxMessage> results = realm.where(InboxMessage.class).equalTo("internalId", internalId).findAll();
        if (results.size() == 0) {
            return false;
        }
        return true;
    }

    public static void fillInboxMessage (InboxMessage message, JSONObject oneMessage) throws JSONException {
        long dateLong;
        Date date;
        String strTemp = oneMessage.getString("date_sent");

        dateLong = getDateAPI(strTemp);
        //TODO: do some better timezone hacks
        // add 4 hours to be sure we're in the right day
        //dateLong +=  (DateUtils.HOUR_IN_MILLIS * 4);
        date = new Date(dateLong);

        message.setDate(date);
        message.setDateLong(dateLong);
        message.setInternalId(oneMessage.getLong("id"));
        message.setDescription(oneMessage.getString("description"));
        message.setTitle(oneMessage.getString("title"));
        message.setFavorite(oneMessage.getBoolean("is_favorite"));
        message.setFeatured(oneMessage.getBoolean("is_featured"));
        message.setUnread(oneMessage.getBoolean("is_unread"));
        message.setSupertitle(oneMessage.getString("supertitle"));
        message.setDownloadedFull(false);

    }

    public static boolean isInboxInitial() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<InboxMessage> results = realm.where(InboxMessage.class).findAll(); // TODO: another case of whatif there is actually nothing?
        boolean answer = true;
        if(results.size()>0) {
            answer = false;
        }
        realm.close();
        return answer;
    }

    public static boolean isAgendaInitial(Context context) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<EventDay> results = realm.where(EventDay.class).findAll(); // TODO: another case of whatif there is actually nothing?
        boolean answer = true;
        if(results.size()>0) {
            answer = false;
        }
        realm.close();
        return answer;
    }

    public static void wipeAgenda(Context context) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        realm.where(EventDay.class).findAll().deleteAllFromRealm();
        realm.where(AgendaEvent.class).findAll().deleteAllFromRealm();

        realm.commitTransaction();
        realm.close();
    }

    public static void writeAgendaDataInitial(JSONArray response, Context context) {



        Realm realm = Realm.getDefaultInstance();
        long currentTime = System.currentTimeMillis();
        boolean reminders = getReminderState(context);
        //// Days loop
        for (int i = 0; i < response.length(); i++) {
            JSONObject oneDay;
            try {
                oneDay = response.getJSONObject(i);
                //
                // TODO: find previously done days and compare

                realm.beginTransaction();

                EventDay day = realm.createObject(EventDay.class, i+1);///// TODO: do not forget about the id problem
                fillEventDay(day, oneDay);
                day.setLastUpdated(currentTime);

                RealmList<AgendaEvent> agendaList = new RealmList<>();
                ///////Events loop
                JSONArray eventsArray = oneDay.getJSONArray("events");

                for (int j = 0; j < eventsArray.length(); j++) {

                    JSONObject oneEvent;
                    oneEvent = eventsArray.getJSONObject(j);
                    AgendaEvent agendaEvent = realm.createObject(AgendaEvent.class);
                    fillAgendaEvent(agendaEvent, oneEvent);
                    if(reminders) {
                        remindAgendaEvent(context, agendaEvent);
                    }

                    agendaEvent.setHeader(day);
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

    public static void remindAgendaEvent(Context context, AgendaEvent agendaEvent) {
        // TODO: maybe someone wants calendar filled, but no reminders? Easy to separate
        long startTime = agendaEvent.getDate_start().getTime();

        long endTime = 0;
        if(agendaEvent.getDate_end() == null) {
            // add 90 minutes
            endTime = startTime + 90*60*1000;
        } else {
            endTime = agendaEvent.getDate_end().getTime();
        }
        long eventId = addEvent(context,startTime,endTime,agendaEvent.getTitle(),"me@B class reminder",agendaEvent.getSupertitle());
        agendaEvent.setCalendarId(eventId);
        setReminder(context,eventId,10);
    }

    public static void fillEventDay(EventDay day, JSONObject oneDay) throws JSONException {
        String strTemp = oneDay.getString("date");

        long dateLong;
        Date date;

        dateLong = getDateAPI(strTemp);
        //TODO: do some better timezone hacks
        // add 4 hours to be sure we're in the right day
        //dateLong +=  (DateUtils.HOUR_IN_MILLIS * 4);
        date = new Date(dateLong);

        day.setDate(date);
        day.setDateLong(dateLong);
        day.setDateString(new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(date)); // TODO: LOCALE! or not?
        String weekday = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);

        day.setWeekdayString(weekday.substring(0, 1).toUpperCase() + weekday.substring(1));

        day.setDayString(oneDay.toString());

    }
    public static void fillAgendaEvent(AgendaEvent agendaEvent, JSONObject oneEvent) throws JSONException {
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
        String strTemp;

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
    */
}
