package com.appsolace.estore.Actvity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.appsolace.estore.R;
import com.appsolace.estore.View.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityThank extends AppCompatActivity {

    //Shared Preferences
    private SharedPreferences sPref;

    Dialog ProgressDialog;

    @BindView(R.id.txt_thank_orderid)
    CustomTextView txt_thank_orderid;

    @BindView(R.id.txt_thank_phone)
    CustomTextView txt_thank_phone;

    @BindView(R.id.txt_view_order_history)
    CustomTextView txt_view_order_history;

    @BindView(R.id.txt_go_home)
    CustomTextView txt_go_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank);
        ButterKnife.bind(this);

        sPref = PreferenceManager.getDefaultSharedPreferences(ActivityThank.this);

        initdata();
    }

    private void initdata() {

        txt_thank_orderid.setText(getString(R.string.thank_order_id) + " " + sPref.getInt("order_id", 0));
        txt_thank_phone.setText(getString(R.string.thank_phone) + " " + sPref.getString("mobile", "") + " " + getString(R.string.thank_confirm_order));

        txt_view_order_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ActivityThank.this,MainActivity.class);
                SharedPreferences.Editor sh = sPref.edit();
                sh.putInt("Cart", 0);
                sh.putInt("order_id", 0);
                sh.putString("mobile", "");
                sh.putBoolean("IsOrder",true);
                sh.commit();
                startActivity(i);
            }
        });


        txt_go_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ActivityThank.this,MainActivity.class);
                SharedPreferences.Editor sh = sPref.edit();
                sh.putInt("Cart", 0);
                sh.putInt("order_id", 0);
                sh.putString("mobile", "");
                sh.putBoolean("Iscart",false);
                sh.commit();
                sh.clear();
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}

