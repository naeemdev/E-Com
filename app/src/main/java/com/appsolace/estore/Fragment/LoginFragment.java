package com.appsolace.estore.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.appsolace.estore.Actvity.ActivityLoginRegister;
import com.appsolace.estore.Actvity.MainActivity;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.CustomEditTextView;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.callback.CallbackInternate;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.R;
import com.appsolace.estore.callback.CallbackMessage;
import com.appsolace.estore.connection.RestAdapter;

import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class LoginFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.edtEmail_login)
    CustomEditTextView edtEmail_login;

    @BindView(R.id.edtPassword_login)
    CustomEditTextView edtPassword_login;

    @BindView(R.id.txtForgotPassword)
    CustomTextView txtForgotPassword;

    @BindView(R.id.txtLogin)
    CustomTextView txtLogin;

    @BindView(R.id.layoutFBLogin)
    LinearLayout layoutFBLogin;

    @BindView(R.id.imgPassword)
    ImageView imgPassword;

    //Shared Preferences
    private SharedPreferences userPref;

    Dialog ProgressDialog;

    private boolean isConfirmPasswordShow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        ButterKnife.bind(this, view);

        userPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ProgressDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);


        String udata = txtForgotPassword.getText().toString();
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        txtForgotPassword.setText(content);

        txtLogin.setOnClickListener(this);
        layoutFBLogin.setOnClickListener(this);
        imgPassword.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.txtLogin) {
            if (edtEmail_login.getText().toString().length() == 0) {
                edtEmail_login.setError(getActivity().getString(R.string.enter_username));
                edtEmail_login.requestFocus();
            } else if (edtPassword_login.getText().toString().length() == 0) {
                edtPassword_login.setError(getActivity().getString(R.string.enter_password));
                edtPassword_login.requestFocus();
            } else {
                //Toast.makeText(activity, " Login Success", Toast.LENGTH_SHORT).show();

                if (Common.isNetworkAvailable(getActivity())) {
                    requestLogin(edtEmail_login.getText().toString().trim(), edtPassword_login.getText().toString().trim());
                } else {
                    GetLogin(edtEmail_login.getText().toString().trim(), edtPassword_login.getText().toString().trim());
                }
            }
        } else if (v.getId() == R.id.layoutFBLogin) {

            ((ActivityLoginRegister) getActivity()).fbLogin();
        }
        else if (v.getId() == R.id.imgPassword) {
            if (isConfirmPasswordShow) {

                isConfirmPasswordShow = false;

                int resID = getResources().getIdentifier("@drawable/hide_password", "drawable", getActivity().getPackageName());
                imgPassword.setImageResource(resID);

                edtPassword_login.setTransformationMethod(new PasswordTransformationMethod());
                edtPassword_login.setSelection(edtPassword_login.getText().length());

            } else {

                isConfirmPasswordShow = true;

                int resID = getResources().getIdentifier("@drawable/show_password", "drawable", getActivity().getPackageName());
                imgPassword.setImageResource(resID);

                edtPassword_login.setTransformationMethod(null);
                edtPassword_login.setSelection(edtPassword_login.getText().length());

            }
        }

    }

    private void requestLogin(final String email, final String password) {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();
        Call<JsonObject> callback_login = api.Login(email, password);
        callback_login.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("Login onResponse", "=>" + response.body());

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();

                  /*  {"status":"success","message":"Successfully Logged in","code":"success","tax":"4.5","Isactive":"1","userdetail":{"u_id":"2272","token":"","is_device":"1"}}*/

                    if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {


                        if (jsonObject.has("tax") && !jsonObject.get("tax").isJsonNull()) {
                            String tax = jsonObject.get("tax").getAsString();

                            SharedPreferences.Editor sh = userPref.edit();
                            sh.putString("TAX", tax);
                            sh.commit();
                        }


                        JsonObject userdetail;

                        if (jsonObject.has("userdetail") && jsonObject.get("userdetail").isJsonObject())

                            userdetail = jsonObject.get("userdetail").getAsJsonObject();
                        else
                            userdetail = null;


                        if (userdetail != null) {

                            String u_id = "", name = "", facebook_id = "", phone_number = "";
                            if (userdetail.has("u_id") && !userdetail.get("u_id").isJsonNull()) {
                                u_id = userdetail.get("u_id").getAsString();
                            }

                            if (userdetail.has("name") && !userdetail.get("name").isJsonNull()) {
                                name = userdetail.get("name").getAsString();
                            }

                            if (userdetail.has("phone_number") && !userdetail.get("phone_number").isJsonNull()) {
                                phone_number = userdetail.get("name").getAsString();
                            }

                            if (userdetail.has("facebook_id") && !userdetail.get("facebook_id").isJsonNull()) {
                                facebook_id = userdetail.get("facebook_id").getAsString();
                            }


                            SharedPreferences.Editor sh = userPref.edit();
                            sh.putString("uid", u_id);
                            sh.putBoolean("islogin", true);
                            sh.putString("name", name);
                            sh.putString("email", email);
                            sh.putString("password", password);
                            sh.putString("phonenumber", phone_number);
                            sh.putString("facebookid", facebook_id);
                            sh.commit();

                            //requestHomeCategory();

                            if (ProgressDialog != null && ProgressDialog.isShowing())
                                ProgressDialog.dismiss();


                            Intent i = new Intent(getActivity(), MainActivity.class);
                            startActivity(i);
                            getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                        } else {

                            if (ProgressDialog != null && ProgressDialog.isShowing())
                                ProgressDialog.dismiss();

                            ErrorMessage(getString(R.string.error));
                        }

                    } else {
                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive=0;
                        if (jsonObject.has("Isactive") && !jsonObject.get("Isactive").isJsonNull()) {
                            Isactive = jsonObject.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(getActivity());
                        }
                        else if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                            ErrorMessage(jsonObject.get("message").toString());
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
                    Common.ShowHttpErrorMessage(getActivity(), message);
                }
            }
        });

    }

    private void GetLogin(final String email, final String password) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(getActivity()).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                if (Common.isNetworkAvailable(getActivity())) {
                    requestLogin(email, password);
                } else {
                    GetLogin(email, password);
                }
            }
        });
        dialog.show();
    }

    private void ErrorMessage(String Message) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(getActivity()).buildDialogMessage(new CallbackMessage() {
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



}