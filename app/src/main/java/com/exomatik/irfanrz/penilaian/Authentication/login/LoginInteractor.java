package com.exomatik.irfanrz.penilaian.Authentication.login;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.exomatik.irfanrz.penilaian.DataModel.User;
import com.exomatik.irfanrz.penilaian.FirebaseClass.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import static android.content.ContentValues.TAG;

/**
 * Created by IrfanRZ on 05/09/2018.
 */

class LoginInteractor implements LoginContract.Interactor {
    private LoginContract.OnLoginListener mOnLoginListener;
    private Activity activity2;

    public LoginInteractor(LoginContract.OnLoginListener onLoginListener) {
        this.mOnLoginListener = onLoginListener;
    }

    @Override
    public void performFirebaseLogin(final Activity activity, final String email, String password) {
        activity2 = activity;

        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "performFirebaseLogin:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the User. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in User can be handled in the listener.
                        if (task.isSuccessful()) {
                            updateFirebaseToken(task.getResult().getUser().getUid(),
                                    new SharedPrefUtil(activity.getApplicationContext()).getString("firebaseToken", null));

                            cekTypeUser();
                        } else {
                            mOnLoginListener.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateFirebaseToken(String uid, String token) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(uid)
                .child("firebaseToken")
                .setValue(token);
    }

    private void cekTypeUser() {
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    User user = null;
                    user = dataSnapshotChild.getValue(User.class);

                    if (FirebaseAuth.getInstance().getCurrentUser().getUid().toString().equals(user.uid)) {
                        new SharedPrefUtil(activity2).saveString("type", user.typeUser.toString());
                        mOnLoginListener.onSuccess("Login Berhasil");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
