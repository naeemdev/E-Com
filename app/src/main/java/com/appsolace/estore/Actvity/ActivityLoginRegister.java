package com.appsolace.estore.Actvity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.appsolace.estore.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonObject;
import com.appsolace.estore.Fragment.RegisterFragment;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.Fragment.LoginFragment;
import com.appsolace.estore.callback.CallbackInternate;
import com.appsolace.estore.callback.CallbackMessage;
import com.appsolace.estore.connection.RestAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLoginRegister extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private Context context;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.img_cancle)
    ImageView img_cancle;

     Pager adapter;

    Dialog ProgressDialog;

    //Shared Preferences
    private SharedPreferences userPref;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        FacebookSdk.sdkInitialize(context);
        context = this;
        ButterKnife.bind(this);
        initData();

    }

    private void initData() {

        userPref = PreferenceManager.getDefaultSharedPreferences(ActivityLoginRegister.this);

        ProgressDialog = new Dialog(ActivityLoginRegister.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);

        //Tab-Layout
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.Login)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.Registration)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //coding for add divider in tabLayout

        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.gray));
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(20);
        linearLayout.setDividerDrawable(drawable);

        changeTabsFont();
        adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);
        setupTabIcons();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });

    }


    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText(getString(R.string.Login));
        // tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.driveractive, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);
        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(getString(R.string.Registration));
        //  tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.legle, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);


    }

    private void changeTabsFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {

            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    Typeface customFont = Typeface.createFromAsset(ActivityLoginRegister.this.getAssets(), "fonts/" + getString(R.string.ubuntu_b));
                    ((TextView) tabViewChild).setTypeface(customFont, Typeface.NORMAL);
                }
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public class Pager extends FragmentStatePagerAdapter {

        //integer to count number of tabs
        int tabCount;

        private String[] tabTitles = new String[]{getString(R.string.Login), getString(R.string.Registration)};

        //Constructor to the class
        public Pager(FragmentManager fm, int tabCount) {
            super(fm);
            //Initializing tab count
            this.tabCount = tabCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        //Overriding method getItem
        @Override
        public Fragment getItem(int position) {
            //Returning the current tabs
            switch (position) {
                case 0:
                    LoginFragment loginFragment = new LoginFragment();
                    return loginFragment;
                case 1:
                    RegisterFragment registerFragment = new RegisterFragment();
                    return registerFragment;

                default:
                    return null;
            }
        }

        //Overriden method getCount to get the number of tabs
        @Override
        public int getCount() {
            return tabCount;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void fbLogin() {

        boolean isFacebookLogin = AccessToken.getCurrentAccessToken() != null;
        if (Common.isNetworkAvailable(ActivityLoginRegister.this)) {
            if (isFacebookLogin) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.e("TaxiBooking", "Facebook object = " + object + "==" + response);
                                if (object != null) {
                                    //facebook get data
                                    try {
                                        Common.facebook_id = object.getString("id");

                                        if (object.has("email"))
                                            Common.facebook_email = object.getString("email");
                                        if (object.has("name"))
                                            Common.facebook_name = object.getString("name");


                                        ProgressDialog.show();

                                        API api = RestAdapter.createAPI();
                                        Call<JsonObject> callbackCall = api.social_login(object.getString("id"));
                                        callbackCall.enqueue(new Callback<JsonObject>() {
                                            @Override
                                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                JsonObject resp = response.body();
                                                Log.e("TaxiBooking", "Facebook Response>>>" + resp);

                                                if (resp != null) {

                                                    if (ProgressDialog != null && ProgressDialog.isShowing())
                                                        ProgressDialog.dismiss();

                                                    if (resp != null && resp.get("status").getAsString().equals("success")) {


                                                        if (resp.has("tax") && resp.get("tax").isJsonNull()) {
                                                            String tax = resp.get("tax").getAsString();

                                                            SharedPreferences.Editor sh = userPref.edit();
                                                            sh.putString("TAX", tax);
                                                            sh.commit();
                                                        }


                                                        JsonObject userdetail;

                                                        if (resp.has("userdetail") && resp.get("userdetail").isJsonObject())

                                                            userdetail = resp.get("userdetail").getAsJsonObject();
                                                        else
                                                            userdetail = null;


                                                        if (userdetail != null) {

                                                            String u_id = "", name = "", facebook_id = "", phone_number = "", email = "";
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

                                                            if (userdetail.has("email") && !userdetail.get("email").isJsonNull()) {
                                                                email = userdetail.get("email").getAsString();
                                                            }

                                                            SharedPreferences.Editor sh = userPref.edit();
                                                            sh.putString("uid", u_id);
                                                            sh.putBoolean("islogin", true);
                                                            sh.putString("name", name);
                                                            sh.putString("email", email);
                                                            sh.putString("password", "");
                                                            sh.putString("phonenumber", phone_number);
                                                            sh.putString("facebookid", facebook_id);
                                                            sh.commit();

                                                            //requestHomeCategory();

                                                            if (ProgressDialog != null && ProgressDialog.isShowing())
                                                                ProgressDialog.dismiss();


                                                            Intent i = new Intent(ActivityLoginRegister.this, MainActivity.class);
                                                            startActivity(i);
                                                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                                                        } else {

                                                            if (ProgressDialog != null && ProgressDialog.isShowing())
                                                                ProgressDialog.dismiss();

                                                            ErrorMessage(getString(R.string.error));
                                                        }

                                                    } else if (resp.get("status").getAsString().equals("false")) {

                                                        //Common.showError(ActivityLoginRegister.this, resp.get("message").toString());

                                                        FacebookRegi(getString(R.string.facebook_popup_string));
                                                    }

                                                } else {
                                                    if (ProgressDialog != null && ProgressDialog.isShowing())
                                                        ProgressDialog.dismiss();

                                                    ErrorMessage(getString(R.string.error));
                                                }


                                            }

                                            @Override
                                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                                Log.e("onFailure", t.getMessage());
                                                if (ProgressDialog != null && ProgressDialog.isShowing())
                                                    ProgressDialog.dismiss();
                                                String message = "";
                                                if (t instanceof SocketTimeoutException) {
                                                    message = "Socket Time out. Please try again.";
                                                    Log.e("onFailure", message);
                                                    Common.ShowHttpErrorMessage(ActivityLoginRegister.this, message);
                                                }
                                            }

                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else

                                {
                                    Toast.makeText(ActivityLoginRegister.this, "Something went wrong", Toast.LENGTH_LONG);
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

            } else {
                callbackManager = CallbackManager.Factory.create();
                //LoginManager.getInstance().logInWithPublishPermissions(ActivityLoginRegister.this, Arrays.asList("publish_actions"));
                LoginManager.getInstance().logInWithReadPermissions(ActivityLoginRegister.this, Arrays.asList("public_profile"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        Log.e("TaxiBooking", "loginResult = " + loginResult);
                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.d("object", "object = " + object + "==" + response);
                                        if (object != null) {
                                            //facebook get data
                                            try {
                                                Common.facebook_id = object.getString("id");
                                                if (object.has("email"))
                                                    Common.facebook_email = object.getString("email");
                                                if (object.has("name"))
                                                    Common.facebook_name = object.getString("name");

                                                ProgressDialog.show();

                                                API api = RestAdapter.createAPI();
                                                Call<JsonObject> callbackCall = api.social_login(object.getString("id"));
                                                callbackCall.enqueue(new Callback<JsonObject>() {
                                                    @Override
                                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                        JsonObject resp = response.body();
                                                        Log.e("TaxiBooking", "Facebook Response>>>" + resp);

                                                        if (resp != null) {

                                                            if (ProgressDialog != null && ProgressDialog.isShowing())
                                                                ProgressDialog.dismiss();

                                                            if (resp.get("status").getAsString().equals("success")) {

                                                                if (resp.has("tax") && resp.get("tax").isJsonNull()) {
                                                                    String tax = resp.get("tax").getAsString();

                                                                    SharedPreferences.Editor sh = userPref.edit();
                                                                    sh.putString("TAX", tax);
                                                                    sh.commit();
                                                                }


                                                                JsonObject userdetail;

                                                                if (resp.has("userdetail") && resp.get("userdetail").isJsonObject())

                                                                    userdetail = resp.get("userdetail").getAsJsonObject();
                                                                else
                                                                    userdetail = null;


                                                                if (userdetail != null) {

                                                                    String u_id = "", name = "", facebook_id = "", phone_number = "", email = "";
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

                                                                    if (userdetail.has("email") && !userdetail.get("email").isJsonNull()) {
                                                                        email = userdetail.get("email").getAsString();
                                                                    }

                                                                    SharedPreferences.Editor sh = userPref.edit();
                                                                    sh.putString("uid", u_id);
                                                                    sh.putBoolean("islogin", true);
                                                                    sh.putString("name", name);
                                                                    sh.putString("email", email);
                                                                    sh.putString("password", "");
                                                                    sh.putString("phonenumber", phone_number);
                                                                    sh.putString("facebookid", facebook_id);
                                                                    sh.commit();

                                                                    //requestHomeCategory();

                                                                    if (ProgressDialog != null && ProgressDialog.isShowing())
                                                                        ProgressDialog.dismiss();


                                                                   Intent i = new Intent(ActivityLoginRegister.this, MainActivity.class);
                                                                    startActivity(i);
                                                                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                                                                } else {

                                                                    if (ProgressDialog != null && ProgressDialog.isShowing())
                                                                        ProgressDialog.dismiss();

                                                                    ErrorMessage(getString(R.string.error));
                                                                }


                                                            } else if (resp.get("status").getAsString().equals("false"))
                                                            {
                                                                //Common.showError(ActivityLoginRegister.this, resp.get("message").toString());

                                                                FacebookRegi(getString(R.string.facebook_popup_string));
                                                            }
                                                        }
                                                        else {

                                                            if (ProgressDialog != null && ProgressDialog.isShowing())
                                                                ProgressDialog.dismiss();

                                                            ErrorMessage(getString(R.string.error));
                                                        }

                                                    }

                                                    @Override
                                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                                        Log.e("onFailure", t.getMessage());
                                                        if (ProgressDialog != null && ProgressDialog.isShowing())
                                                            ProgressDialog.dismiss();
                                                        String message = "";
                                                        if (t instanceof SocketTimeoutException) {
                                                            message = "Socket Time out. Please try again.";
                                                            Log.e("onFailure", message);
                                                            Common.ShowHttpErrorMessage(ActivityLoginRegister.this, message);
                                                        }
                                                    }

                                                });

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            Toast.makeText(ActivityLoginRegister.this, "Something went wrong", Toast.LENGTH_LONG);
                                        }


                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.e("cancel", "cancel = ");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("fb error", "fb error = " + exception.getMessage());
                        System.out.println("exception >>" + exception.getMessage());
                    }
                });
            }

        } else
        {
            Internate();
        }

    }

    private void Internate() {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(ActivityLoginRegister.this).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private void ErrorMessage(String Message) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(ActivityLoginRegister.this).buildDialogMessage(new CallbackMessage() {
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

    private void FacebookRegi(String Message) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(ActivityLoginRegister.this).buildDialogRegister(new CallbackMessage() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(1);
            }

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }
        }, Message);
        dialog.show();
    }
}
