package com.thermatk.android.meatb.data;

import android.text.format.DateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;

public class DataWriter {
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
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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
    }

    public static void writeAgendaData(JSONArray response) {



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
                EventDay day = realm.createObject(EventDay.class);
                day.setDate(date);
                day.setId(i + 1); ///// TODO: do not forget about the id problem
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
}
