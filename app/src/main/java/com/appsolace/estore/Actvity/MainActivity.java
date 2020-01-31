package com.appsolace.estore.Actvity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsolace.estore.Fragment.AboutUsFragment;
import com.appsolace.estore.Fragment.CancelAndReturnFragment;
import com.appsolace.estore.Fragment.DeliveryFragment;
import com.appsolace.estore.Fragment.FaqFragment;
import com.appsolace.estore.Fragment.AllProductFragment;
import com.appsolace.estore.Fragment.MenuFragment;
import com.appsolace.estore.Fragment.MyAccountFragment;
import com.appsolace.estore.Fragment.OrderHistoryFragment;
import com.appsolace.estore.Fragment.PrivacyPolicyFragment;
import com.appsolace.estore.Fragment.SecurePaymentFragment;
import com.appsolace.estore.Fragment.ShopingCartFragment;
import com.appsolace.estore.Fragment.TermConditionFragment;
import com.appsolace.estore.Fragment.TestimonialsFragment;
import com.appsolace.estore.Fragment.WishListFragment;
import com.appsolace.estore.R;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.callback.CallbackMessage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sPref;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ActionBar actionBar;

    @BindView(R.id.nav_view)
    NavigationView nav_view;
    private ActionBarDrawerToggle toggle;

    @BindView(R.id.layout_item)
    FrameLayout layout_item;

    @BindView(R.id.search_bar)
    CardView search_bar;

    @BindView(R.id.layout_menu_home)
    LinearLayout layout_menu_home;

    @BindView(R.id.img_home)
    ImageView img_home;

    @BindView(R.id.txt_menu_home)
    CustomTextView txt_menu_home;

    @BindView(R.id.layout_menu_sbc)
    LinearLayout layout_menu_sbc;

    @BindView(R.id.img_sbc)
    ImageView img_sbc;

    @BindView(R.id.txt_menu_sbc)
    CustomTextView txt_menu_sbc;

    @BindView(R.id.layout_menu_shopingcart)
    LinearLayout layout_menu_shopingcart;

    @BindView(R.id.img_shopingcart)
    ImageView img_shopingcart;

    @BindView(R.id.txt_menu_shopingcart)
    CustomTextView txt_menu_shopingcart;

    @BindView(R.id.layout_menu_wishlist)
    LinearLayout layout_menu_wishlist;

    @BindView(R.id.img_wishlist)
    ImageView img_wishlist;

    @BindView(R.id.txt_menu_wishlist)
    CustomTextView txt_menu_wishlist;

    @BindView(R.id.layout_menu_orderhistory)
    LinearLayout layout_menu_orderhistory;

    @BindView(R.id.img_orderhistory)
    ImageView img_orderhistory;

    @BindView(R.id.txt_menu_orderhistory)
    CustomTextView txt_menu_orderhistory;

    @BindView(R.id.layout_menu_testimonials)
    LinearLayout layout_menu_testimonials;

    @BindView(R.id.img_testimonials)
    ImageView img_testimonials;

    @BindView(R.id.txt_menu_testimonials)
    CustomTextView txt_menu_testimonials;

    @BindView(R.id.layout_menu_loginregister)
    LinearLayout layout_menu_loginregister;

    @BindView(R.id.img_loginregister)
    ImageView img_loginregister;

    @BindView(R.id.txt_menu_loginregister)
    CustomTextView txt_menu_loginregister;

    @BindView(R.id.layout_menu_callus)
    LinearLayout layout_menu_callus;

    @BindView(R.id.img_callus)
    ImageView img_callus;

    @BindView(R.id.txt_menu_callus)
    CustomTextView txt_menu_callus;

    @BindView(R.id.layout_menu_writeus)
    LinearLayout layout_menu_writeus;

    @BindView(R.id.img_writeus)
    ImageView img_writeus;

    @BindView(R.id.txt_menu_writeus)
    CustomTextView txt_menu_writeus;

    @BindView(R.id.layout_menu_rateapp)
    LinearLayout layout_menu_rateapp;

    @BindView(R.id.img_rateapp)
    ImageView img_rateapp;

    @BindView(R.id.txt_menu_rateapp)
    CustomTextView txt_menu_rateapp;

    @BindView(R.id.layout_menu_shareapp)
    LinearLayout layout_menu_shareapp;

    @BindView(R.id.img_shareapp)
    ImageView img_shareapp;

    @BindView(R.id.txt_menu_shareapp)
    CustomTextView txt_menu_shareapp;

    @BindView(R.id.layout_menu_facebook)
    LinearLayout layout_menu_facebook;

    @BindView(R.id.img_facebook)
    ImageView img_facebook;

    @BindView(R.id.txt_menu_facebook)
    CustomTextView txt_menu_facebook;

    @BindView(R.id.layout_menu_twitter)
    LinearLayout layout_menu_twitter;

    @BindView(R.id.img_twitter)
    ImageView img_twitter;

    @BindView(R.id.txt_menu_twitter)
    CustomTextView txt_menu_twitter;

    @BindView(R.id.layout_menu_pripoli)
    LinearLayout layout_menu_pripoli;

    @BindView(R.id.img_pripoli)
    ImageView img_pripoli;

    @BindView(R.id.txt_menu_pripoli)
    CustomTextView txt_menu_pripoli;

    @BindView(R.id.layout_menu_termscon)
    LinearLayout layout_menu_termscon;

    @BindView(R.id.img_termscon)
    ImageView img_termscon;

    @BindView(R.id.txt_menu_termscon)
    CustomTextView txt_menu_termscon;

    @BindView(R.id.layout_menu_cancellretun)
    LinearLayout layout_menu_cancellretun;

    @BindView(R.id.img_cancellretun)
    ImageView img_cancellretun;

    @BindView(R.id.txt_menu_cancellretun)
    CustomTextView txt_menu_cancellretun;

    @BindView(R.id.layout_menu_securepayment)
    LinearLayout layout_menu_securepayment;

    @BindView(R.id.img_securepayment)
    ImageView img_securepayment;

    @BindView(R.id.txt_menu_securepayment)
    CustomTextView txt_menu_securepayment;

    @BindView(R.id.layout_menu_delivary)
    LinearLayout layout_menu_delivary;

    @BindView(R.id.img_delivary)
    ImageView img_delivary;

    @BindView(R.id.txt_menu_delivry)
    CustomTextView txt_menu_delivry;

    @BindView(R.id.layout_menu_faq)
    LinearLayout layout_menu_faq;

    @BindView(R.id.img_faq)
    ImageView img_faq;

    @BindView(R.id.txt_menu_faq)
    CustomTextView txt_menu_faq;

    @BindView(R.id.layout_menu_aboutus)
    LinearLayout layout_menu_aboutus;

    @BindView(R.id.img_aboutus)
    ImageView img_aboutus;

    @BindView(R.id.txt_menu_aboutus)
    CustomTextView txt_menu_aboutus;

    @BindView(R.id.layout_menu_mylogin)
    LinearLayout layout_menu_mylogin;

    @BindView(R.id.img_mylogin)
    ImageView img_mylogin;

    @BindView(R.id.txt_menu_mylogin)
    CustomTextView txt_menu_mylogin;

    @BindView(R.id.layout_menu_logout)
    LinearLayout layout_menu_logout;

    @BindView(R.id.img_logout)
    ImageView img_logout;

    @BindView(R.id.txt_menu_logout)
    CustomTextView txt_menu_logout;

    @BindView(R.id.lay_islogin)
    LinearLayout lay_islogin;

    @BindView(R.id.txt_menu_shopingcart_count)
    CustomTextView txt_menu_shopingcart_count;

    @BindView(R.id.txt_menu_wishlist_count)
    CustomTextView txt_menu_wishlist_count;

    //Broadcast Receiver
    BroadcastReceiver myUpdatename = null;
    Boolean myUpdatenameRegistered = false;

    //Broadcast Receiver
    BroadcastReceiver UpdateCart = null;
    Boolean UpdateCartRegistered = false;

    //Broadcast Receiver
    BroadcastReceiver Wishlist = null;
    Boolean WishlistRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Common.Activity = this;
        sPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        initToolbar();
        initDrawerMenu();
        initView();

        myUpdatename = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(Common.TAG, "onReceive-user");
                try {

                    if (sPref.getBoolean("islogin", false)) {
                        txt_menu_mylogin.setText(sPref.getString("name", ""));
                        lay_islogin.setVisibility(View.VISIBLE);
                        layout_menu_loginregister.setVisibility(View.GONE);
                    } else {
                        lay_islogin.setVisibility(View.GONE);
                        layout_menu_loginregister.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        UpdateCart = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(Common.TAG, "onReceive-user");
                try {

                    if (sPref.getInt("Cart", 0) == 0) {
                        txt_menu_shopingcart_count.setText("");
                    } else {
                        txt_menu_shopingcart_count.setText("" + sPref.getInt("Cart", 0));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Wishlist = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(Common.TAG, "onReceive-user");
                try {

                    if (sPref.getInt("wishlist", 0) == 0) {
                        txt_menu_wishlist_count.setText("");
                    } else {
                        txt_menu_wishlist_count.setText("" + sPref.getInt("wishlist", 0));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void initToolbar() {

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setHomeButtonEnabled(true);
        //actionBar.setTitle(R.string.app_name);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                tv.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
                tv.setTextColor(Color.BLACK);
                Typeface titleFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + getResources().getString(R.string.ubuntu_r));
                if (tv.getText().equals(toolbar.getTitle())) {
                    tv.setTypeface(titleFont);
                    tv.setTextSize(22);
                    break;
                }
            }
        }
    }

    private void initDrawerMenu() {

        toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        toggle.setDrawerIndicatorEnabled(false);
        drawer_layout.setDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationIcon(R.drawable.menu_icon);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(Gravity.LEFT);
            }
        });

        actionBar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white_100));
        nav_view.setItemIconTintList(getResources().getColorStateList(R.color.nav_state_list));
        View header = nav_view.getHeaderView(0);


        if (sPref.getBoolean("islogin", false)) {
            txt_menu_mylogin.setText(sPref.getString("name", ""));
            lay_islogin.setVisibility(View.VISIBLE);
            layout_menu_loginregister.setVisibility(View.GONE);
        } else {
            lay_islogin.setVisibility(View.GONE);
            layout_menu_loginregister.setVisibility(View.VISIBLE);
        }

        if (sPref.getInt("wishlist", 0) == 0) {
            txt_menu_wishlist_count.setText("");
        } else {
            txt_menu_wishlist_count.setText("" + sPref.getInt("wishlist", 0));
        }

        if (sPref.getInt("Cart", 0) == 0) {
            txt_menu_shopingcart_count.setText("");
        } else {
            txt_menu_shopingcart_count.setText("" + sPref.getInt("Cart", 0));
        }


        layout_menu_home.setOnClickListener(this);
        layout_menu_sbc.setOnClickListener(this);
        layout_menu_shopingcart.setOnClickListener(this);
        layout_menu_wishlist.setOnClickListener(this);
        layout_menu_orderhistory.setOnClickListener(this);
        layout_menu_testimonials.setOnClickListener(this);
        layout_menu_loginregister.setOnClickListener(this);
        layout_menu_callus.setOnClickListener(this);
        layout_menu_writeus.setOnClickListener(this);
        layout_menu_rateapp.setOnClickListener(this);
        layout_menu_shareapp.setOnClickListener(this);
        layout_menu_facebook.setOnClickListener(this);
        layout_menu_twitter.setOnClickListener(this);
        layout_menu_pripoli.setOnClickListener(this);
        layout_menu_termscon.setOnClickListener(this);
        layout_menu_cancellretun.setOnClickListener(this);
        layout_menu_securepayment.setOnClickListener(this);
        layout_menu_delivary.setOnClickListener(this);
        layout_menu_faq.setOnClickListener(this);
        layout_menu_aboutus.setOnClickListener(this);
        layout_menu_mylogin.setOnClickListener(this);
        layout_menu_logout.setOnClickListener(this);
    }

    public void initView() {

        if (sPref.getBoolean("Iscart", false)) {
            layout_menu_shopingcart.performClick();
        }
        else if(sPref.getBoolean("IsOrder",false))
        {
            layout_menu_orderhistory.performClick();
        }
        else {
            layout_menu_home.performClick();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();

            Dialog dialog = new DialogUtils(MainActivity.this).buildDialogLogout(new CallbackMessage() {
                @Override
                public void onSuccess(Dialog dialog) {
                    dialog.dismiss();

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                @Override
                public void onCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            }, getResources().getString(R.string.exit_confirmation));
            dialog.show();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.layout_menu_home:

                selectedItem();
                img_home.setImageResource(R.drawable.ic_nav_home_active);
                txt_menu_home.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                actionBar.setTitle(R.string.app_name);
                layout_item.setVisibility(View.VISIBLE);


                MenuFragment fragment = new MenuFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.layout_item, fragment)
                        .commit();
                break;

            case R.id.layout_menu_sbc:
                selectedItem();
                img_sbc.setImageResource(R.drawable.ic_nav_products_active);
                txt_menu_sbc.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawer(GravityCompat.START);

                actionBar.setTitle(R.string.product);
                layout_item.setVisibility(View.VISIBLE);

                AllProductFragment productFragment = new AllProductFragment();
                FragmentManager productfragmentManager = getSupportFragmentManager();
                productfragmentManager.beginTransaction()
                        .replace(R.id.layout_item, productFragment)
                        .commit();

                break;

            case R.id.layout_menu_shopingcart:
                selectedItem();
                img_shopingcart.setImageResource(R.drawable.ic_shopping_cart_active);
                txt_menu_shopingcart.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawer(GravityCompat.START);

                actionBar.setTitle(R.string.ShopingCart);
                layout_item.setVisibility(View.VISIBLE);

                ShopingCartFragment shopingCartFragment = new ShopingCartFragment();
                FragmentManager cartfragmentManager = getSupportFragmentManager();
                cartfragmentManager.beginTransaction()
                        .replace(R.id.layout_item, shopingCartFragment)
                        .commit();

                break;

            case R.id.layout_menu_wishlist:
                selectedItem();
                img_wishlist.setImageResource(R.drawable.ic_nav_wish_active);
                txt_menu_wishlist.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawer(GravityCompat.START);

                actionBar.setTitle(R.string.WishList);
                layout_item.setVisibility(View.VISIBLE);

                WishListFragment wishListFragment = new WishListFragment();
                FragmentManager wishListfragmentManager = getSupportFragmentManager();
                wishListfragmentManager.beginTransaction()
                        .replace(R.id.layout_item, wishListFragment)
                        .commit();

                break;

            case R.id.layout_menu_orderhistory:
                selectedItem();
                img_orderhistory.setImageResource(R.drawable.ic_nav_history_active);
                txt_menu_orderhistory.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawer(GravityCompat.START);

                actionBar.setTitle(R.string.Orderhistory);
                layout_item.setVisibility(View.VISIBLE);

                OrderHistoryFragment orderHistoryFragment = new OrderHistoryFragment();
                FragmentManager orderHistoryfragmentManager = getSupportFragmentManager();
                orderHistoryfragmentManager.beginTransaction()
                        .replace(R.id.layout_item, orderHistoryFragment)
                        .commit();

                break;

            case R.id.layout_menu_testimonials:

                selectedItem();
                img_testimonials.setImageResource(R.drawable.ic_nav_testimonials_active);
                txt_menu_testimonials.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();
                actionBar.setTitle(R.string.Testimonials);
                layout_item.setVisibility(View.VISIBLE);

                TestimonialsFragment testimonialsFragment = new TestimonialsFragment();
                FragmentManager testimonialsfragmentManager = getSupportFragmentManager();
                testimonialsfragmentManager.beginTransaction()
                        .replace(R.id.layout_item, testimonialsFragment)
                        .commit();
                break;

            case R.id.layout_menu_loginregister:
                selectedItem();
                img_loginregister.setImageResource(R.drawable.user_icon_active);
                txt_menu_loginregister.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                Intent login = new Intent(MainActivity.this, ActivityLoginRegister.class);
                startActivity(login);

                break;

            case R.id.layout_menu_callus:
                selectedItem();
                img_callus.setImageResource(R.drawable.callus_active);
                txt_menu_callus.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                if (checkCallPermission()) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+918780784484"));
                    startActivity(callIntent);
                }

                break;

            case R.id.layout_menu_writeus:
                selectedItem();
                img_writeus.setImageResource(R.drawable.writeus_active);
                txt_menu_writeus.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                /* Fill it with Data */
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"rupen@techintegrity.in"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Customer Service");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));


                break;

            case R.id.layout_menu_rateapp:
                selectedItem();
                img_rateapp.setImageResource(R.drawable.ic_nav_rate_active);
                txt_menu_rateapp.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://thecodefactory.in/Mobile-E-Megastore-Application"));
                startActivity(browserIntent);

                break;

            case R.id.layout_menu_shareapp:
                selectedItem();
                img_shareapp.setImageResource(R.drawable.share_active);
                txt_menu_shareapp.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hey,\n" +
                        "I would this beautiful app available on store along with their full script at throw away prices. Don’t miss to grab your copy.\n" +
                        "\n" +
                        "\n" +
                        "http://thecodefactory.in/Mobile-E-Megastore-Application";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "E Megastore Mobile App");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                break;

            case R.id.layout_menu_facebook:
                selectedItem();
                img_facebook.setImageResource(R.drawable.facebook_active);
                txt_menu_facebook.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                Intent implicit_fb = new Intent(Intent.ACTION_VIEW, Uri.parse("http://techintegrity.in/"));
                startActivity(implicit_fb);

                break;

            case R.id.layout_menu_twitter:
                selectedItem();
                img_twitter.setImageResource(R.drawable.twiiter_active);
                txt_menu_twitter.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                Intent implicit_twi = new Intent(Intent.ACTION_VIEW, Uri.parse("http://techintegrity.in/"));
                startActivity(implicit_twi);
                break;

            case R.id.layout_menu_pripoli:
                selectedItem();
                img_pripoli.setImageResource(R.drawable.privacy_policy_active);
                txt_menu_pripoli.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                actionBar.setTitle(R.string.PrivacyPolicy);

                PrivacyPolicyFragment privacyPolicyFragment = new PrivacyPolicyFragment();
                FragmentManager privacyPolicyFragmentManager = getSupportFragmentManager();
                privacyPolicyFragmentManager.beginTransaction()
                        .replace(R.id.layout_item, privacyPolicyFragment)
                        .commit();

                break;

            case R.id.layout_menu_termscon:
                selectedItem();
                img_termscon.setImageResource(R.drawable.terms_active);
                txt_menu_termscon.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                actionBar.setTitle(R.string.Terms_Conditions);

                TermConditionFragment termConditionFragment = new TermConditionFragment();
                FragmentManager termConditionFragmentManager = getSupportFragmentManager();
                termConditionFragmentManager.beginTransaction()
                        .replace(R.id.layout_item, termConditionFragment)
                        .commit();
                break;

            case R.id.layout_menu_cancellretun:
                selectedItem();
                img_cancellretun.setImageResource(R.drawable.cancellation_active);
                txt_menu_cancellretun.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                actionBar.setTitle(R.string.Cancellation_Return);

                CancelAndReturnFragment cancelAndReturnFragment = new CancelAndReturnFragment();
                FragmentManager cancelAndReturnFragmentManager = getSupportFragmentManager();
                cancelAndReturnFragmentManager.beginTransaction()
                        .replace(R.id.layout_item, cancelAndReturnFragment)
                        .commit();
                break;

            case R.id.layout_menu_securepayment:
                selectedItem();
                img_securepayment.setImageResource(R.drawable.securepayment_active);
                txt_menu_securepayment.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                actionBar.setTitle(R.string.SecurePayment);

                SecurePaymentFragment securePaymentFragment = new SecurePaymentFragment();
                FragmentManager securePaymentFragmentManager = getSupportFragmentManager();
                securePaymentFragmentManager.beginTransaction()
                        .replace(R.id.layout_item, securePaymentFragment)
                        .commit();

                break;

            case R.id.layout_menu_delivary:
                selectedItem();
                img_delivary.setImageResource(R.drawable.delivery_active);
                txt_menu_delivry.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                actionBar.setTitle(R.string.Delivary);

                DeliveryFragment deliveryFragment = new DeliveryFragment();
                FragmentManager deliveryFragmentManager = getSupportFragmentManager();
                deliveryFragmentManager.beginTransaction()
                        .replace(R.id.layout_item, deliveryFragment)
                        .commit();

                break;

            case R.id.layout_menu_faq:

                selectedItem();
                img_faq.setImageResource(R.drawable.faq_active);
                txt_menu_faq.setTextColor(getResources().getColor(R.color.colorPrimary));

                drawer_layout.closeDrawers();
                actionBar.setTitle(R.string.FAQ);
                layout_item.setVisibility(View.VISIBLE);

                FaqFragment faqFragment = new FaqFragment();
                FragmentManager faqfragmentManager = getSupportFragmentManager();
                faqfragmentManager.beginTransaction()
                        .replace(R.id.layout_item, faqFragment)
                        .commit();

                break;

            case R.id.layout_menu_aboutus:

                selectedItem();
                img_aboutus.setImageResource(R.drawable.aboutus_active);
                txt_menu_aboutus.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();
                actionBar.setTitle(R.string.AboutUs);

                AboutUsFragment aboutUsFragment = new AboutUsFragment();
                FragmentManager aboutUsFragmentManager = getSupportFragmentManager();
                aboutUsFragmentManager.beginTransaction()
                        .replace(R.id.layout_item, aboutUsFragment)
                        .commit();
                break;

            case R.id.layout_menu_mylogin:

                selectedItem();
                img_mylogin.setImageResource(R.drawable.user_icon_active);
                txt_menu_mylogin.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();
                actionBar.setTitle(R.string.MyAccount);

                MyAccountFragment myAccountFragment = new MyAccountFragment();
                FragmentManager accoutfragmentManager = getSupportFragmentManager();
                accoutfragmentManager.beginTransaction()
                        .replace(R.id.layout_item, myAccountFragment)
                        .commit();

                break;


            case R.id.layout_menu_logout:

                selectedItem();
                img_logout.setImageResource(R.drawable.logout_active);
                txt_menu_logout.setTextColor(getResources().getColor(R.color.colorPrimary));
                drawer_layout.closeDrawers();

                Dialog dialog = new DialogUtils(MainActivity.this).buildDialogLogout(new CallbackMessage() {
                    @Override
                    public void onSuccess(Dialog dialog) {
                        dialog.dismiss();

                        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
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


                        Intent i = new Intent(MainActivity.this, ActivityLoginRegister.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);


                    }

                    @Override
                    public void onCancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                }, getResources().getString(R.string.logout_confirmation));
                dialog.show();

                break;

            default:
                break;
        }
    }

    private void selectedItem() {

        Common.ClearData();
        freeMemory();

        SharedPreferences.Editor sh = sPref.edit();
        sh.putBoolean("Iscart",false);
        sh.putBoolean("IsOrder",false);
        sh.putBoolean("iscall",false);
        sh.commit();

        img_home.setImageResource(R.drawable.ic_nav_home);
        txt_menu_home.setTextColor(getResources().getColor(R.color.gray2));

        img_sbc.setImageResource(R.drawable.ic_nav_products);
        txt_menu_sbc.setTextColor(getResources().getColor(R.color.gray2));

        img_shopingcart.setImageResource(R.drawable.ic_shopping_cart);
        txt_menu_shopingcart.setTextColor(getResources().getColor(R.color.gray2));

        img_wishlist.setImageResource(R.drawable.ic_nav_wish);
        txt_menu_wishlist.setTextColor(getResources().getColor(R.color.gray2));

        img_orderhistory.setImageResource(R.drawable.ic_nav_history);
        txt_menu_orderhistory.setTextColor(getResources().getColor(R.color.gray2));

        img_testimonials.setImageResource(R.drawable.ic_nav_testimonials);
        txt_menu_testimonials.setTextColor(getResources().getColor(R.color.gray2));

        img_loginregister.setImageResource(R.drawable.user_icon);
        txt_menu_loginregister.setTextColor(getResources().getColor(R.color.gray2));

        img_callus.setImageResource(R.drawable.callus_deactive);
        txt_menu_callus.setTextColor(getResources().getColor(R.color.gray2));

        img_writeus.setImageResource(R.drawable.writeus_deactive);
        txt_menu_writeus.setTextColor(getResources().getColor(R.color.gray2));

        img_rateapp.setImageResource(R.drawable.ic_nav_rate);
        txt_menu_rateapp.setTextColor(getResources().getColor(R.color.gray2));

        img_shareapp.setImageResource(R.drawable.share_deactive);
        txt_menu_shareapp.setTextColor(getResources().getColor(R.color.gray2));

        img_facebook.setImageResource(R.drawable.facebook_deactive);
        txt_menu_facebook.setTextColor(getResources().getColor(R.color.gray2));

        img_twitter.setImageResource(R.drawable.twiiter_deactive);
        txt_menu_twitter.setTextColor(getResources().getColor(R.color.gray2));

        img_pripoli.setImageResource(R.drawable.privacy_policy_deactive);
        txt_menu_pripoli.setTextColor(getResources().getColor(R.color.gray2));

        img_termscon.setImageResource(R.drawable.terms_deactive);
        txt_menu_termscon.setTextColor(getResources().getColor(R.color.gray2));

        img_cancellretun.setImageResource(R.drawable.cancellation_deactive);
        txt_menu_cancellretun.setTextColor(getResources().getColor(R.color.gray2));

        img_securepayment.setImageResource(R.drawable.securepayment_deactive);
        txt_menu_securepayment.setTextColor(getResources().getColor(R.color.gray2));

        img_delivary.setImageResource(R.drawable.delivery_deactive);
        txt_menu_delivry.setTextColor(getResources().getColor(R.color.gray2));

        img_faq.setImageResource(R.drawable.faq_deactive);
        txt_menu_faq.setTextColor(getResources().getColor(R.color.gray2));

        img_aboutus.setImageResource(R.drawable.aboutus_deactive);
        txt_menu_aboutus.setTextColor(getResources().getColor(R.color.gray2));

        img_mylogin.setImageResource(R.drawable.user_icon);
        txt_menu_mylogin.setTextColor(getResources().getColor(R.color.gray2));

        img_logout.setImageResource(R.drawable.logout);
        txt_menu_logout.setTextColor(getResources().getColor(R.color.gray2));
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();

    }

    @Override
    protected void onResume() {

        if (!myUpdatenameRegistered) {
            registerReceiver(myUpdatename, new IntentFilter("com.megastore.updatename"));
            myUpdatenameRegistered = true;
        }

        if (!UpdateCartRegistered) {
            registerReceiver(UpdateCart, new IntentFilter("com.megastore.shopingcart"));
            UpdateCartRegistered = true;
        }

        if (!WishlistRegistered) {
            registerReceiver(Wishlist, new IntentFilter("com.megastore.wishlist"));
            WishlistRegistered = true;
        }

        super.onResume();
    }

    @Override
    protected void onPause() {

       /* if (myUpdatenameRegistered) {
            unregisterReceiver(myUpdatename);
            myUpdatenameRegistered = false;
        }*/

        super.onPause();
    }

    private boolean checkCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        1);
                return false;
            }
            return true;
        }
        return true;
    }


}
