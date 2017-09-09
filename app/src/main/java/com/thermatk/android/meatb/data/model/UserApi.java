package com.thermatk.android.meatb.data.model;

/**
 * Created by thermatk on 08.09.17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class UserApi extends RealmObject implements Parcelable {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("nome")
    @Expose
    private String nome;
    @SerializedName("cognome")
    @Expose
    private String cognome;
    @SerializedName("ruolo")
    @Expose
    private Integer ruolo;
    @SerializedName("ruoloDes")
    @Expose
    private String ruoloDes;
    @SerializedName("statoAuth")
    @Expose
    private Integer statoAuth;
    @SerializedName("statoAuthDes")
    @Expose
    private String statoAuthDes;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("appMobileBaseUrl")
    @Expose
    private String appMobileBaseUrl;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public Integer getRuolo() {
        return ruolo;
    }

    public void setRuolo(Integer ruolo) {
        this.ruolo = ruolo;
    }

    public String getRuoloDes() {
        return ruoloDes;
    }

    public void setRuoloDes(String ruoloDes) {
        this.ruoloDes = ruoloDes;
    }

    public Integer getStatoAuth() {
        return statoAuth;
    }

    public void setStatoAuth(Integer statoAuth) {
        this.statoAuth = statoAuth;
    }

    public String getStatoAuthDes() {
        return statoAuthDes;
    }

    public void setStatoAuthDes(String statoAuthDes) {
        this.statoAuthDes = statoAuthDes;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppMobileBaseUrl() {
        return appMobileBaseUrl;
    }

    public void setAppMobileBaseUrl(String appMobileBaseUrl) {
        this.appMobileBaseUrl = appMobileBaseUrl;
    }

    public static final Parcelable.Creator<UserApi> CREATOR
            = new Parcelable.Creator<UserApi>() {
        public UserApi createFromParcel(Parcel in) {
            return new UserApi(in);
        }

        public UserApi[] newArray(int size) {
            return new UserApi[size];
        }
    };

    public UserApi() {

    }

    private UserApi(Parcel parcel) {
        this.username = parcel.readString();
        this.nome = parcel.readString();
        this.cognome = parcel.readString();
        this.ruolo = parcel.readInt();
        this.ruoloDes = parcel.readString();
        this.statoAuth = parcel.readInt();
        this.statoAuthDes = parcel.readString();
        this.token = parcel.readString();
        this.appMobileBaseUrl = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.username);
        parcel.writeString(this.nome);
        parcel.writeString(this.cognome);
        parcel.writeInt(this.ruolo);
        parcel.writeString(this.ruoloDes);
        parcel.writeInt(this.statoAuth);
        parcel.writeString(this.statoAuthDes);
        parcel.writeString(this.token);
        parcel.writeString(this.appMobileBaseUrl);
    }

    @Override
    public String toString() {
        return "Username: '" + username + "', nome: '" + nome + "', cognome: '" + cognome
                + "', ruolo: '" + ruolo + "', ruoloDes: '" + ruoloDes + "', statoAuth: '" + statoAuth
                + "', statoAuthDes: '" + statoAuthDes + "', token: '" + token + "', appMobileBaseUrl: '" + appMobileBaseUrl + "'";
    }
}
