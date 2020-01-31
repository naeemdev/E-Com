package com.appsolace.estore.Actvity;

import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appsolace.estore.R;
import com.google.gson.JsonObject;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.View.ThirdScreenView;
import com.appsolace.estore.callback.CallbackInternate;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.callback.CallbackMessage;
import com.appsolace.estore.View.ThirdScreenView1;
import com.appsolace.estore.connection.RestAdapter;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Intro_Viewpager_Activity extends AppCompatActivity {

    Dialog ProgressDialog;
    ProgressBar progressBar;
    SharedPreferences userPref;

    private static final int NUM_PAGES = 2;

    public static ViewPager mPager;
    private LinearLayout mIndicatorLayout;
    private TextView mIndicatorView[];
    private Drawable mPagerBackground;
    private static AnimatorSet mAnimatorSet;

    private int mSelectedPosition = -1;

    //Second screen
    private float mPreviousPositionOffset;
    private static boolean mViewPagerScrollingLeft;
    private int mPreviousPosition;

    // Third screen
    private static boolean mShouldSpheresRotate = true;
    private static ThirdScreenView mRoundView;
    private static ThirdScreenView1 mRoundView1;

    private boolean mThirdPageSelected;
    private static Button mLetsGoButton;

    CustomTextView txt_continue_guest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_pager);

        setUpViews();
    }

    private void setUpViews() {

        userPref = PreferenceManager.getDefaultSharedPreferences(Intro_Viewpager_Activity.this);

        ProgressDialog = new Dialog(Intro_Viewpager_Activity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);
        progressBar = (ProgressBar) ProgressDialog.findViewById(R.id.progress_circular);


        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerBackground = mPager.getBackground();
        mIndicatorLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        mPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        setIndicatorLayout();
        setPageChangeListener(mPager);
        mPager.bringToFront();

        txt_continue_guest = (CustomTextView) findViewById(R.id.txt_continue_guest);
        txt_continue_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isNetworkAvailable(Intro_Viewpager_Activity.this)) {
                    requestGuestLogin();
                } else {
                    GetGuestLogin();
                }
            }
        });
    }

    private void GetGuestLogin() {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(Intro_Viewpager_Activity.this).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                if (Common.isNetworkAvailable(Intro_Viewpager_Activity.this)) {
                    requestGuestLogin();
                } else {
                    GetGuestLogin();
                }
            }
        });
        dialog.show();
    }

    private void setIndicatorLayout() {

        int dotsCount = NUM_PAGES;
        mIndicatorView = new TextView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {

            mIndicatorView[i] = new TextView(this);
            mIndicatorView[i].setWidth((int) getResources().getDimension(R.dimen.slide_dot_size));
            mIndicatorView[i].setHeight((int) getResources().getDimension(R.dimen.slide_dot_size));
            mIndicatorView[i].setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, (int) getResources().getDimension(R.dimen.slide_margin_10), 0);
            mIndicatorView[i].setLayoutParams(params);
            mIndicatorView[i].setBackgroundResource(R.drawable.rounded_cell_gray);
            mIndicatorLayout.addView(mIndicatorView[i]);

        }

        //mIndicatorView[0].setWidth(20);
        //mIndicatorView[0].setHeight(20);
        mIndicatorView[0].setBackgroundResource(R.drawable.rounded_cell_red);
        mIndicatorView[0].setGravity(Gravity.CENTER);
    }

    private void setPageChangeListener(ViewPager viewPager) {

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int oldPos = mPager.getCurrentItem();

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                // Scrollling left or right
                if ((positionOffset > mPreviousPositionOffset && position == mPreviousPosition) || (positionOffset < mPreviousPositionOffset && position > mPreviousPosition)) {
                    mViewPagerScrollingLeft = true;
                } else if (positionOffset < mPreviousPositionOffset) {

                    mViewPagerScrollingLeft = false;
                }
                mPreviousPositionOffset = positionOffset;
                mPreviousPosition = position;

                // FADE the indicator layout
                if (position == 1 && mViewPagerScrollingLeft) {

                    mIndicatorLayout.setAlpha(1 - positionOffset);
                } else if (position == 1 && !mViewPagerScrollingLeft) {

                    mIndicatorLayout.setAlpha(1 - positionOffset);
                }

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 1) {
                    mSelectedPosition = 1;

                    if (mAnimatorSet != null) {
                        mAnimatorSet.cancel();
                    }

                    //animateBookView();
                }
                if (position == 0) {
                    mSelectedPosition = 0;
                    // doFadeAnimation();

                }

                for (int i = 0; i < mIndicatorView.length; i++) {
                    mIndicatorView[i].setBackgroundResource(R.drawable.rounded_cell_gray);
                }
                mIndicatorView[position].setBackgroundResource(R.drawable.rounded_cell_red);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    mShouldSpheresRotate = false;
                } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mShouldSpheresRotate = true;
                }
                if (mRoundView != null) {
                    mRoundView.setRotatingPermission(mShouldSpheresRotate);
                }

                if (mRoundView1 != null) {
                    mRoundView1.setRotatingPermission(mShouldSpheresRotate);
                }


                if (mSelectedPosition == 0 && state == ViewPager.SCROLL_STATE_IDLE) {
                }
            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            ScreenSlideFragment fragment = new ScreenSlideFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public static class CustomTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {


            int pageWidth = page.getWidth();

            if (!mShouldSpheresRotate && page.findViewById(R.id.center_box_third) != null) {
                mRoundView.translateTheSpheres(position, pageWidth);
            }

            if (!mShouldSpheresRotate && page.findViewById(R.id.center_box_third1) != null) {
                mRoundView1.translateTheSpheres(position, pageWidth);
            }

            //mRoundView.translateTheSpheres(position, pageWidth);

        }
    }

    public static class ScreenSlideFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            Bundle args = getArguments();
            int position = args.getInt("position");
            int layoutId = getLayoutId(position);


            ViewGroup rootView = (ViewGroup) inflater.inflate(layoutId, container, false);
            if (position == 0) {
                initThirdScreenViews(rootView, savedInstanceState);
                //initFirstScreenViews(rootView, savedInstanceState);
            }
            if (position == 1) {
                initThirdScreenViews1(rootView, savedInstanceState);
                // initSecondScreenViews(rootView, savedInstanceState);
            }

            return rootView;
        }

        private int getLayoutId(int position) {

            int id = 0;
            if (position == 0) {
                id = R.layout.welcome_screen;
            } else if (position == 1) {
                id = R.layout.second_screen;
            }
            return id;
        }

    }

    public static void getOriginalXValues(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mPager.setPageTransformer(true, new CustomTransformer());
        }
    }

    private static void initThirdScreenViews(View rootView, final Bundle savedInstanceState) {

        mRoundView = (ThirdScreenView) rootView.findViewById(R.id.round_view);

        rootView.post(new Runnable() {
            @Override
            public void run() {
                getOriginalXValues(savedInstanceState);
            }
        });
    }

    private static void initThirdScreenViews1(View rootView, final Bundle savedInstanceState) {

        mRoundView1 = (ThirdScreenView1) rootView.findViewById(R.id.round_view);
        rootView.post(new Runnable() {
            @Override
            public void run() {
                getOriginalXValues(savedInstanceState);
            }
        });
    }

    private void requestGuestLogin() {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();
        Call<JsonObject> callback_guestlogin = api.GuestLogin("", "", 1);
        callback_guestlogin.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("GuestLogin onResponse", "=>" + response.body());

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();

                  /*  {"status":"success","message":"Successfully Logged in","code":"success","tax":"4.5","Isactive":"1","userdetail":{"u_id":"2272","token":"","is_device":"1"}}*/

                    if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {

                        JsonObject userdetail;

                        if (jsonObject.has("userdetail") && jsonObject.get("userdetail").isJsonObject())

                            userdetail = jsonObject.get("userdetail").getAsJsonObject();
                        else
                            userdetail = null;


                        if (userdetail != null) {

                            if (userdetail.has("u_id") && !userdetail.get("u_id").isJsonNull()) {
                                String u_id = userdetail.get("u_id").getAsString();

                                SharedPreferences.Editor sh = userPref.edit();
                                sh.putString("uid", u_id);
                                sh.putBoolean("islogin", false);
                                sh.commit();

                                //requestHomeCategory();

                                if (ProgressDialog != null && ProgressDialog.isShowing())
                                    ProgressDialog.dismiss();


                                Intent i = new Intent(Intro_Viewpager_Activity.this, MainActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                            }
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
                            Common.AccountLock(Intro_Viewpager_Activity.this);
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
                    Common.ShowHttpErrorMessage(Intro_Viewpager_Activity.this, message);
                }
            }
        });

    }

    private void ErrorMessage(String Message) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(Intro_Viewpager_Activity.this).buildDialogMessage(new CallbackMessage() {
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
