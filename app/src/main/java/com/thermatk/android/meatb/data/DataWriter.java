package com.thermatk.android.meatb.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.realm.Realm;

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
        data.setLastUpdated(System.currentTimeMillis() / 1000L);
        realm.commitTransaction();
        realm.close();
    }
}
