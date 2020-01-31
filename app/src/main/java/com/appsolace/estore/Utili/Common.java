package com.appsolace.estore.Utili;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsolace.estore.Actvity.ActivityLoginRegister;
import com.appsolace.estore.Model.FilterProduct.FilterData;
import com.appsolace.estore.Model.HomeData.HomeData;
import com.appsolace.estore.Model.Menu2.Menu2Data;
import com.appsolace.estore.Model.Menu1.Menu1Data;
import com.appsolace.estore.Model.Menu3.Menu3Data;
import com.appsolace.estore.Model.ProductListModel.ProductItem;
import com.appsolace.estore.R;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.callback.CallbackMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class Common {


    public static final String TAG = "Activity";

    public static Activity Activity;

    public static String facebook_id, facebook_name="", facebook_email="";

    public static ArrayList<HashMap<String, String>> menu_list = new ArrayList<HashMap<String, String>>();

    public static HomeData homeData = new HomeData();
    public static Menu1Data menu1Data = new Menu1Data();
    public static Menu2Data menu2Data = new Menu2Data();
    public static Menu3Data menu3Data = new Menu3Data();

    public static List<ProductItem> list_ProductItem;
    public static String product_sort_flag;

    public static String filtermenu1;
    public static String filtermenu2;
    public static String filtermenu3;
    public static String filtermenu4;
    public static String filtermenu5;

    public static FilterData filterData = new FilterData();

    public static boolean isNetworkAvailable(Activity act) {

        ConnectivityManager connMgr = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            return true;
        } else {
            // display error
            return false;
        }

    }

    public static boolean ShowHttpErrorMessage(Activity activity, String ErrorMessage) {

        Log.d("ErrorMessage", "ErrorMessage = " + ErrorMessage);
        boolean Status = true;
        if (ErrorMessage != null && !ErrorMessage.equals("")) {
            if (ErrorMessage.contains("Connect to")) {
                Common.showMkError(activity, "failed to connect to");
                Status = false;
            } else if (ErrorMessage.contains("failed to connect to")) {
                Common.showMkError(activity, "failed to connect to");
                Status = false;
            } else if (ErrorMessage.contains("Internal Server Error")) {
                Common.showMkError(activity, "Internal Server Error");
                Status = false;
            } else if (ErrorMessage.contains("Request Timeout")) {
                Common.showMkError(activity, "Request Timeout");
                Status = false;
            } else if (ErrorMessage.contains("Request Timeout")) {
                Common.showMkError(activity, "Something Went Wrong ");
                Status = false;
            } else {
                Common.showMkError(activity, "Something Went Wrong ");
                Status = false;
            }
        } else {
            // Toast.makeText(activity, "Server Time Out", Toast.LENGTH_LONG).show();
            Common.showMkError(activity, "Something Went Wrong ");
            Status = false;
        }
        return Status;
    }

    public static void showMkError(final Activity act, final String error_code) {
        String message = "";
        if (!act.isFinishing()) {
            Log.d("error_code", "error_code = " + error_code);

            if (error_code.equals("1000")) {
                message = act.getResources().getString(R.string.error);
            } else {
                message = act.getResources().getString(R.string.error);
            }

            final SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(act);

            Animation slideUpAnimation;

            final Dialog MKInfoPanelDialog = new Dialog(act, android.R.style.Theme_Translucent_NoTitleBar);

            MKInfoPanelDialog.setContentView(R.layout.mkinfopanel);
            MKInfoPanelDialog.show();
            slideUpAnimation = AnimationUtils.loadAnimation(act.getApplicationContext(),
                    R.anim.slide_up_map);

            RelativeLayout layout_info_panel = (RelativeLayout) MKInfoPanelDialog.findViewById(R.id.layout_info_panel);
            layout_info_panel.startAnimation(slideUpAnimation);

            RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) act.getResources().getDimension(R.dimen.height_40));
            buttonLayoutParams.setMargins(0, (int) act.getResources().getDimension(R.dimen.height_50), 0, 0);
            layout_info_panel.setLayoutParams(buttonLayoutParams);

            TextView subtitle = (TextView) MKInfoPanelDialog.findViewById(R.id.subtitle);
            subtitle.setText(message);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (MKInfoPanelDialog.isShowing() && !act.isFinishing())
                            MKInfoPanelDialog.cancel();

                        if (error_code.equals("1") || error_code.equals("5")) {
                            SharedPreferences.Editor editor = userPref.edit();
                            editor.clear();
                            editor.commit();

//                            Intent logInt = new Intent(act, LoginOptionActivity.class);
//                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            act.startActivity(logInt);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2000);

        }
    }

    public static void showError(final Activity act, final String message) {
      ;
        if (!act.isFinishing()) {
            Log.d("error_message", "error_message = " + message);


            final SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(act);

            Animation slideUpAnimation;

            final Dialog MKInfoPanelDialog = new Dialog(act, android.R.style.Theme_Translucent_NoTitleBar);

            MKInfoPanelDialog.setContentView(R.layout.mkinfopanel);
            MKInfoPanelDialog.show();
            slideUpAnimation = AnimationUtils.loadAnimation(act.getApplicationContext(),
                    R.anim.slide_up_map);

            RelativeLayout layout_info_panel = (RelativeLayout) MKInfoPanelDialog.findViewById(R.id.layout_info_panel);
            layout_info_panel.startAnimation(slideUpAnimation);

            RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) act.getResources().getDimension(R.dimen.height_40));
            buttonLayoutParams.setMargins(0, (int) act.getResources().getDimension(R.dimen.height_50), 0, 0);
            layout_info_panel.setLayoutParams(buttonLayoutParams);

            TextView subtitle = (TextView) MKInfoPanelDialog.findViewById(R.id.subtitle);
            subtitle.setText(message);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                        if (MKInfoPanelDialog.isShowing() && !act.isFinishing())
                            MKInfoPanelDialog.cancel();

                }
            }, 2000);

        }
    }

    public static void ClearData() {
        Common.homeData = new HomeData();
        Common.menu1Data = new Menu1Data();
        Common.menu2Data = new Menu2Data();
        Common.menu3Data = new Menu3Data();
        list_ProductItem = new ArrayList<>();
    }

    public static void AccountLock(final Activity activity) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(activity).buildDialogMessage(new CallbackMessage() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(activity);
                SharedPreferences.Editor sh = sPref.edit();
                sh.putString("uid", "");
                sh.putBoolean("islogin", false);
                sh.putString("name", "");
                sh.putString("email", "");
                sh.putString("password", "");
                sh.putString("phonenumber", "");
                sh.putString("facebookid", "");
                sh.putInt("Cart", 0);
                sh.putInt("wishlist", 0);
                sh.commit();
                sh.clear();


                Intent i = new Intent(activity, ActivityLoginRegister.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(i);

            }

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }
        }, activity.getResources().getString(R.string.account_lock));
        dialog.show();
    }

    public static void Errordialog(final Activity activity,String Message) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(activity).buildDialogMessage(new CallbackMessage() {
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
