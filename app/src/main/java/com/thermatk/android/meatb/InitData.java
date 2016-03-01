package com.thermatk.android.meatb;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class InitData extends RealmObject {
    @Required
    private String firstname;
    private String lastname;
    private String photo_url;
    private String carreerTitle;
    private String carreerDescription;
    private int carreerId;
    private String careerDateStart;

    private int lastUpdate;
    private String rawData;
}
