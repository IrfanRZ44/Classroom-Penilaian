package com.exomatik.irfanrz.penilaian.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.exomatik.irfanrz.penilaian.Authentication.login.loginUser;
import com.exomatik.irfanrz.penilaian.R;
import com.google.firebase.auth.FirebaseAuth;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {
    private GoogleProgressBar googleProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash_screen);

        googleProgressBar = (GoogleProgressBar) findViewById(R.id.google_progress);

        try{
            googleProgressBar.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(this).colors(getResources().getIntArray(R.array.progressLoader)).build());
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
            Log.d("Google Progress Bar", "onCreate() returned: " + e );
        }

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    Intent homeIntent = new Intent(SplashScreen.this, Menu.class);
                    startActivity(homeIntent);
                    finish();
                }
                else {
                    Intent homeIntent = new Intent(SplashScreen.this, loginUser.class);
                    startActivity(homeIntent);
                    finish();
                }
            }
        },2000);
    }

    @Override
    public void onBackPressed() {

    }
}
