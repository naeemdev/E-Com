package com.appsolace.estore.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.appsolace.estore.Actvity.ActivityCategoryProduct;
import com.appsolace.estore.Actvity.ActivitySubCategory;
import com.appsolace.estore.Adapter.HomeCategoryItemAdapter;
import com.appsolace.estore.Adapter.HomeViewPagerAdapter;
import com.appsolace.estore.Adapter.Home_NewproductAdapter;
import com.appsolace.estore.Adapter.RecentproductAdapter;
import com.appsolace.estore.Model.FilterProduct.FilterData;
import com.appsolace.estore.Model.HomeData.CategoryDataItem;
import com.appsolace.estore.Model.HomeData.HomeSliderImageItem;
import com.appsolace.estore.Model.HomeData.NewProductItem;
import com.appsolace.estore.Model.RecentlyProduct.RecentlyProductItem;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.R;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeFragment extends Fragment {


    @BindView(R.id.pager)
    ViewPager viewPager;

    @BindView(R.id.indicator)
    CirclePageIndicator indicator;

    @BindView(R.id.home_recycler)
    RecyclerView home_recycler;

    @BindView(R.id.new_product_recycler)
    RecyclerView new_product_recycler;

    @BindView(R.id.txt_recentproduct)
    CustomTextView txt_recentproduct;

    @BindView(R.id.recentproduct_recycler)
    RecyclerView recentproduct_recycler;

    @BindView(R.id.layout_cart)
    RelativeLayout layout_cart;

    @BindView(R.id.layout_cart_cout)
    RelativeLayout layout_cart_cout;

    @BindView(R.id.txt_cart_cout)
    CustomTextView txt_cart_cout;

    HomeCategoryItemAdapter adapter;
    Home_NewproductAdapter newproduct_adpter;
    RecentproductAdapter recentproduct_adpter;

    List<CategoryDataItem> categoryDataItemList;
    List<HomeSliderImageItem> homeSliderImageItemList;
    List<NewProductItem> newProductItemList;
    List<RecentlyProductItem> recentlyProductItemList;

    SharedPreferences userPref;

    private View v;
    int height, width;
    private static int currentPage = 0, NUM_PAGES = 0;

    //Broadcast Receiver
    BroadcastReceiver UpdateCart = null;
    Boolean UpdateCartRegistered = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);

        initdata();
        return view;
    }

    public void initdata() {

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

        RecyclerView.LayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        new_product_recycler.setLayoutManager(horizontalLayoutManagaer);
        new_product_recycler.setItemAnimator(new DefaultItemAnimator());
        new_product_recycler.setHasFixedSize(true);

        RecyclerView.LayoutManager horizontalLayoutManagaer1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recentproduct_recycler.setLayoutManager(horizontalLayoutManagaer1);
        recentproduct_recycler.setItemAnimator(new DefaultItemAnimator());
        recentproduct_recycler.setHasFixedSize(true);


        if (Common.homeData != null) {
            homeSliderImageItemList = Common.homeData.getHomeSliderImage();
            if (homeSliderImageItemList != null && homeSliderImageItemList.size() > 0) {

                //Adapter for Home_Slider
                HomeViewPagerAdapter mCustomPagerAdapter = new HomeViewPagerAdapter(Common.Activity, homeSliderImageItemList);
                viewPager.setOffscreenPageLimit(1);
                viewPager.setAdapter(mCustomPagerAdapter);
                indicator.setViewPager(viewPager);

                NUM_PAGES = homeSliderImageItemList.size();
                SetTimerOnSlider();
            }
        }

        if (Common.homeData != null) {
            categoryDataItemList = Common.homeData.getCategoryData();
            if (categoryDataItemList != null && categoryDataItemList.size() > 0) {
                adapter = new HomeCategoryItemAdapter(Common.Activity, home_recycler, categoryDataItemList, width, height);
                home_recycler.setAdapter(adapter);


                adapter.setOnItemClickListener(new HomeCategoryItemAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, CategoryDataItem obj, int position) {

                        if (obj.getFlag() == 1) {
                            Intent intent = new Intent(Common.Activity, ActivitySubCategory.class);
                            SharedPreferences.Editor hsid = userPref.edit();
                            hsid.putString("Categoryid", obj.getCategoryId());
                            hsid.putString("Categoryname", obj.getCategoryName());
                            hsid.commit();
                            startActivity(intent);
                        } else {
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

        if (Common.homeData != null) {
            newProductItemList = Common.homeData.getNewProduct();
            if (newProductItemList != null && newProductItemList.size() > 0) {
                //Adapter for New Products
                newproduct_adpter = new Home_NewproductAdapter(Common.Activity,newProductItemList, width, height);
                new_product_recycler.setAdapter(newproduct_adpter);

            }
        }


        if (Common.homeData != null) {
            recentlyProductItemList = Common.homeData.getRecentProduct();
            if (recentlyProductItemList != null && recentlyProductItemList.size() > 0) {

                txt_recentproduct.setVisibility(View.VISIBLE);
                recentproduct_recycler.setVisibility(View.VISIBLE);

                //Adapter for New Products
                recentproduct_adpter = new RecentproductAdapter(Common.Activity,recentlyProductItemList);
                recentproduct_recycler.setAdapter(recentproduct_adpter);

            }
            else
            {
                txt_recentproduct.setVisibility(View.GONE);
                recentproduct_recycler.setVisibility(View.GONE);
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

    @Override
    public void onResume() {

        if (!UpdateCartRegistered) {
            getActivity().registerReceiver(UpdateCart, new IntentFilter("com.megastore.shopingcart"));
            UpdateCartRegistered = true;
        }
        super.onResume();
    }

}
