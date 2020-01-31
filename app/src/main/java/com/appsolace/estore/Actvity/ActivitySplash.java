package com.appsolace.estore.Actvity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.appsolace.estore.R;

public class ActivitySplash extends AppCompatActivity {

    SharedPreferences userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        userPref = PreferenceManager.getDefaultSharedPreferences(ActivitySplash.this);

        if (!userPref.getString("email", "").equalsIgnoreCase("") && !userPref.getString("password", "").equalsIgnoreCase(""))
        {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(ActivitySplash.this, MainActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                }
            }, 1500);

        }
        else {

            if (userPref.getString("uid", "").equalsIgnoreCase("") && !userPref.getBoolean("islogin", false)) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(ActivitySplash.this, Intro_Viewpager_Activity.class));//Main_activity
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    }
                }, 1500);

            } else {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(ActivitySplash.this, MainActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    }
                }, 1500);


            }
        }
    }


}
