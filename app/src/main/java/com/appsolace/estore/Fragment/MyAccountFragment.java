package com.appsolace.estore.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.appsolace.estore.R;
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

import static com.appsolace.estore.Utili.Utils.isValidPass;


public class MyAccountFragment extends Fragment implements View.OnClickListener {

    SharedPreferences sPref;

    @BindView(R.id.edt_name)
    CustomEditTextView edt_name;

    @BindView(R.id.txt_email)
    CustomTextView txt_email;

    @BindView(R.id.txt_update)
    CustomTextView txt_update;

    @BindView(R.id.edt_current_pass)
    CustomEditTextView edt_current_pass;

    @BindView(R.id.img_cur_pass)
    ImageView img_cur_pass;

    @BindView(R.id.edt_new_pass)
    CustomEditTextView edt_new_pass;

    @BindView(R.id.img_new_pass)
    ImageView img_new_pass;

    @BindView(R.id.edt_confim_pass)
    CustomEditTextView edt_confim_pass;

    @BindView(R.id.img_canfim_pass)
    ImageView img_canfim_pass;

    @BindView(R.id.txt_change_password)
    CustomTextView txt_change_password;

    Dialog ProgressDialog;

    private boolean isPasswordShow, isConfirmPasswordShow, Isnewpasswordshow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myaccount_fragment, container, false);
        ButterKnife.bind(this, view);

        initdata();

        return view;
    }

    public void initdata() {

        sPref = PreferenceManager.getDefaultSharedPreferences(Common.Activity);

        ProgressDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);

        edt_name.setText(sPref.getString("name", ""));
        txt_email.setText(sPref.getString("email", ""));

        AddTextChangeListener(edt_current_pass, "password");
        AddTextChangeListener(edt_new_pass, "newpassword");
        AddTextChangeListener(edt_confim_pass, "confirmpassword");

        txt_update.setOnClickListener(this);
        txt_change_password.setOnClickListener(this);
        img_cur_pass.setOnClickListener(this);
        img_new_pass.setOnClickListener(this);
        img_canfim_pass.setOnClickListener(this);
    }

    public void AddTextChangeListener(final EditText edit, final String field) {

        edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (field.equals("password")) {
                    if (count > 0)
                        img_cur_pass.setVisibility(View.VISIBLE);
                    else
                        img_cur_pass.setVisibility(View.GONE);

                } else if (field.equals("newpassword")) {
                    if (count > 0)
                        img_new_pass.setVisibility(View.VISIBLE);
                    else
                        img_new_pass.setVisibility(View.GONE);

                } else if (field.equals("confirmpassword")) {
                    if (count > 0)
                        img_canfim_pass.setVisibility(View.VISIBLE);
                    else
                        img_canfim_pass.setVisibility(View.GONE);

                }

            }
        });
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.txt_update) {
            if (edt_name.getText().toString().trim().equals("")) {
                edt_name.setError(Common.Activity.getString(R.string.enter_username));
                edt_name.requestFocus();
            } else {

                if (Common.isNetworkAvailable(Common.Activity)) {
                    requestUpdateprofile(edt_name.getText().toString().trim());
                } else {
                    GetUpdateprofile(edt_name.getText().toString().trim());
                }

            }
        } else if (id == R.id.txt_change_password) {

            if (edt_current_pass.getText().toString().length() == 0) {
                edt_current_pass.setError(Common.Activity.getString(R.string.enter_cur_password));
                edt_current_pass.requestFocus();
            } else if (!edt_current_pass.getText().toString().equals(sPref.getString("password", ""))) {
                edt_current_pass.setError(Common.Activity.getResources().getString(R.string.enter_curpass_notmatch));
                edt_current_pass.requestFocus();
            } else if (edt_new_pass.getText().toString().equals("")) {
                edt_new_pass.setError(Common.Activity.getString(R.string.enter_new_password));
                edt_new_pass.requestFocus();
            } else if (edt_new_pass.getText().toString().length() < 6 || edt_new_pass.getText().toString().length() > 32) {
                edt_new_pass.setError(Common.Activity.getResources().getString(R.string.password_length));
                edt_new_pass.requestFocus();
            } else if (!isValidPass(edt_new_pass.getText().toString().trim())) {
                edt_new_pass.setError(Common.Activity.getResources().getString(R.string.password_valid));
                edt_new_pass.requestFocus();
            } else if (!edt_confim_pass.getText().toString().equals(edt_new_pass.getText().toString())) {
                edt_confim_pass.setError(Common.Activity.getString(R.string.enter_newpass_notmatch));
                edt_confim_pass.requestFocus();
            } else {
                if (Common.isNetworkAvailable(Common.Activity)) {
                    requestChangePassword(edt_new_pass.getText().toString().trim());
                } else {
                    GetChangePassword(edt_new_pass.getText().toString().trim());
                }
            }
        } else if (id == R.id.img_cur_pass) {
            if (isPasswordShow) {

                isPasswordShow = false;

                int resID = getResources().getIdentifier("@drawable/hide_password", "drawable", getActivity().getPackageName());
                img_cur_pass.setImageResource(resID);

                edt_current_pass.setTransformationMethod(new PasswordTransformationMethod());
                edt_current_pass.setSelection(edt_current_pass.getText().length());

            } else {

                isPasswordShow = true;

                int resID = getResources().getIdentifier("@drawable/show_password", "drawable", getActivity().getPackageName());
                img_cur_pass.setImageResource(resID);

                edt_current_pass.setTransformationMethod(null);
                edt_current_pass.setSelection(edt_current_pass.getText().length());

            }
        } else if (id == R.id.img_new_pass) {
            if (isConfirmPasswordShow) {

                isConfirmPasswordShow = false;

                int resID = getResources().getIdentifier("@drawable/hide_password", "drawable", getActivity().getPackageName());
                img_new_pass.setImageResource(resID);

                edt_new_pass.setTransformationMethod(new PasswordTransformationMethod());
                edt_new_pass.setSelection(edt_new_pass.getText().length());

            } else {

                isConfirmPasswordShow = true;

                int resID = getResources().getIdentifier("@drawable/show_password", "drawable", getActivity().getPackageName());
                img_new_pass.setImageResource(resID);

                edt_new_pass.setTransformationMethod(null);
                edt_new_pass.setSelection(edt_new_pass.getText().length());

            }
        } else if (id == R.id.img_canfim_pass) {
            if (isConfirmPasswordShow) {

                isConfirmPasswordShow = false;

                int resID = getResources().getIdentifier("@drawable/hide_password", "drawable", getActivity().getPackageName());
                img_canfim_pass.setImageResource(resID);

                edt_confim_pass.setTransformationMethod(new PasswordTransformationMethod());
                edt_confim_pass.setSelection(edt_confim_pass.getText().length());

            } else {

                isConfirmPasswordShow = true;

                int resID = getResources().getIdentifier("@drawable/show_password", "drawable", getActivity().getPackageName());
                img_canfim_pass.setImageResource(resID);

                edt_confim_pass.setTransformationMethod(null);
                edt_confim_pass.setSelection(edt_confim_pass.getText().length());

            }
        }
    }

    private void requestUpdateprofile(final String username) {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();
        Call<JsonObject> callback_login = api.edit_profile(sPref.getString("uid", ""), username, "1");
        callback_login.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("Login onResponse", "=>" + response.body());

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();

                  /*  {"status":"success","message":"Successfully Logged in","code":"success","tax":"4.5","Isactive":"1","userdetail":{"u_id":"2272","token":"","is_device":"1"}}*/

                    if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {


                        JsonObject userdetail;

                        if (jsonObject.has("user_detail") && jsonObject.get("user_detail").isJsonObject())

                            userdetail = jsonObject.get("user_detail").getAsJsonObject();
                        else
                            userdetail = null;


                        if (userdetail != null) {

                            String name = "", email = "";

                            if (userdetail.has("name") && !userdetail.get("name").isJsonNull()) {
                                name = userdetail.get("name").getAsString();
                            }

                            if (userdetail.has("email") && !userdetail.get("email").isJsonNull()) {
                                email = userdetail.get("email").getAsString();
                            }


                            SharedPreferences.Editor sh = sPref.edit();
                            sh.putBoolean("islogin", true);
                            sh.putString("name", name);
                            sh.putString("email", email);
                            sh.commit();

                            //requestHomeCategory();

                            if (ProgressDialog != null && ProgressDialog.isShowing())
                                ProgressDialog.dismiss();


                            // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
                            Dialog dialog = new DialogUtils(Common.Activity).buildDialogMessage(new CallbackMessage() {
                                @Override
                                public void onSuccess(Dialog dialog) {
                                    dialog.dismiss();

                                    Intent iReceiver = new Intent("com.megastore.updatename");
                                    getActivity().sendBroadcast(iReceiver);
                                }

                                @Override
                                public void onCancel(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            }, Common.Activity.getResources().getString(R.string.profile_succes));
                            dialog.show();


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
                            Common.AccountLock(Common.Activity);
                        }
                        else if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                            Common.Errordialog(Common.Activity,jsonObject.get("message").toString());
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
                    Common.ShowHttpErrorMessage(Common.Activity, message);
                }
            }
        });

    }

    private void GetUpdateprofile(final String username) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(Common.Activity).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                if (Common.isNetworkAvailable(Common.Activity)) {
                    requestUpdateprofile(username);
                } else {
                    GetUpdateprofile(username);
                }
            }
        });
        dialog.show();
    }

    private void ErrorMessage(String Message) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(Common.Activity).buildDialogMessage(new CallbackMessage() {
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

    private void requestChangePassword(final String password) {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();
        Call<JsonObject> callback_login = api.change_password(sPref.getString("uid", ""), password, "1");
        callback_login.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("Login onResponse", "=>" + response.body());

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();

                  /*  {"status":"success","message":"Successfully Logged in","code":"success","tax":"4.5","Isactive":"1","userdetail":{"u_id":"2272","token":"","is_device":"1"}}*/

                    if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {


                        SharedPreferences.Editor sh = sPref.edit();
                        sh.putBoolean("islogin", true);
                        sh.putString("password", password);
                        sh.commit();


                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
                        Dialog dialog = new DialogUtils(Common.Activity).buildDialogMessage(new CallbackMessage() {
                            @Override
                            public void onSuccess(Dialog dialog) {
                                dialog.dismiss();

                                edt_current_pass.setText("");
                                edt_confim_pass.setText("");
                                edt_new_pass.setText("");
                            }

                            @Override
                            public void onCancel(Dialog dialog) {
                                dialog.dismiss();
                            }
                        }, Common.Activity.getResources().getString(R.string.changepassword_succes));
                        dialog.show();


                    } else {
                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive=0;
                        if (jsonObject.has("Isactive") && !jsonObject.get("Isactive").isJsonNull()) {
                            Isactive = jsonObject.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(Common.Activity);
                        }
                        else if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                            Common.Errordialog(Common.Activity,jsonObject.get("message").toString());
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
                    Common.ShowHttpErrorMessage(Common.Activity, message);
                }
            }
        });

    }

    private void GetChangePassword(final String password) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(Common.Activity).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                if (Common.isNetworkAvailable(Common.Activity)) {
                    requestChangePassword(password);
                } else {
                    GetChangePassword(password);
                }
            }
        });
        dialog.show();
    }


}
