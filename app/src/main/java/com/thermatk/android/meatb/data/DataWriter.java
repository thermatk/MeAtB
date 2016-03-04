package com.thermatk.android.meatb.data;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import com.thermatk.android.meatb.LogConst;

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

        //// Days loop
        for (int i = 0; i < response.length(); i++) {
            JSONObject oneDay = null;
            long dateLong;
            Date date;
            try {
                oneDay = response.getJSONObject(i);
                String strTemp = oneDay.getString("date");

                dateLong = Long.parseLong(strTemp.substring(strTemp.indexOf('(')+1,strTemp.indexOf('+')));
                //TODO: do some better timezone hacks
                // add 4 hours to be sure we're in the right day
                //dateLong +=  (DateUtils.HOUR_IN_MILLIS * 4);
                date = getDateAPI(strTemp);
                //
                // TODO: find previously done days and compare

                realm.beginTransaction();
                EventDay day = realm.createObject(EventDay.class);
                day.setDate(date);
                day.setDateLong(dateLong);
                day.setLastUpdated(System.currentTimeMillis());
                Log.d(LogConst.LOG,Long.toString(day.getDateLong()));
                RealmList<AgendaEvent> agendaList = new RealmList<AgendaEvent>();
                ///////Events loop
                JSONArray eventsArray = oneDay.getJSONArray("events");

                for (int j = 0; j < eventsArray.length(); j++) {

                    JSONObject oneEvent;
                    Date date_end;
                    Date date_start;
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
                        date_end = getDateAPI(strTemp);
                    }
                    strTemp = oneEvent.getString("date_start");
                    date_start = getDateAPI(strTemp);
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
                    agendaEvent.setDescription(description);
                    agendaEvent.setId(id);
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

    private static Date getDateAPI(String dateAPI) {
        return new Date(Long.parseLong(dateAPI.substring(dateAPI.indexOf('(')+1,dateAPI.indexOf('+'))));
    }
}
