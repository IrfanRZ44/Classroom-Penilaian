package com.exomatik.irfanrz.penilaian.Authentication.register;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by IrfanRZ on 06/09/2018.
 */

public class AddUserPresenter implements AddUserContract.Presenter, AddUserContract.OnUserDatabaseListener{
    private AddUserContract.View mView;
    private AddUserInteractor mAddUserInteractor;

    public AddUserPresenter(AddUserContract.View view) {
        this.mView = view;
        mAddUserInteractor = new AddUserInteractor(this);
    }

    @Override
    public void addUser(Context context, FirebaseUser firebaseUser, String nama, String nim, String idMhs) {
        mAddUserInteractor.addUserToDatabase(context, firebaseUser, nama, nim, idMhs);
    }

    @Override
    public void onSuccess(String message) {
        mView.onAddUserSuccess(message);
    }

    @Override
    public void onFailure(String message) {
        mView.onAddUserFailure(message);
    }

}
