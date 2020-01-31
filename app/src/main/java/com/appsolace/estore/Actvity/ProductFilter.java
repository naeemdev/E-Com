package com.appsolace.estore.Actvity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.appsolace.estore.R;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.appsolace.estore.Adapter.BrandAdapter;
import com.appsolace.estore.Adapter.ColorAdapter;
import com.appsolace.estore.Adapter.SizeAdapter;
import com.appsolace.estore.Adapter.TagsAdapter;
import com.appsolace.estore.Model.FilterProduct.FilterBrand;
import com.appsolace.estore.Model.FilterProduct.FilterColor;
import com.appsolace.estore.Model.FilterProduct.FilterData;
import com.appsolace.estore.Model.FilterProduct.FilterSize;
import com.appsolace.estore.Model.FilterProduct.FilterTag;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.connection.RestAdapter;


import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFilter extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    @BindView(R.id.ly_back)
    RelativeLayout ly_back;

    @BindView(R.id.txt_clearall)
    CustomTextView txt_clearall;

    @BindView(R.id.txt_filter)
    CustomTextView txt_filter;

    @BindView(R.id.layout_tag)
    LinearLayout layout_tag;

    @BindView(R.id.tag_view)
    RelativeLayout tag_view;

    @BindView(R.id.txt_tag)
    CustomTextView txt_tag;

    @BindView(R.id.txt_tagcount)
    CustomTextView txt_tagcount;

    @BindView(R.id.layout_brand)
    LinearLayout layout_brand;

    @BindView(R.id.brand_view)
    RelativeLayout brand_view;

    @BindView(R.id.txt_brand)
    CustomTextView txt_brand;

    @BindView(R.id.txt_brandcount)
    CustomTextView txt_brandcount;

    @BindView(R.id.layout_price)
    LinearLayout layout_price;

    @BindView(R.id.price_view)
    RelativeLayout price_view;

    @BindView(R.id.txt_price)
    CustomTextView txt_price;

    @BindView(R.id.txt_pricecount)
    CustomTextView txt_pricecount;

    @BindView(R.id.layout_size)
    LinearLayout layout_size;

    @BindView(R.id.size_view)
    RelativeLayout size_view;

    @BindView(R.id.txt_size)
    CustomTextView txt_size;

    @BindView(R.id.txt_sizecount)
    CustomTextView txt_sizecount;

    @BindView(R.id.layout_color)
    LinearLayout layout_color;

    @BindView(R.id.color_view)
    RelativeLayout color_view;

    @BindView(R.id.txt_color)
    CustomTextView txt_color;

    @BindView(R.id.txt_colorcount)
    CustomTextView txt_colorcount;

    @BindView(R.id.search_filter)
    SearchView search_filter;

    @BindView(R.id.recycle_filter)
    RecyclerView recycle_filter;

    @BindView(R.id.layout_listview)
    LinearLayout layout_listview;

    @BindView(R.id.layout_priceview)
    LinearLayout layout_priceview;

    @BindView(R.id.price_rangeview)
    CrystalRangeSeekbar price_rangeview;

    @BindView(R.id.txt_minvalue)
    CustomTextView txt_minvalue;

    @BindView(R.id.txt_maxvalue)
    CustomTextView txt_maxvalue;

    @BindView(R.id.txt_selectminvalue)
    CustomTextView txt_selectminvalue;

    @BindView(R.id.txt_selectmaxvalue)
    CustomTextView txt_selectmaxvalue;

    //Shared Preferences
    private SharedPreferences sPref;

    Dialog ProgressDialog;
    String Categoryid, uid, ismenu;

    boolean iscall = true;


    TagsAdapter tag_adapter;
    BrandAdapter brand_adapter;
    SizeAdapter size_adapter;
    ColorAdapter color_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_filter);
        ButterKnife.bind(this);

        sPref = PreferenceManager.getDefaultSharedPreferences(ProductFilter.this);
        Categoryid = sPref.getString("Categoryid", "");
        uid = sPref.getString("uid", "");

        initdata();
    }

    private void initdata() {

        ProgressDialog = new Dialog(ProductFilter.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ProductFilter.this);
        recycle_filter.setLayoutManager(mLayoutManager);
        recycle_filter.setHasFixedSize(true);

        setupSearchView();

        if (!Common.filtermenu1.equals("")) {
            layout_tag.setVisibility(View.VISIBLE);
            txt_tag.setText(Common.filtermenu1);
            ismenu = "tags";

            if (Common.filterData.getFilterTagList() != null && Common.filterData.getFilterTagList().size() > 0) {
                if (iscall) {

                    layout_listview.setVisibility(View.VISIBLE);
                    layout_priceview.setVisibility(View.GONE);

                    iscall = false;
                    layout_tag.setBackgroundColor(getResources().getColor(R.color.white));
                    txt_tag.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txt_brand.setTextColor(getResources().getColor(R.color.white));
                    txt_price.setTextColor(getResources().getColor(R.color.white));
                    txt_size.setTextColor(getResources().getColor(R.color.white));
                    txt_color.setTextColor(getResources().getColor(R.color.white));
                    layout_brand.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_price.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_size.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_color.setBackgroundColor(getResources().getColor(R.color.transparent));

                    setTagsData(Common.filterData.getFilterTagList());
                }
            } else {

                layout_listview.setVisibility(View.VISIBLE);
                layout_priceview.setVisibility(View.GONE);

                layout_tag.setBackgroundColor(getResources().getColor(R.color.white));
                txt_tag.setTextColor(getResources().getColor(R.color.colorPrimary));
                txt_brand.setTextColor(getResources().getColor(R.color.white));
                txt_price.setTextColor(getResources().getColor(R.color.white));
                txt_size.setTextColor(getResources().getColor(R.color.white));
                txt_color.setTextColor(getResources().getColor(R.color.white));
                layout_brand.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_price.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_size.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_color.setBackgroundColor(getResources().getColor(R.color.transparent));

                iscall = false;
                requestFilterTag();
            }

        }

        if (!Common.filtermenu2.equals("")) {
            layout_brand.setVisibility(View.VISIBLE);
            txt_brand.setText(Common.filtermenu2);

            if (Common.filterData.getFilterBrandList() != null && Common.filterData.getFilterBrandList().size() > 0) {
                if (iscall) {
                    layout_listview.setVisibility(View.VISIBLE);
                    layout_priceview.setVisibility(View.GONE);

                    layout_brand.setBackgroundColor(getResources().getColor(R.color.white));
                    txt_brand.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txt_tag.setTextColor(getResources().getColor(R.color.white));
                    txt_price.setTextColor(getResources().getColor(R.color.white));
                    txt_size.setTextColor(getResources().getColor(R.color.white));
                    txt_color.setTextColor(getResources().getColor(R.color.white));
                    layout_tag.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_price.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_size.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_color.setBackgroundColor(getResources().getColor(R.color.transparent));

                    iscall = false;
                    setBrandData(Common.filterData.getFilterBrandList());
                }
            } else {
                if (iscall) {

                    layout_listview.setVisibility(View.VISIBLE);
                    layout_priceview.setVisibility(View.GONE);
                    layout_brand.setBackgroundColor(getResources().getColor(R.color.white));
                    txt_brand.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txt_tag.setTextColor(getResources().getColor(R.color.white));
                    txt_price.setTextColor(getResources().getColor(R.color.white));
                    txt_size.setTextColor(getResources().getColor(R.color.white));
                    txt_color.setTextColor(getResources().getColor(R.color.white));
                    layout_tag.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_price.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_size.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_color.setBackgroundColor(getResources().getColor(R.color.transparent));

                    ismenu = "brand";
                    iscall = false;
                    requestFilterBrand();
                }
            }

        }

        if (!Common.filtermenu3.equals("")) {
            layout_price.setVisibility(View.VISIBLE);
            txt_price.setText(Common.filtermenu3);

            if (!Common.filterData.getMinimum_price().equals("") && !Common.filterData.getHigh_price().equals("")) {
                if (iscall) {

                    layout_listview.setVisibility(View.GONE);
                    layout_priceview.setVisibility(View.VISIBLE);

                    layout_price.setBackgroundColor(getResources().getColor(R.color.white));
                    txt_price.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txt_tag.setTextColor(getResources().getColor(R.color.white));
                    txt_brand.setTextColor(getResources().getColor(R.color.white));
                    txt_size.setTextColor(getResources().getColor(R.color.white));
                    txt_color.setTextColor(getResources().getColor(R.color.white));
                    layout_tag.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_brand.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_size.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_color.setBackgroundColor(getResources().getColor(R.color.transparent));

                    iscall = false;
                    setPriceData();
                }
            } else {
                if (iscall) {

                    layout_listview.setVisibility(View.GONE);
                    layout_priceview.setVisibility(View.VISIBLE);
                    layout_price.setBackgroundColor(getResources().getColor(R.color.white));
                    txt_price.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txt_tag.setTextColor(getResources().getColor(R.color.white));
                    txt_brand.setTextColor(getResources().getColor(R.color.white));
                    txt_size.setTextColor(getResources().getColor(R.color.white));
                    txt_color.setTextColor(getResources().getColor(R.color.white));
                    layout_tag.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_brand.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_size.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_color.setBackgroundColor(getResources().getColor(R.color.transparent));

                    ismenu = "price";
                    iscall = false;
                    requestFilterPrice();
                }
            }
        }

        if (!Common.filtermenu4.equals("")) {
            layout_size.setVisibility(View.VISIBLE);
            txt_size.setText(Common.filtermenu4);

            if (Common.filterData.getFilterSizeList() != null && Common.filterData.getFilterSizeList().size() > 0) {
                if (iscall) {

                    layout_listview.setVisibility(View.GONE);
                    layout_priceview.setVisibility(View.VISIBLE);

                    layout_size.setBackgroundColor(getResources().getColor(R.color.white));
                    txt_size.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txt_tag.setTextColor(getResources().getColor(R.color.white));
                    txt_price.setTextColor(getResources().getColor(R.color.white));
                    txt_brand.setTextColor(getResources().getColor(R.color.white));
                    txt_color.setTextColor(getResources().getColor(R.color.white));
                    layout_tag.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_brand.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_price.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_color.setBackgroundColor(getResources().getColor(R.color.transparent));

                    iscall = false;
                    setSizeData(Common.filterData.getFilterSizeList());
                }
            } else {
                if (iscall) {

                    layout_listview.setVisibility(View.GONE);
                    layout_priceview.setVisibility(View.VISIBLE);
                    layout_size.setBackgroundColor(getResources().getColor(R.color.white));
                    txt_size.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txt_tag.setTextColor(getResources().getColor(R.color.white));
                    txt_price.setTextColor(getResources().getColor(R.color.white));
                    txt_brand.setTextColor(getResources().getColor(R.color.white));
                    txt_color.setTextColor(getResources().getColor(R.color.white));
                    layout_tag.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_brand.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_price.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_color.setBackgroundColor(getResources().getColor(R.color.transparent));

                    ismenu = "size";
                    iscall = false;
                    requestFilterSize();
                }
            }
        }

        if (!Common.filtermenu5.equals("")) {
            layout_color.setVisibility(View.VISIBLE);
            txt_color.setText(Common.filtermenu5);

            if (Common.filterData.getFilterColorList() != null && Common.filterData.getFilterColorList().size() > 0) {

                if (iscall) {

                    layout_listview.setVisibility(View.VISIBLE);
                    layout_priceview.setVisibility(View.GONE);

                    layout_color.setBackgroundColor(getResources().getColor(R.color.white));
                    txt_color.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txt_tag.setTextColor(getResources().getColor(R.color.white));
                    txt_price.setTextColor(getResources().getColor(R.color.white));
                    txt_size.setTextColor(getResources().getColor(R.color.white));
                    txt_brand.setTextColor(getResources().getColor(R.color.white));
                    layout_tag.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_brand.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_price.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_size.setBackgroundColor(getResources().getColor(R.color.transparent));

                    iscall = false;
                    setColorData(Common.filterData.getFilterColorList());
                }
            } else {
                if (iscall) {

                    layout_listview.setVisibility(View.VISIBLE);
                    layout_priceview.setVisibility(View.GONE);

                    layout_color.setBackgroundColor(getResources().getColor(R.color.white));
                    txt_color.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txt_tag.setTextColor(getResources().getColor(R.color.white));
                    txt_price.setTextColor(getResources().getColor(R.color.white));
                    txt_size.setTextColor(getResources().getColor(R.color.white));
                    txt_brand.setTextColor(getResources().getColor(R.color.white));
                    layout_tag.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_brand.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_price.setBackgroundColor(getResources().getColor(R.color.transparent));
                    layout_size.setBackgroundColor(getResources().getColor(R.color.transparent));

                    ismenu = "color";
                    iscall = false;
                    requestFilterColor();
                }
            }
        }

        ly_back.setOnClickListener(this);
        txt_clearall.setOnClickListener(this);
        txt_filter.setOnClickListener(this);
        layout_tag.setOnClickListener(this);
        layout_brand.setOnClickListener(this);
        layout_price.setOnClickListener(this);
        layout_size.setOnClickListener(this);
        layout_color.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ly_back:
                SharedPreferences.Editor hsid = sPref.edit();
                hsid.putInt("ProductFilter", 2);
                hsid.commit();
                finish();
                break;
            case R.id.txt_clearall:

                Common.filterData = new FilterData();
                Common.filterData.setIsfilter(false);
                SharedPreferences.Editor hsid1 = sPref.edit();
                hsid1.putInt("ProductFilter", 1);
                hsid1.commit();
                finish();
                break;
            case R.id.txt_filter:

                setFilterData();
                Common.filterData.setIsfilter(true);
                SharedPreferences.Editor hsid2 = sPref.edit();
                hsid2.putInt("ProductFilter", 1);
                hsid2.commit();
                finish();
                break;
            case R.id.layout_tag:
                setupSearchView();

                ismenu = "tags";
                search_filter.clearFocus();
                layout_listview.setVisibility(View.VISIBLE);
                layout_priceview.setVisibility(View.GONE);
                layout_tag.setBackgroundColor(getResources().getColor(R.color.white));
                txt_tag.setTextColor(getResources().getColor(R.color.colorPrimary));
                txt_brand.setTextColor(getResources().getColor(R.color.white));
                txt_price.setTextColor(getResources().getColor(R.color.white));
                txt_size.setTextColor(getResources().getColor(R.color.white));
                txt_color.setTextColor(getResources().getColor(R.color.white));
                layout_brand.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_price.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_size.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_color.setBackgroundColor(getResources().getColor(R.color.transparent));

                if (Common.filterData.getFilterTagList() != null && Common.filterData.getFilterTagList().size() > 0) {

                    setTagsData(Common.filterData.getFilterTagList());
                } else {
                    requestFilterTag();
                }
                break;

            case R.id.layout_brand:
                setupSearchView();

                ismenu = "brand";
                search_filter.clearFocus();
                layout_listview.setVisibility(View.VISIBLE);
                layout_priceview.setVisibility(View.GONE);
                layout_brand.setBackgroundColor(getResources().getColor(R.color.white));
                txt_brand.setTextColor(getResources().getColor(R.color.colorPrimary));
                txt_tag.setTextColor(getResources().getColor(R.color.white));
                txt_price.setTextColor(getResources().getColor(R.color.white));
                txt_size.setTextColor(getResources().getColor(R.color.white));
                txt_color.setTextColor(getResources().getColor(R.color.white));
                layout_tag.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_price.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_size.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_color.setBackgroundColor(getResources().getColor(R.color.transparent));

                if (Common.filterData.getFilterBrandList() != null && Common.filterData.getFilterBrandList().size() > 0) {
                    setBrandData(Common.filterData.getFilterBrandList());
                } else {
                    requestFilterBrand();
                }

                break;

            case R.id.layout_price:
                setupSearchView();

                search_filter.clearFocus();
                layout_listview.setVisibility(View.GONE);
                layout_priceview.setVisibility(View.VISIBLE);
                layout_price.setBackgroundColor(getResources().getColor(R.color.white));
                txt_price.setTextColor(getResources().getColor(R.color.colorPrimary));
                txt_brand.setTextColor(getResources().getColor(R.color.white));
                txt_tag.setTextColor(getResources().getColor(R.color.white));
                txt_size.setTextColor(getResources().getColor(R.color.white));
                txt_color.setTextColor(getResources().getColor(R.color.white));
                layout_brand.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_tag.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_size.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_color.setBackgroundColor(getResources().getColor(R.color.transparent));

                if (!Common.filterData.getMinimum_price().equals("") && !Common.filterData.getHigh_price().equals("")) {
                    setPriceData();
                } else {
                    requestFilterPrice();
                }


                break;

            case R.id.layout_size:
                setupSearchView();

                ismenu = "size";
                search_filter.clearFocus();
                layout_listview.setVisibility(View.VISIBLE);
                layout_priceview.setVisibility(View.GONE);
                layout_size.setBackgroundColor(getResources().getColor(R.color.white));
                txt_size.setTextColor(getResources().getColor(R.color.colorPrimary));
                txt_brand.setTextColor(getResources().getColor(R.color.white));
                txt_price.setTextColor(getResources().getColor(R.color.white));
                txt_tag.setTextColor(getResources().getColor(R.color.white));
                txt_color.setTextColor(getResources().getColor(R.color.white));
                layout_brand.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_price.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_tag.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_color.setBackgroundColor(getResources().getColor(R.color.transparent));

                if (Common.filterData.getFilterSizeList() != null && Common.filterData.getFilterSizeList().size() > 0) {
                    setSizeData(Common.filterData.getFilterSizeList());
                } else {
                    requestFilterSize();
                }

                break;

            case R.id.layout_color:
                setupSearchView();

                ismenu = "color";
                search_filter.clearFocus();
                layout_listview.setVisibility(View.VISIBLE);
                layout_priceview.setVisibility(View.GONE);
                layout_color.setBackgroundColor(getResources().getColor(R.color.white));
                txt_color.setTextColor(getResources().getColor(R.color.colorPrimary));
                txt_brand.setTextColor(getResources().getColor(R.color.white));
                txt_price.setTextColor(getResources().getColor(R.color.white));
                txt_size.setTextColor(getResources().getColor(R.color.white));
                txt_tag.setTextColor(getResources().getColor(R.color.white));
                layout_brand.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_price.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_size.setBackgroundColor(getResources().getColor(R.color.transparent));
                layout_tag.setBackgroundColor(getResources().getColor(R.color.transparent));

                if (Common.filterData.getFilterColorList() != null && Common.filterData.getFilterColorList().size() > 0) {
                    setColorData(Common.filterData.getFilterColorList());
                } else {
                    requestFilterColor();
                }

                break;
        }
    }

    private void requestFilterTag() {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();

        Call<JsonObject> callBooking = api.filter_tag(uid, Categoryid);

        callBooking.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-category" + resp);
                if (resp != null) {
                    if (resp.get("status").getAsString().equals("success")) {

                        JsonArray tag_data;

                        if (resp.has("tag_data") && resp.get("tag_data").isJsonArray())

                            tag_data = resp.get("tag_data").getAsJsonArray();
                        else
                            tag_data = null;

                        if (tag_data != null && tag_data.size() > 0) {

                            List<FilterTag> filterTagList = new ArrayList<>();

                            for (int i = 0; i < tag_data.size(); i++) {

                                JsonObject jsonObject_tag_data = tag_data.get(i).getAsJsonObject();

                                FilterTag filterTag = new FilterTag();

                                if (jsonObject_tag_data.has("tag_id") && !jsonObject_tag_data.get("tag_id").isJsonNull())
                                    filterTag.setTag_id(jsonObject_tag_data.get("tag_id").getAsString());
                                else
                                    filterTag.setTag_id("");

                                if (jsonObject_tag_data.has("tag_name") && !jsonObject_tag_data.get("tag_name").isJsonNull())
                                    filterTag.setTag_name(jsonObject_tag_data.get("tag_name").getAsString());
                                else
                                    filterTag.setTag_name("");

                                filterTag.setTagselect(false);

                                filterTagList.add(filterTag);
                            }

                            Common.filterData.setFilterTagList(filterTagList);

                            setTagsData(filterTagList);
                        }

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();


                    } else if (resp.get("status").getAsString().equals("false")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive = 0;
                        if (resp.has("Isactive") && !resp.get("Isactive").isJsonNull()) {
                            Isactive = resp.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(ProductFilter.this);
                        } else if (resp.has("message") && !resp.get("message").isJsonNull()) {
                            Common.Errordialog(ProductFilter.this, resp.get("message").toString());
                        }
                    } else if (resp.get("status").getAsString().equals("failed")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        if (resp.has("error code")) {
                            //Common.showMkError(ActivitySubCategory.this, resp.get("error code").getAsString());
                        } else {
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (ProgressDialog != null && ProgressDialog.isShowing())
                    ProgressDialog.dismiss();
                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(ProductFilter.this, message);
                }
            }
        });
    }

    private void requestFilterBrand() {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();

        Call<JsonObject> callBooking = api.filter_brand(uid, Categoryid);

        callBooking.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-category" + resp);
                if (resp != null) {
                    if (resp.get("status").getAsString().equals("success")) {

                        JsonArray brand_data;

                        if (resp.has("brand_data") && resp.get("brand_data").isJsonArray())

                            brand_data = resp.get("brand_data").getAsJsonArray();
                        else
                            brand_data = null;

                        if (brand_data != null && brand_data.size() > 0) {

                            List<FilterBrand> filterBrandList = new ArrayList<>();

                            for (int i = 0; i < brand_data.size(); i++) {

                                JsonObject jsonObject_brand_data = brand_data.get(i).getAsJsonObject();

                                FilterBrand filterBrand = new FilterBrand();

                                if (jsonObject_brand_data.has("brand_id") && !jsonObject_brand_data.get("brand_id").isJsonNull())
                                    filterBrand.setBrand_id(jsonObject_brand_data.get("brand_id").getAsString());
                                else
                                    filterBrand.setBrand_id("");

                                if (jsonObject_brand_data.has("brand_name") && !jsonObject_brand_data.get("brand_name").isJsonNull())
                                    filterBrand.setBrand_name(jsonObject_brand_data.get("brand_name").getAsString());
                                else
                                    filterBrand.setBrand_name("");

                                filterBrand.setBrandselect(false);

                                filterBrandList.add(filterBrand);
                            }

                            Common.filterData.setFilterBrandList(filterBrandList);

                            setBrandData(filterBrandList);
                        }

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();


                    } else if (resp.get("status").getAsString().equals("false")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive = 0;
                        if (resp.has("Isactive") && !resp.get("Isactive").isJsonNull()) {
                            Isactive = resp.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(ProductFilter.this);
                        } else if (resp.has("message") && !resp.get("message").isJsonNull()) {
                            Common.Errordialog(ProductFilter.this, resp.get("message").toString());
                        }

                    } else if (resp.get("status").getAsString().equals("failed")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        if (resp.has("error code")) {
                            //Common.showMkError(ActivitySubCategory.this, resp.get("error code").getAsString());
                        } else {
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (ProgressDialog != null && ProgressDialog.isShowing())
                    ProgressDialog.dismiss();
                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(ProductFilter.this, message);
                }
            }
        });
    }

    private void requestFilterPrice() {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();

        Call<JsonObject> callBooking = api.filter_price(uid, Categoryid);

        callBooking.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-category" + resp);
                if (resp != null) {
                    if (resp.get("status").getAsString().equals("success")) {

                        JsonObject price_data;

                        if (resp.has("price_data") && resp.get("price_data").isJsonObject())

                            price_data = resp.get("price_data").getAsJsonObject();
                        else
                            price_data = null;

                        if (price_data != null) {

                            if (price_data.has("minimum_price") && !price_data.get("minimum_price").isJsonNull())
                                Common.filterData.setMinimum_price(price_data.get("minimum_price").getAsString());
                            else
                                Common.filterData.setMinimum_price("");

                            if (price_data.has("high_price") && !price_data.get("high_price").isJsonNull())
                                Common.filterData.setHigh_price(price_data.get("high_price").getAsString());
                            else
                                Common.filterData.setHigh_price("");


                            setPriceData();

                        }

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();


                    } else if (resp.get("status").getAsString().equals("false")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive = 0;
                        if (resp.has("Isactive") && !resp.get("Isactive").isJsonNull()) {
                            Isactive = resp.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(ProductFilter.this);
                        } else if (resp.has("message") && !resp.get("message").isJsonNull()) {
                            Common.Errordialog(ProductFilter.this, resp.get("message").toString());
                        }

                    } else if (resp.get("status").getAsString().equals("failed")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        if (resp.has("error code")) {
                            //Common.showMkError(ActivitySubCategory.this, resp.get("error code").getAsString());
                        } else {
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (ProgressDialog != null && ProgressDialog.isShowing())
                    ProgressDialog.dismiss();
                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(ProductFilter.this, message);
                }
            }
        });
    }

    private void requestFilterSize() {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();

        Call<JsonObject> callBooking = api.filter_size(uid, Categoryid);

        callBooking.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-category" + resp);
                if (resp != null) {
                    if (resp.get("status").getAsString().equals("success")) {

                        JsonArray size_data;

                        if (resp.has("size_data") && resp.get("size_data").isJsonArray())

                            size_data = resp.get("size_data").getAsJsonArray();
                        else
                            size_data = null;

                        if (size_data != null && size_data.size() > 0) {

                            List<FilterSize> filterSizeList = new ArrayList<>();

                            for (int i = 0; i < size_data.size(); i++) {

                                JsonObject jsonObject_size_data = size_data.get(i).getAsJsonObject();

                                FilterSize filterSize = new FilterSize();

                                if (jsonObject_size_data.has("size_id") && !jsonObject_size_data.get("size_id").isJsonNull())
                                    filterSize.setSize_id(jsonObject_size_data.get("size_id").getAsString());
                                else
                                    filterSize.setSize_id("");

                                if (jsonObject_size_data.has("size_name") && !jsonObject_size_data.get("size_name").isJsonNull())
                                    filterSize.setSize_name(jsonObject_size_data.get("size_name").getAsString());
                                else
                                    filterSize.setSize_name("");

                                filterSize.setSizeselect(false);

                                filterSizeList.add(filterSize);
                            }

                            Common.filterData.setFilterSizeList(filterSizeList);

                            setSizeData(filterSizeList);
                        }

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();


                    } else if (resp.get("status").getAsString().equals("false")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive = 0;
                        if (resp.has("Isactive") && !resp.get("Isactive").isJsonNull()) {
                            Isactive = resp.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(ProductFilter.this);
                        } else if (resp.has("message") && !resp.get("message").isJsonNull()) {
                            Common.Errordialog(ProductFilter.this, resp.get("message").toString());
                        }

                    } else if (resp.get("status").getAsString().equals("failed")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        if (resp.has("error code")) {
                            //Common.showMkError(ActivitySubCategory.this, resp.get("error code").getAsString());
                        } else {
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (ProgressDialog != null && ProgressDialog.isShowing())
                    ProgressDialog.dismiss();
                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(ProductFilter.this, message);
                }
            }
        });
    }

    private void requestFilterColor() {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();

        Call<JsonObject> callBooking = api.filter_color(uid);

        callBooking.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-category" + resp);
                if (resp != null) {
                    if (resp.get("status").getAsString().equals("success")) {

                        JsonArray color_data;

                        if (resp.has("color_data") && resp.get("color_data").isJsonArray())

                            color_data = resp.get("color_data").getAsJsonArray();
                        else
                            color_data = null;

                        if (color_data != null && color_data.size() > 0) {

                            List<FilterColor> filterColorList = new ArrayList<>();

                            for (int i = 0; i < color_data.size(); i++) {

                                JsonObject jsonObject_color_data = color_data.get(i).getAsJsonObject();

                                FilterColor filterColor = new FilterColor();

                                if (jsonObject_color_data.has("color_name") && !jsonObject_color_data.get("color_name").isJsonNull())
                                    filterColor.setColor_name(jsonObject_color_data.get("color_name").getAsString());
                                else
                                    filterColor.setColor_name("");

                                if (jsonObject_color_data.has("color_code") && !jsonObject_color_data.get("color_code").isJsonNull())
                                    filterColor.setColor_code(jsonObject_color_data.get("color_code").getAsString());
                                else
                                    filterColor.setColor_code("");

                                if (jsonObject_color_data.has("color_id") && !jsonObject_color_data.get("color_id").isJsonNull())
                                    filterColor.setColor_id(jsonObject_color_data.get("color_id").getAsString());
                                else
                                    filterColor.setColor_id("");

                                filterColor.setColorselect(false);

                                filterColorList.add(filterColor);
                            }

                            Common.filterData.setFilterColorList(filterColorList);

                            setColorData(filterColorList);

                        } else {

                        }

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();


                    } else if (resp.get("status").getAsString().equals("false")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive = 0;
                        if (resp.has("Isactive") && !resp.get("Isactive").isJsonNull()) {
                            Isactive = resp.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(ProductFilter.this);
                        } else if (resp.has("message") && !resp.get("message").isJsonNull()) {
                            Common.Errordialog(ProductFilter.this, resp.get("message").toString());
                        }

                    } else if (resp.get("status").getAsString().equals("failed")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        if (resp.has("error code")) {
                            //Common.showMkError(ActivitySubCategory.this, resp.get("error code").getAsString());
                        } else {
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (ProgressDialog != null && ProgressDialog.isShowing())
                    ProgressDialog.dismiss();
                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(ProductFilter.this, message);
                }
            }
        });
    }

    private void setTagsData(List<FilterTag> filterTagList) {

        tag_adapter = new TagsAdapter(ProductFilter.this, filterTagList);
        recycle_filter.setAdapter(tag_adapter);

    }

    private void setBrandData(List<FilterBrand> filterBrandList) {

        brand_adapter = new BrandAdapter(ProductFilter.this, filterBrandList);
        recycle_filter.setAdapter(brand_adapter);
    }

    private void setPriceData() {

        txt_minvalue.setText("" + Common.filterData.getMinimum_price());
        txt_maxvalue.setText("" + Common.filterData.getHigh_price());

        if (Common.filterData.isIsfilter()) {

            double minprice = 0, maxprice = 0;
            if (!Common.filterData.getMinimum_price().equals(""))
                minprice = Double.parseDouble("" + Common.filterData.getMinimum_price());

            if (!Common.filterData.getHigh_price().equals(""))
                maxprice = Double.parseDouble("" + Common.filterData.getHigh_price());

            float low = (float) minprice;
            float high = (float) maxprice;


            double str_pri = 0, end_pri = 0;
            if (Common.filterData.getStart_price() != null && !Common.filterData.getStart_price().equals(""))
                str_pri = Double.parseDouble("" + Common.filterData.getStart_price());

            if (Common.filterData.getEnd_price() != null && !Common.filterData.getEnd_price().equals(""))
                end_pri = Double.parseDouble("" + Common.filterData.getEnd_price());

            txt_selectminvalue.setText(String.valueOf(str_pri));
            txt_selectmaxvalue.setText(String.valueOf(end_pri));

            int min,max;
            if(str_pri==0 && end_pri==0) {
                 min = (int) minprice;
                 max = (int) maxprice;
            }
            else
            {
                 min = (int) str_pri;
                 max = (int) end_pri;
            }

            price_rangeview.setMinValue(low);
            price_rangeview.setMaxValue(high);
            price_rangeview.setMinStartValue(min);
            price_rangeview.setMaxStartValue(max);
            price_rangeview.apply();


        } else {

            double minprice = 0, maxprice = 0;
            if (!Common.filterData.getMinimum_price().equals(""))
                minprice = Double.parseDouble("" + Common.filterData.getMinimum_price());

            if (!Common.filterData.getHigh_price().equals(""))
                maxprice = Double.parseDouble("" + Common.filterData.getHigh_price());

            float min = (float) minprice;
            float max = (float) maxprice;

            price_rangeview.setMinValue(min);
            price_rangeview.setMaxValue(max);

            Common.filterData.setStart_price(String.valueOf(min));
            Common.filterData.setEnd_price(String.valueOf(max));

        }

        price_rangeview.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {

                txt_selectminvalue.setText(String.valueOf(minValue));
                txt_selectmaxvalue.setText(String.valueOf(maxValue));

            }
        });
    }

    private void setSizeData(List<FilterSize> filterSizeList) {
        size_adapter = new SizeAdapter(ProductFilter.this, filterSizeList);
        recycle_filter.setAdapter(size_adapter);
    }

    private void setColorData(List<FilterColor> filterColorList) {

        color_adapter = new ColorAdapter(ProductFilter.this, filterColorList);
        recycle_filter.setAdapter(color_adapter);
    }

    private void setupSearchView() {
        search_filter.setIconifiedByDefault(false);
        search_filter.setOnQueryTextListener(this);
        search_filter.setSubmitButtonEnabled(false);
        search_filter.setFocusable(false);
        search_filter.setFocusableInTouchMode(true);
        search_filter.setQueryHint("Search Here");

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(getApplicationContext(), "submit", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (ismenu.equals("tags")) {
            tag_adapter.filter(newText);
        } else if (ismenu.equals("brand")) {
            brand_adapter.filter(newText);
        } else if (ismenu.equals("size")) {
            size_adapter.filter(newText);
        } else if (ismenu.equals("color")) {
            color_adapter.filter(newText);
        }
        return true;

    }

    private void setFilterData() {

        /* ----------Taglist data------------------*/
        String tag_id = "";
        String tag_name = "";
        if (tag_adapter != null) {
            if (((TagsAdapter) tag_adapter).getlTaglist() != null) {
                List<FilterTag> tagList = ((TagsAdapter) tag_adapter).getlTaglist();

                for (int i = 0; i < tagList.size(); i++) {
                    FilterTag singletag = tagList.get(i);
                    if (singletag.isTagselect() == true) {
                        tag_name = tag_name + "," + singletag.getTag_name().toString();
                        tag_id = tag_id + "," + singletag.getTag_id().toString();
                    }
                }

                if (!tag_name.equals("")) {
                    tag_name = tag_name.substring(1);
                    tag_id = tag_id.substring(1);
                    Common.filterData.setTag(tag_id);
                }
            }
        }

         /* ----------Brand data------------------*/
        String brand_id = "";
        String brand_name = "";
        if (brand_adapter != null) {
            if (((BrandAdapter) brand_adapter).getbrandlist() != null) {

                List<FilterBrand> bndList = ((BrandAdapter) brand_adapter).getbrandlist();

                for (int i = 0; i < bndList.size(); i++) {
                    FilterBrand singlebnd = bndList.get(i);
                    if (singlebnd.isBrandselect() == true) {
                        brand_name = brand_name + "," + singlebnd.getBrand_name().toString();
                        brand_id = brand_id + "," + singlebnd.getBrand_id().toString();
                    }
                }

                if (!brand_name.equals("")) {
                    brand_name = brand_name.substring(1);
                    brand_id = brand_id.substring(1);
                    Common.filterData.setBrand(brand_id);
                }
            }
        }

         /* ----------Size data------------------*/
        String size_id = "";
        String size_name = "";
        if (size_adapter != null) {
            if (((SizeAdapter) size_adapter).getSizelist() != null) {

                List<FilterSize> sizeList = ((SizeAdapter) size_adapter).getSizelist();

                for (int i = 0; i < sizeList.size(); i++) {
                    FilterSize single_size = sizeList.get(i);
                    if (single_size.getSizeselect() == true) {
                        size_name = size_name + "," + single_size.getSize_name().toString();
                        size_id = size_id + "," + single_size.getSize_id().toString();
                    }
                }

                if (!size_name.equals("")) {
                    size_name = size_name.substring(1);
                    size_id = size_id.substring(1);
                    Common.filterData.setSize(size_id);
                }
            }
        }


        /* ----------Color data------------------*/
        String color_id = "";
        String color_name = "";
        String color_code = "";
        if (color_adapter != null) {
            if (((ColorAdapter) color_adapter).getColorlist() != null) {

                List<FilterColor> colorList = ((ColorAdapter) color_adapter).getColorlist();

                for (int i = 0; i < colorList.size(); i++) {
                    FilterColor single_color = colorList.get(i);
                    if (single_color.isColorselect() == true) {
                        color_name = color_name + "," + single_color.getColor_name().toString();
                        color_id = color_id + "," + single_color.getColor_id().toString();
                        color_code = color_code + "," + single_color.getColor_code().toString();
                    }
                }

                if (!color_name.equals("")) {
                    color_name = color_name.substring(1);
                    color_id = color_id.substring(1);
                    color_code = color_code.substring(1);
                    Common.filterData.setColor(color_code);
                }
            }
        }

        if (!Common.filterData.getMinimum_price().equals("") && !Common.filterData.getHigh_price().equals("")) {
            Common.filterData.setStart_price(txt_selectminvalue.getText().toString());
            Common.filterData.setEnd_price(txt_selectmaxvalue.getText().toString());
        }
    }

}
