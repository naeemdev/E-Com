package com.appsolace.estore.Actvity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.appsolace.estore.R;
import com.google.gson.JsonObject;
import com.appsolace.estore.Model.Address;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.CustomEditTextView;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.callback.CallbackInternate;
import com.appsolace.estore.callback.CallbackMessage;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.connection.RestAdapter;

import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityAddAddress extends AppCompatActivity implements View.OnClickListener {

    //Shared Preferences
    private SharedPreferences sPref;

    Dialog ProgressDialog;

    @BindView(R.id.ly_back)
    RelativeLayout ly_back;

    @BindView(R.id.txt_title)
    CustomTextView txt_title;

    @BindView(R.id.edt_name)
    CustomEditTextView edt_name;

    @BindView(R.id.edt_address)
    CustomEditTextView edt_address;

    @BindView(R.id.edt_pincode)
    CustomEditTextView edt_pincode;

    @BindView(R.id.edt_city)
    CustomEditTextView edt_city;

    @BindView(R.id.edt_state)
    CustomEditTextView edt_state;

    @BindView(R.id.edt_phone)
    CustomEditTextView edt_phone;

    @BindView(R.id.txt_add_address)
    CustomTextView txt_add_address;

    boolean isEdit = false;
    String add_id = "";

    Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ButterKnife.bind(this);

        sPref = PreferenceManager.getDefaultSharedPreferences(ActivityAddAddress.this);

        initdata();
    }

    private void initdata() {

        isEdit = sPref.getBoolean("isEdit", false);

        if (isEdit) {
            txt_title.setText(getString(R.string.edit_address));
            txt_add_address.setText("" + getString(R.string.edit_address1));
        } else {
            txt_title.setText(getString(R.string.add_address));
            txt_add_address.setText("" + getString(R.string.add_address1));
        }

        if (isEdit) {
            Intent intent = getIntent();
            address = (Address) intent.getSerializableExtra("Address");

            edt_name.setText(address.getName());
            edt_address.setText(address.getAddress());
            edt_pincode.setText(address.getPincode());
            edt_city.setText(address.getCity());
            edt_state.setText(address.getState());
            edt_phone.setText(address.getMobile_no());
            add_id=address.getAddress_id();
        }


        ProgressDialog = new Dialog(ActivityAddAddress.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);
        //progressBar = (ProgressBar) ProgressDialog.findViewById(R.id.progress_circular);

        ly_back.setOnClickListener(this);
        txt_add_address.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ly_back:

                SharedPreferences.Editor sh = sPref.edit();
                sh.putBoolean("Add", false);
                sh.commit();

                finish();
                break;

            case R.id.txt_add_address:

                /*Intent i = new Intent(ActivityAddAddress.this,ActivityAddressList.class);
                SharedPreferences.Editor sh = sPref.edit();
                sh.putBoolean("Add",true);
                sh.commit();
                startActivity(i);*/

                CheckValidation();
                break;
        }
    }

    public void CheckValidation() {
        if (edt_name.getText().toString().length() == 0) {
            edt_name.setError(getString(R.string.enter_name));
            edt_name.requestFocus();
        } else if (edt_address.getText().toString().equals("")) {
            edt_address.setError(getString(R.string.enter_address));
            edt_address.requestFocus();
        } else if (edt_pincode.getText().toString().equals("")) {
            edt_pincode.setError(getString(R.string.enter_pincode));
            edt_pincode.requestFocus();
        } else if (edt_city.getText().toString().equals("")) {
            edt_city.setError(getString(R.string.enter_city));
            edt_city.requestFocus();
        } else if (edt_state.getText().toString().equals("")) {
            edt_state.setError(getString(R.string.enter_state));
            edt_state.requestFocus();
        } else if (edt_phone.getText().toString().equals("")) {
            edt_phone.setError(getString(R.string.enter_phone));
            edt_phone.requestFocus();
        } else {
            //Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();

            String name = edt_name.getText().toString().trim();
            String address = edt_address.getText().toString().trim();
            String pincode = edt_pincode.getText().toString().trim();
            String city = edt_city.getText().toString().trim();
            String state = edt_state.getText().toString().trim();
            String phone = edt_phone.getText().toString().trim();


            if (isEdit) {
                if (Common.isNetworkAvailable(ActivityAddAddress.this)) {
                    requestEdtAddress(name, address, pincode, city, state, phone);
                } else {
                    Getretrive(name, address, pincode, city, state, phone);
                }
            } else {
                if (Common.isNetworkAvailable(ActivityAddAddress.this)) {
                    requestAddAddress(name, address, pincode, city, state, phone);
                } else {
                    Getretrive(name, address, pincode, city, state, phone);
                }
            }

        }
    }

    private void requestAddAddress(final String name, final String address, final String pincode, final String city, final String state, final String phone) {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();
        Call<JsonObject> callback_login = api.add_address(sPref.getString("uid", ""), name, address, pincode, city, state, phone);
        callback_login.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("Login onResponse", "=>" + response.body());

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();

                    /*{"message":"address add successfully","status":"success","Isactive":"1"}*/

                    if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        SharedPreferences.Editor sh = sPref.edit();
                        sh.putBoolean("Add", true);
                        sh.commit();

                        finish();

                    } else {
                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive=0;
                        if (jsonObject.has("Isactive") && !jsonObject.get("Isactive").isJsonNull()) {
                            Isactive = jsonObject.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(ActivityAddAddress.this);
                        }
                        else if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                            Common.Errordialog(ActivityAddAddress.this,jsonObject.get("message").toString());
                        }
                    }

                } else {

                    if (ProgressDialog != null && ProgressDialog.isShowing())
                        ProgressDialog.dismiss();

                    ErrorMessage(getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                if (ProgressDialog != null && ProgressDialog.isShowing())
                    ProgressDialog.dismiss();

                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(ActivityAddAddress.this, message);
                }
            }
        });

    }

    private void requestEdtAddress(final String name, final String address, final String pincode, final String city, final String state, final String phone) {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();
        Call<JsonObject> callback_login = api.edit_address(sPref.getString("uid", ""), name, address, pincode, city, state, phone, add_id);
        callback_login.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("Login onResponse", "=>" + response.body());

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();

                    if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {


                        /*{"message":"address update successfully","status":"success","Isactive":"1"}*/

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();


                        SharedPreferences.Editor sh = sPref.edit();
                        sh.putBoolean("Add", true);
                        sh.commit();

                        finish();

                    } else {
                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive=0;
                        if (jsonObject.has("Isactive") && !jsonObject.get("Isactive").isJsonNull()) {
                            Isactive = jsonObject.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(ActivityAddAddress.this);
                    }
                        else if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                            Common.Errordialog(ActivityAddAddress.this,jsonObject.get("message").toString());
                        }
                    }

                } else {

                    if (ProgressDialog != null && ProgressDialog.isShowing())
                        ProgressDialog.dismiss();

                    ErrorMessage(getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                if (ProgressDialog != null && ProgressDialog.isShowing())
                    ProgressDialog.dismiss();

                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(ActivityAddAddress.this, message);
                }
            }
        });

    }

    private void Getretrive(final String name, final String address, final String pincode, final String city, final String state, final String phone) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(ActivityAddAddress.this).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                if (isEdit) {
                    if (Common.isNetworkAvailable(ActivityAddAddress.this)) {
                        requestEdtAddress(name, address, pincode, city, state, phone);
                    } else {
                        Getretrive(name, address, pincode, city, state, phone);
                    }
                } else {
                    if (Common.isNetworkAvailable(ActivityAddAddress.this)) {
                        requestAddAddress(name, address, pincode, city, state, phone);
                    } else {
                        Getretrive(name, address, pincode, city, state, phone);
                    }
                }
            }
        });
        dialog.show();
    }

    private void ErrorMessage(String Message) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(ActivityAddAddress.this).buildDialogMessage(new CallbackMessage() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }
        }, Message);
        dialog.show();
    }

    @Override
    public void onBackPressed() {

        ly_back.performClick();
    }
}
