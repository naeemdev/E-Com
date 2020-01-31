package com.appsolace.estore.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.gson.JsonObject;
import com.appsolace.estore.Actvity.MainActivity;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.R;
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

import static com.appsolace.estore.Utili.Utils.isValidEmail;
import static com.appsolace.estore.Utili.Utils.isValidPass;


public class RegisterFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.txtregister)
    CustomTextView txtregister;

    @BindView(R.id.edtUser)
    CustomEditTextView edtUser;

    @BindView(R.id.edtEmail)
    CustomEditTextView edtEmail;

    @BindView(R.id.edtPassword)
    CustomEditTextView edtPassword;

    @BindView(R.id.edtconfirmPassword)
    CustomEditTextView edtconfirmPassword;

    @BindView(R.id.switch_driver_status)
    Switch switch_driver_status;

    @BindView(R.id.imgPassword)
    ImageView imgPassword;

    @BindView(R.id.imgConfirmpass)
    ImageView imgConfirmpass;

    String subscrip = "0";
    private boolean isPasswordShow, isConfirmPasswordShow;

    //Shared Preferences
    private SharedPreferences userPref;

    Dialog ProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        ButterKnife.bind(this, view);

        userPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ProgressDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);


        edtUser.setText(Common.facebook_name);
        txtregister.setOnClickListener(this);
        imgPassword.setOnClickListener(this);
        imgConfirmpass.setOnClickListener(this);

        switch_driver_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    subscrip = "1";
                } else {
                    subscrip = "0";
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.txtregister) {
            if (edtUser.getText().toString().length() == 0) {
                edtUser.setError(getActivity().getString(R.string.enter_username));
                edtUser.requestFocus();
            } else if (edtEmail.getText().toString().equals("")) {
                edtEmail.setError(getActivity().getString(R.string.enter_email));
                edtEmail.requestFocus();
            } else if (!isValidEmail(edtEmail.getText().toString().trim())) {
                edtEmail.setError(getActivity().getString(R.string.enter_valid_email));
                edtEmail.requestFocus();
            } else if (edtPassword.getText().toString().length() == 0) {
                edtPassword.setError(getActivity().getString(R.string.enter_password));
                edtPassword.requestFocus();
            } else if (edtPassword.getText().toString().length() < 6 || edtPassword.getText().toString().length() > 32) {
                edtPassword.setError(getActivity().getResources().getString(R.string.password_length));
                edtPassword.requestFocus();
            } else if (!isValidPass(edtPassword.getText().toString().trim())) {
                edtPassword.setError(getActivity().getResources().getString(R.string.password_valid));
                edtPassword.requestFocus();
            } else if (edtconfirmPassword.getText().toString().equals("")) {
                edtconfirmPassword.setError(getActivity().getString(R.string.enter_cnfpassword));
                edtconfirmPassword.requestFocus();
            } else if (!edtconfirmPassword.getText().toString().equals(edtPassword.getText().toString())) {
                edtconfirmPassword.setError(getActivity().getString(R.string.enter_pass_notmatch));
                edtconfirmPassword.requestFocus();
            } else {
                //Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();

                String username = edtUser.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();


                if (Common.isNetworkAvailable(getActivity())) {
                    requestRegister(username, email, password);
                } else {
                    GetLogin(username, email, password);
                }
            }
        } else if (id == R.id.imgPassword) {
            if (isPasswordShow) {

                isPasswordShow = false;

                int resID = getResources().getIdentifier("@drawable/hide_password", "drawable", getActivity().getPackageName());
                imgPassword.setImageResource(resID);

                edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                edtPassword.setSelection(edtPassword.getText().length());

            } else {

                isPasswordShow = true;

                int resID = getResources().getIdentifier("@drawable/show_password", "drawable", getActivity().getPackageName());
                imgPassword.setImageResource(resID);

                edtPassword.setTransformationMethod(null);
                edtPassword.setSelection(edtPassword.getText().length());

            }
        } else if (id == R.id.imgConfirmpass) {
            if (isConfirmPasswordShow) {

                isConfirmPasswordShow = false;

                int resID = getResources().getIdentifier("@drawable/hide_password", "drawable", getActivity().getPackageName());
                imgConfirmpass.setImageResource(resID);

                edtconfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                edtconfirmPassword.setSelection(edtconfirmPassword.getText().length());

            } else {

                isConfirmPasswordShow = true;

                int resID = getResources().getIdentifier("@drawable/show_password", "drawable", getActivity().getPackageName());
                imgConfirmpass.setImageResource(resID);

                edtconfirmPassword.setTransformationMethod(null);
                edtconfirmPassword.setSelection(edtconfirmPassword.getText().length());

            }
        }

    }

    private void requestRegister(final String username, final String email, final String password) {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();
        Call<JsonObject> callback_login = api.Register(userPref.getString("uid", ""), username, email, password, Common.facebook_id, subscrip, "1");
        callback_login.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("Login onResponse", "=>" + response.body());

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();

                  /*  {"status":"success","message":"Successfully Logged in","code":"success","tax":"4.5","Isactive":"1","userdetail":{"u_id":"2272","token":"","is_device":"1"}}*/

                    if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {


                        if (jsonObject.has("tax") && jsonObject.get("tax").isJsonNull()) {
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


                            // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
                            Dialog dialog = new DialogUtils(getActivity()).buildDialogMessage(new CallbackMessage() {
                                @Override
                                public void onSuccess(Dialog dialog) {
                                    dialog.dismiss();

                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                    startActivity(i);
                                    getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                                }

                                @Override
                                public void onCancel(Dialog dialog) {
                                    dialog.dismiss();

                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                    startActivity(i);
                                    getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                                }
                            }, getActivity().getResources().getString(R.string.Register_succes));
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

    private void GetLogin(final String username, final String email, final String password) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(getActivity()).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                if (Common.isNetworkAvailable(getActivity())) {
                    requestRegister(username, email, password);
                } else {
                    GetLogin(username, email, password);
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
