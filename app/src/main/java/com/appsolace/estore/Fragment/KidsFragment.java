package com.appsolace.estore.Fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.appsolace.estore.Adapter.KidsCategoryItemAdapter;
import com.appsolace.estore.Model.FilterProduct.FilterData;
import com.appsolace.estore.Model.Menu3.Menu3CategoryItem;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.Actvity.ActivityCategoryProduct;
import com.appsolace.estore.Actvity.ActivitySubCategory;
import com.appsolace.estore.Adapter.KidsViewPagerAdapter;
import com.appsolace.estore.Model.Menu3.Menu3SliderItem;
import com.appsolace.estore.R;
import com.appsolace.estore.callback.CallbackInternate;
import com.appsolace.estore.connection.RestAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KidsFragment extends Fragment {

    @BindView(R.id.pager)
    ViewPager viewPager;

    @BindView(R.id.indicator)
    CirclePageIndicator indicator;

    @BindView(R.id.home_recycler)
    RecyclerView home_recycler;

    @BindView(R.id.nested_view)
    NestedScrollView nested_view;

    @BindView(R.id.layout_main)
    LinearLayout layout_main;

    @BindView(R.id.lay_arcloader)
    LinearLayout lay_arcloader;

    @BindView(R.id.layout_cart)
    RelativeLayout layout_cart;

    @BindView(R.id.layout_cart_cout)
    RelativeLayout layout_cart_cout;

    @BindView(R.id.txt_cart_cout)
    CustomTextView txt_cart_cout;

    KidsCategoryItemAdapter adapter;

    List<Menu3CategoryItem> menu3CategoryItemList;
    List<Menu3SliderItem> menu3SliderItemList;

    SharedPreferences userPref;

    String category_id;

    private View v;
    int height, width;
    private static int currentPage = 0, NUM_PAGES = 0;

    int visibleItemCount = 0, totalItemCount = 0, pastVisiblesItems = 0;
    boolean isLoadData = true;

    //Broadcast Receiver
    BroadcastReceiver UpdateCart = null;
    Boolean UpdateCartRegistered = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.kids_fregment, container, false);
        ButterKnife.bind(this, view);

        initdata();
        return view;
    }

    public void initdata() {

        if (Common.menu_list != null && Common.menu_list.size() > 0) {

            for (int i = 0; i < Common.menu_list.size(); i++) {
                HashMap<String, String> map = Common.menu_list.get(i);
                if (i == 3) {
                    category_id = map.get("id");
                    break;
                }
            }
        }

        userPref = PreferenceManager.getDefaultSharedPreferences(Common.Activity);

        UpdateCart = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(Common.TAG, "onReceive-user");
                try {

                    if (userPref.getInt("Cart", 0) == 0) {
                        txt_cart_cout.setText("");
                        layout_cart_cout.setVisibility(View.GONE);
                    } else {
                        layout_cart_cout.setVisibility(View.VISIBLE);
                        txt_cart_cout.setText("" + userPref.getInt("Cart", 0));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        if (userPref.getInt("Cart", 0) == 0) {
            txt_cart_cout.setText("");
            layout_cart_cout.setVisibility(View.GONE);
        } else {
            layout_cart_cout.setVisibility(View.VISIBLE);
            txt_cart_cout.setText("" + userPref.getInt("Cart", 0));
        }

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        Common.Activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        height = localDisplayMetrics.heightPixels;
        width = localDisplayMetrics.widthPixels;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Common.Activity.getApplicationContext());
        home_recycler.setLayoutManager(mLayoutManager);
        home_recycler.setHasFixedSize(true);
        home_recycler.setNestedScrollingEnabled(false);


        if (Common.menu3Data != null) {
            menu3SliderItemList = Common.menu3Data.getMenu3Slider();
            if (menu3SliderItemList != null && menu3SliderItemList.size() > 0) {

                //Adapter for Home_Slider
                KidsViewPagerAdapter mCustomPagerAdapter = new KidsViewPagerAdapter(Common.Activity, menu3SliderItemList);
                viewPager.setAdapter(mCustomPagerAdapter);
                indicator.setViewPager(viewPager);

                NUM_PAGES = menu3SliderItemList.size();
                SetTimerOnSlider();
            }
        }

        if (Common.menu3Data != null) {
            menu3CategoryItemList = Common.menu3Data.getMenu3Category();
            if (menu3CategoryItemList != null && menu3CategoryItemList.size() > 0) {
                adapter = new KidsCategoryItemAdapter(Common.Activity, home_recycler, menu3CategoryItemList, width, height);
                home_recycler.setAdapter(adapter);

                nested_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        final LinearLayoutManager layoutManager = (LinearLayoutManager) home_recycler.getLayoutManager();

                        if (v.getChildAt(v.getChildCount() - 1) != null) {
                            if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                                    scrollY > oldScrollY) {

                                visibleItemCount = layoutManager.getChildCount();
                                totalItemCount = layoutManager.getItemCount();
                                pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                                if (isLoadData) {
                                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                        isLoadData = false;
                                        requestAction(totalItemCount);
                                    }
                                }
                            }
                        }
                    }
                });

                adapter.setOnItemClickListener(new KidsCategoryItemAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, Menu3CategoryItem obj, int position) {

                        if(obj.getFlag() == 1)
                        {
                            Intent intent = new Intent(Common.Activity, ActivitySubCategory.class);
                            SharedPreferences.Editor hsid = userPref.edit();
                            hsid.putString("Categoryid", obj.getCategoryId());
                            hsid.putString("Categoryname", obj.getCategoryName());
                            hsid.commit();
                            startActivity(intent);
                        }
                        else
                        {
                            Common.filterData = new FilterData();
                            Common.product_sort_flag = "";
                            Common.filtermenu1 = "";
                            Common.filtermenu2 = "";
                            Common.filtermenu3 = "";
                            Common.filtermenu4 = "";
                            Common.filtermenu5 = "";
                            Intent intent = new Intent(Common.Activity, ActivityCategoryProduct.class);
                            SharedPreferences.Editor hsid = userPref.edit();
                            hsid.putString("Categoryid", obj.getCategoryId());
                            hsid.putString("Categoryname", obj.getCategoryName());
                            hsid.putInt("ProductFilter",2);
                            hsid.commit();
                            startActivity(intent);
                        }

                    }
                });

            }
        }

        layout_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShopingCartFragment shopingCartFragment = new ShopingCartFragment();
                FragmentManager cartfragmentManager = getActivity().getSupportFragmentManager();
                cartfragmentManager.beginTransaction()
                        .replace(R.id.layout_item, shopingCartFragment)
                        .commit();
            }
        });
    }

    private void SetTimerOnSlider() {
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                currentPage = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
    }

    private void requestAction(final int page_no) {

        lay_arcloader.setVisibility(View.VISIBLE);

        if (Common.isNetworkAvailable(Common.Activity)) {
            requestListOrder(page_no);
        } else {
            RetriverRequest(page_no);
        }
    }

    private void RetriverRequest(final int page_no) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(Common.Activity).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                if (Common.isNetworkAvailable(Common.Activity)) {
                    requestListOrder(page_no);
                } else {
                    RetriverRequest(page_no);
                }
            }
        });
        dialog.show();
    }

    private void requestListOrder(final int page_no) {

        API api = RestAdapter.createAPI();
        Call<JsonObject> callmenucategory = api.category_list(userPref.getString("uid", ""), category_id, page_no + "");

        callmenucategory.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-Getbooking" + resp);
                if (resp != null) {
                    if (resp.get("status").getAsString().equals("success")) {
                        isLoadData = true;
                        lay_arcloader.setVisibility(View.GONE);

                        JsonArray menu1_category;

                        if (resp.has("category_data") && resp.get("category_data").isJsonArray())

                            menu1_category = resp.get("category_data").getAsJsonArray();
                        else
                            menu1_category = null;

                        if (menu1_category != null && menu1_category.size() > 0) {

                            List<Menu3CategoryItem> list_Menu3CategoryItem = new ArrayList<>();

                            for (int i = 0; i < menu1_category.size(); i++) {

                                JsonObject jsonObject_menu1_category = menu1_category.get(i).getAsJsonObject();

                                Menu3CategoryItem menu1CategoryItem = new Menu3CategoryItem();

                                if (jsonObject_menu1_category.has("category_name") && !jsonObject_menu1_category.get("category_name").isJsonNull())
                                    menu1CategoryItem.setCategoryName(jsonObject_menu1_category.get("category_name").getAsString());
                                else
                                    menu1CategoryItem.setCategoryName("");

                                if (jsonObject_menu1_category.has("category_id") && !jsonObject_menu1_category.get("category_id").isJsonNull())
                                    menu1CategoryItem.setCategoryId(jsonObject_menu1_category.get("category_id").getAsString());
                                else
                                    menu1CategoryItem.setCategoryId("");

                                if (jsonObject_menu1_category.has("flag") && !jsonObject_menu1_category.get("flag").isJsonNull())
                                    menu1CategoryItem.setFlag(jsonObject_menu1_category.get("flag").getAsInt());
                                else
                                    menu1CategoryItem.setFlag(0);

                                if (jsonObject_menu1_category.has("category_image") && !jsonObject_menu1_category.get("category_image").isJsonNull())
                                    menu1CategoryItem.setCategoryImage(jsonObject_menu1_category.get("category_image").getAsString());
                                else
                                    menu1CategoryItem.setCategoryImage("");

                                if (jsonObject_menu1_category.has("category_color") && !jsonObject_menu1_category.get("category_color").isJsonNull())
                                    menu1CategoryItem.setCategoryColor(jsonObject_menu1_category.get("category_color").getAsString());
                                else
                                    menu1CategoryItem.setCategoryColor("");

                                list_Menu3CategoryItem.add(menu1CategoryItem);
                            }


                            if (list_Menu3CategoryItem.size() > 0) {
                                displayApiResult(list_Menu3CategoryItem);
                            } else {
                                onFailRequest(page_no, false);
                            }

                        } else {

                            isLoadData = false;
                            onFailRequest(page_no, false);
                        }

                    } else if (resp.get("status").getAsString().equals("false")) {

                        isLoadData = true;
                        lay_arcloader.setVisibility(View.GONE);

                        int Isactive=0;
                        if (resp.has("Isactive") && !resp.get("Isactive").isJsonNull()) {
                            Isactive = resp.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(Common.Activity);
                        }
                        else if (resp.has("message") && !resp.get("message").isJsonNull()) {
                            Common.Errordialog(Common.Activity,resp.get("message").toString());
                        }

                    } else if (resp.get("status").getAsString().equals("failed")) {

                        isLoadData = true;
                        lay_arcloader.setVisibility(View.GONE);

                        if (resp.has("error code")) {

                            if (resp.get("error code").getAsString().equals("15")) {
                                onFailRequest(page_no, true);
                            } else
                                onFailRequest(page_no, true);
                            //Common.showMkError(Common.Activity, resp.get("error code").getAsString());
                        } else {
                            onFailRequest(page_no, true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                isLoadData = true;
                lay_arcloader.setVisibility(View.GONE);

                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(Common.Activity, message);
                }
            }
        });
    }

    private void displayApiResult(final List<Menu3CategoryItem> items) {
        menu3CategoryItemList.addAll(items);
        adapter.notifyDataSetChanged();
    }

    private void onFailRequest(int page_no, boolean data) {

        if (Common.isNetworkAvailable(Common.Activity)) {
            if (data)
                Snackbar.make(layout_main, R.string.no_item, Snackbar.LENGTH_SHORT).show();
        } else {
            RetriverRequest(page_no);
        }
    }

    @Override
    public void onResume() {

        if (!UpdateCartRegistered) {
            getActivity().registerReceiver(UpdateCart, new IntentFilter("com.megastore.shopingcart"));
            UpdateCartRegistered = true;
        }
        super.onResume();
    }

}
