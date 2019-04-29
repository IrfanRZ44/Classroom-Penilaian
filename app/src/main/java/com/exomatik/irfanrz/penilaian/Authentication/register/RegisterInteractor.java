package com.exomatik.irfanrz.penilaian.Authentication.register;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by IrfanRZ on 06/09/2018.
 */

public class RegisterInteractor implements RegisterContract.Interactor {
    private static final String TAG = RegisterInteractor.class.getSimpleName();
    private Activity act;

    private RegisterContract.OnRegistrationListener mOnRegistrationListener;

    public RegisterInteractor(RegisterContract.OnRegistrationListener onRegistrationListener) {
        this.mOnRegistrationListener = onRegistrationListener;
    }

    @Override
    public void performFirebaseRegistration(Activity activity, final String email, String password
            , final String nama, final String nim, final String id) {
        act = activity;
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "performFirebaseRegistration:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the User. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in User can be handled in the listener.
                        if (!task.isSuccessful()) {
                            mOnRegistrationListener.onFailure(task.getException().getMessage());
                        } else {
                            // Add the User to users table.
                            /*DatabaseReference database= FirebaseDatabase.getInstance().getReference();
                            User User = new User(task.getResult().getUser().getUid(), email);
                            database.child("users").push().setValue(User);*/

                            mOnRegistrationListener.onSuccess(task.getResult().getUser(), nama, nim, id);
                        }
                    }
                });
    }

}
