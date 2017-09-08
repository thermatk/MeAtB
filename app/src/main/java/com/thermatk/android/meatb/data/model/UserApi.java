package com.thermatk.android.meatb.data.model;

/**
 * Created by thermatk on 08.09.17.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserApi {

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

}
