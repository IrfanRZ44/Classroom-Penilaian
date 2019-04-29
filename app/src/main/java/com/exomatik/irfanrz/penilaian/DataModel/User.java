package com.exomatik.irfanrz.penilaian.DataModel;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by IrfanRZ on 06/09/2018.
 */

@IgnoreExtraProperties
public class User {
    public String uid;
    public String email;
    public String firebaseToken;
    public String typeUser;
    public String nama;
    public String nimMhs;
    public String idMhs;

    public User() {
    }

    public User(String uid, String email, String firebaseToken, String typeUser, String nama, String nimMhs, String idMhs) {
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
        this.typeUser = typeUser;
        this.nama = nama;
        this.nimMhs = nimMhs;
        this.idMhs = idMhs;
    }
}
