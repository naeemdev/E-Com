package com.appsolace.estore.Fragment;

import android.app.Dialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.appsolace.estore.Model.HomeData.CategoryDataItem;
import com.appsolace.estore.Model.HomeData.ColorItem;
import com.appsolace.estore.Model.HomeData.HomeData;
import com.appsolace.estore.Model.HomeData.HomeSliderImageItem;
import com.appsolace.estore.Model.HomeData.NewProductItem;
import com.appsolace.estore.Model.HomeData.ProductAttributeItem;
import com.appsolace.estore.Model.HomeData.ProductImageItem;
import com.appsolace.estore.Model.HomeData.ProductSizeItem;
import com.appsolace.estore.Model.Menu1.Menu1CategoryItem;
import com.appsolace.estore.Model.Menu1.Menu1Data;
import com.appsolace.estore.Model.Menu1.Menu1SliderItem;
import com.appsolace.estore.Model.Menu2.Menu2CategoryItem;
import com.appsolace.estore.Model.Menu2.Menu2Data;
import com.appsolace.estore.Model.Menu2.Menu2SliderItem;
import com.appsolace.estore.Model.Menu3.Menu3CategoryItem;
import com.appsolace.estore.Model.Menu3.Menu3Data;
import com.appsolace.estore.Model.Menu3.Menu3SliderItem;
import com.appsolace.estore.Model.RecentlyProduct.RecentlyProductItem;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.callback.CallbackInternate;
import com.appsolace.estore.callback.CallbackMessage;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.connection.RestAdapter;
import com.appsolace.estore.R;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MenuFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.ly_loader)
    RelativeLayout ly_loader;

    public static RelativeLayout rlMainView;
    public static TextView tvTitle;

    SharedPreferences userPref;

    ArrayList<String> tabtitle;
    double cur_latitude = 0.0, cur_longitude = 0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.menu_fragment, container, false);

        ButterKnife.bind(this, view);

        userPref = PreferenceManager.getDefaultSharedPreferences(Common.Activity);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        rlMainView = (RelativeLayout) view.findViewById(R.id.rlMainView);

        if (Common.isNetworkAvailable(getActivity())) {
            requestHomeCategory();
        } else {
            TryAgainCall();
        }
        //initData();

        return view;
    }

    private void TryAgainCall() {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(getActivity()).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                if (Common.isNetworkAvailable(getActivity())) {
                    requestHomeCategory();
                } else {
                    TryAgainCall();
                }
            }
        });
        dialog.show();
    }

    private void requestHomeCategory() {

        ly_loader.setVisibility(View.VISIBLE);
        API api = RestAdapter.createAPI();
        Call<JsonObject> callback_homecategory = api.HomeCategory(userPref.getString("uid", ""));
        callback_homecategory.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("HomeCategory onResponse", "=>" + response.body());

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();


                    if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {


                        if (jsonObject.has("currency") && !jsonObject.get("currency").isJsonNull()) {
                            String currency = jsonObject.get("currency").getAsString();

                            SharedPreferences.Editor sh = userPref.edit();
                            sh.putString("CUR", currency);
                            sh.commit();
                        }


                        if (jsonObject.has("tax") && !jsonObject.get("tax").isJsonNull()) {
                            String tax = jsonObject.get("tax").getAsString();

                            SharedPreferences.Editor sh = userPref.edit();
                            sh.putString("TAX", tax);
                            sh.commit();
                        }

                        JsonArray jsonarray_menu;

                        if (jsonObject.has("menu") && jsonObject.get("menu").isJsonArray())

                            jsonarray_menu = jsonObject.get("menu").getAsJsonArray();
                        else
                            jsonarray_menu = null;

                        if (jsonarray_menu != null && jsonarray_menu.size() > 0) {
                            Common.menu_list.clear();

                            for (int i = 0; i < jsonarray_menu.size(); i++) {

                                JsonObject jsonObject_menu = jsonarray_menu.get(i).getAsJsonObject();

                                HashMap<String, String> hash1 = new HashMap<String, String>();

                                if (jsonObject_menu.has("name") && !jsonObject_menu.get("name").isJsonNull())
                                    hash1.put("name", jsonObject_menu.get("name").getAsString());
                                else
                                    hash1.put("name", "");

                                if (jsonObject_menu.has("id") && !jsonObject_menu.get("id").isJsonNull())
                                    hash1.put("id", jsonObject_menu.get("id").getAsString());
                                else
                                    hash1.put("id", "");

                                Common.menu_list.add(hash1);
                            }
                        }

                        JsonObject home_data;

                        if (jsonObject.has("home_data") && jsonObject.get("home_data").isJsonObject())

                            home_data = jsonObject.get("home_data").getAsJsonObject();
                        else
                            home_data = null;


                        if (home_data != null) {

                            Common.homeData = new HomeData();

                            JsonArray home_slider_image;

                            if (home_data.has("home_slider_image") && home_data.get("home_slider_image").isJsonArray())

                                home_slider_image = home_data.get("home_slider_image").getAsJsonArray();
                            else
                                home_slider_image = null;

                            if (home_slider_image != null && home_slider_image.size() > 0) {

                                List<HomeSliderImageItem> list_homeSliderImage = new ArrayList<>();

                                for (int i = 0; i < home_slider_image.size(); i++) {

                                    JsonObject jsonObject_home_slider_image = home_slider_image.get(i).getAsJsonObject();

                                    HomeSliderImageItem homeSliderImageItem = new HomeSliderImageItem();

                                    if (jsonObject_home_slider_image.has("image") && !jsonObject_home_slider_image.get("image").isJsonNull())
                                        homeSliderImageItem.setImage(jsonObject_home_slider_image.get("image").getAsString());
                                    else
                                        homeSliderImageItem.setImage("");

                                    if (jsonObject_home_slider_image.has("category_id") && !jsonObject_home_slider_image.get("category_id").isJsonNull())
                                        homeSliderImageItem.setCategoryId(jsonObject_home_slider_image.get("category_id").getAsString());
                                    else
                                        homeSliderImageItem.setCategoryId("");

                                    if (jsonObject_home_slider_image.has("flag") && !jsonObject_home_slider_image.get("flag").isJsonNull())
                                        homeSliderImageItem.setFlag(jsonObject_home_slider_image.get("flag").getAsInt());
                                    else
                                        homeSliderImageItem.setFlag(0);

                                    if (jsonObject_home_slider_image.has("category_name") && !jsonObject_home_slider_image.get("category_name").isJsonNull())
                                        homeSliderImageItem.setCategoryName(jsonObject_home_slider_image.get("category_name").getAsString());
                                    else
                                        homeSliderImageItem.setCategoryName("");

                                    list_homeSliderImage.add(homeSliderImageItem);
                                }

                                Common.homeData.setHomeSliderImage(list_homeSliderImage);
                            }

                            JsonArray category_data;

                            if (home_data.has("category_data") && home_data.get("category_data").isJsonArray())

                                category_data = home_data.get("category_data").getAsJsonArray();
                            else
                                category_data = null;

                            if (category_data != null && category_data.size() > 0) {

                                List<CategoryDataItem> list_CategoryDataItem = new ArrayList<>();

                                for (int i = 0; i < category_data.size(); i++) {

                                    JsonObject jsonObject_category_data = category_data.get(i).getAsJsonObject();

                                    CategoryDataItem categoryDataItem = new CategoryDataItem();

                                    if (jsonObject_category_data.has("category_name") && !jsonObject_category_data.get("category_name").isJsonNull())
                                        categoryDataItem.setCategoryName(jsonObject_category_data.get("category_name").getAsString());
                                    else
                                        categoryDataItem.setCategoryName("");

                                    if (jsonObject_category_data.has("category_id") && !jsonObject_category_data.get("category_id").isJsonNull())
                                        categoryDataItem.setCategoryId(jsonObject_category_data.get("category_id").getAsString());
                                    else
                                        categoryDataItem.setCategoryId("");

                                    if (jsonObject_category_data.has("flag") && !jsonObject_category_data.get("flag").isJsonNull())
                                        categoryDataItem.setFlag(jsonObject_category_data.get("flag").getAsInt());
                                    else
                                        categoryDataItem.setFlag(0);

                                    if (jsonObject_category_data.has("category_image") && !jsonObject_category_data.get("category_image").isJsonNull())
                                        categoryDataItem.setCategoryImage(jsonObject_category_data.get("category_image").getAsString());
                                    else
                                        categoryDataItem.setCategoryImage("");

                                    if (jsonObject_category_data.has("category_color") && !jsonObject_category_data.get("category_color").isJsonNull())
                                        categoryDataItem.setCategoryColor(jsonObject_category_data.get("category_color").getAsString());
                                    else
                                        categoryDataItem.setCategoryColor("");

                                    list_CategoryDataItem.add(categoryDataItem);
                                }

                                Common.homeData.setCategoryData(list_CategoryDataItem);
                            }

                            JsonArray recent_product;

                            if (home_data.has("recent_product") && home_data.get("recent_product").isJsonArray())

                                recent_product = home_data.get("recent_product").getAsJsonArray();
                            else
                                recent_product = null;

                            if (recent_product != null && recent_product.size() > 0) {

                                List<RecentlyProductItem> list_RecentlyProductItem = new ArrayList<>();

                                for (int i = 0; i < recent_product.size(); i++) {

                                    JsonObject jsonObject_new_product = recent_product.get(i).getAsJsonObject();

                                    RecentlyProductItem newProductItem = new RecentlyProductItem();

                                    if (jsonObject_new_product.has("product_id") && !jsonObject_new_product.get("product_id").isJsonNull())
                                        newProductItem.setProductId(jsonObject_new_product.get("product_id").getAsString());
                                    else
                                        newProductItem.setProductId("");

                                    if (jsonObject_new_product.has("product_name") && !jsonObject_new_product.get("product_name").isJsonNull())
                                        newProductItem.setProductName(jsonObject_new_product.get("product_name").getAsString());
                                    else
                                        newProductItem.setProductName("");

                                    if (jsonObject_new_product.has("product_status") && !jsonObject_new_product.get("product_status").isJsonNull())
                                        newProductItem.setProductStatus(jsonObject_new_product.get("product_status").getAsString());
                                    else
                                        newProductItem.setProductStatus("");

                                    if (jsonObject_new_product.has("product_primary_image") && !jsonObject_new_product.get("product_primary_image").isJsonNull())
                                        newProductItem.setProductPrimaryImage(jsonObject_new_product.get("product_primary_image").getAsString());
                                    else
                                        newProductItem.setProductPrimaryImage("");

                                    if (jsonObject_new_product.has("product_price") && !jsonObject_new_product.get("product_price").isJsonNull())
                                        newProductItem.setProductPrice(jsonObject_new_product.get("product_price").getAsString());
                                    else
                                        newProductItem.setProductPrice("");

                                    if (jsonObject_new_product.has("product_old_price") && !jsonObject_new_product.get("product_old_price").isJsonNull())
                                        newProductItem.setProductOldPrice(jsonObject_new_product.get("product_old_price").getAsString());
                                    else
                                        newProductItem.setProductOldPrice("");

                                    if (jsonObject_new_product.has("product_stock") && !jsonObject_new_product.get("product_stock").isJsonNull())
                                        newProductItem.setProductStock(jsonObject_new_product.get("product_stock").getAsString());
                                    else
                                        newProductItem.setProductStock("");

                                    if (jsonObject_new_product.has("product_description") && !jsonObject_new_product.get("product_description").isJsonNull())
                                        newProductItem.setProductDescription(jsonObject_new_product.get("product_description").getAsString());
                                    else
                                        newProductItem.setProductDescription("");

                                    if (jsonObject_new_product.has("isProductCustomizable") && !jsonObject_new_product.get("isProductCustomizable").isJsonNull())
                                        newProductItem.setIsProductCustomizable(jsonObject_new_product.get("isProductCustomizable").getAsString());
                                    else
                                        newProductItem.setIsProductCustomizable("");

                                    if (jsonObject_new_product.has("product_brand") && !jsonObject_new_product.get("product_brand").isJsonNull())
                                        newProductItem.setProductBrand(jsonObject_new_product.get("product_brand").getAsString());
                                    else
                                        newProductItem.setProductBrand("");

                                    if (jsonObject_new_product.has("product_color") && !jsonObject_new_product.get("product_color").isJsonNull())
                                        newProductItem.setProductColor(jsonObject_new_product.get("product_color").getAsString());
                                    else
                                        newProductItem.setProductColor("");

                                    if (jsonObject_new_product.has("favorite_flag") && !jsonObject_new_product.get("favorite_flag").isJsonNull())
                                        newProductItem.setFavoriteFlag(jsonObject_new_product.get("favorite_flag").getAsInt());
                                    else
                                        newProductItem.setFavoriteFlag(0);


                                    JsonArray product_image;

                                    if (jsonObject_new_product.has("product_image") && jsonObject_new_product.get("product_image").isJsonArray())

                                        product_image = jsonObject_new_product.get("product_image").getAsJsonArray();
                                    else
                                        product_image = null;

                                    if (product_image != null && product_image.size() > 0) {

                                        List<com.appsolace.estore.Model.RecentlyProduct.ProductImageItem> list_ProductImageItem = new ArrayList<>();

                                        for (int j = 0; j < product_image.size(); j++) {

                                            JsonObject jsonObject_product_image = product_image.get(j).getAsJsonObject();

                                            com.appsolace.estore.Model.RecentlyProduct.ProductImageItem productImageItem = new com.appsolace.estore.Model.RecentlyProduct.ProductImageItem();

                                            if (jsonObject_product_image.has("image") && !jsonObject_product_image.get("image").isJsonNull())
                                                productImageItem.setImage(jsonObject_product_image.get("image").getAsString());
                                            else
                                                productImageItem.setImage("");

                                            list_ProductImageItem.add(productImageItem);

                                        }

                                        newProductItem.setProductImage(list_ProductImageItem);
                                    }

                                    JsonArray product_size;

                                    if (jsonObject_new_product.has("product_size") && jsonObject_new_product.get("product_size").isJsonArray())

                                        product_size = jsonObject_new_product.get("product_size").getAsJsonArray();
                                    else
                                        product_size = null;

                                    if (product_size != null && product_size.size() > 0) {

                                        List<com.appsolace.estore.Model.RecentlyProduct.ProductSizeItem> list_ProductSizeItem = new ArrayList<>();

                                        for (int j = 0; j < product_size.size(); j++) {

                                            JsonObject jsonObject_product_size = product_size.get(j).getAsJsonObject();

                                            com.appsolace.estore.Model.RecentlyProduct.ProductSizeItem productSizeItem = new com.appsolace.estore.Model.RecentlyProduct.ProductSizeItem();

                                            if (jsonObject_product_size.has("size_id") && !jsonObject_product_size.get("size_id").isJsonNull())
                                                productSizeItem.setSizeId(jsonObject_product_size.get("size_id").getAsString());
                                            else
                                                productSizeItem.setSizeId("");

                                            if (jsonObject_product_size.has("size_name") && !jsonObject_product_size.get("size_name").isJsonNull())
                                                productSizeItem.setSizeName(jsonObject_product_size.get("size_name").getAsString());
                                            else
                                                productSizeItem.setSizeName("");

                                            if (jsonObject_product_size.has("size_image") && !jsonObject_product_size.get("size_image").isJsonNull())
                                                productSizeItem.setSizeImage(jsonObject_product_size.get("size_image").getAsString());
                                            else
                                                productSizeItem.setSizeImage("");

                                            if (jsonObject_product_size.has("size_stock") && !jsonObject_product_size.get("size_stock").isJsonNull())
                                                productSizeItem.setSizeStock(jsonObject_product_size.get("size_stock").getAsString());
                                            else
                                                productSizeItem.setSizeStock("");

                                            if (jsonObject_product_size.has("size_color") && !jsonObject_product_size.get("size_color").isJsonNull())
                                                productSizeItem.setSizeColor(jsonObject_product_size.get("size_color").getAsString());
                                            else
                                                productSizeItem.setSizeColor("");

                                            if (jsonObject_product_size.has("size_price") && !jsonObject_product_size.get("size_price").isJsonNull())
                                                productSizeItem.setSizePrice(jsonObject_product_size.get("size_price").getAsString());
                                            else
                                                productSizeItem.setSizePrice("");


                                            JsonArray color;

                                            if (jsonObject_product_size.has("color") && jsonObject_product_size.get("color").isJsonArray())

                                                color = jsonObject_product_size.get("color").getAsJsonArray();
                                            else
                                                color = null;

                                            if (color != null && color.size() > 0) {

                                                List<com.appsolace.estore.Model.RecentlyProduct.ColorItem> list_ColorItem = new ArrayList<>();

                                                for (int k = 0; k < color.size(); k++) {

                                                    JsonObject jsonObject_ColorItem = color.get(k).getAsJsonObject();

                                                    com.appsolace.estore.Model.RecentlyProduct.ColorItem colorItem = new com.appsolace.estore.Model.RecentlyProduct.ColorItem();

                                                    if (jsonObject_ColorItem.has("color_id") && !jsonObject_ColorItem.get("color_id").isJsonNull())
                                                        colorItem.setColorId(jsonObject_ColorItem.get("color_id").getAsString());
                                                    else
                                                        colorItem.setColorId("");

                                                    if (jsonObject_ColorItem.has("color_code") && !jsonObject_ColorItem.get("color_code").isJsonNull())
                                                        colorItem.setColorCode(jsonObject_ColorItem.get("color_code").getAsString());
                                                    else
                                                        colorItem.setColorCode("");

                                                    if (jsonObject_ColorItem.has("color_image") && !jsonObject_ColorItem.get("color_image").isJsonNull())
                                                        colorItem.setColorImage(jsonObject_ColorItem.get("color_image").getAsString());
                                                    else
                                                        colorItem.setColorImage("");

                                                    if (jsonObject_ColorItem.has("color_stock") && !jsonObject_ColorItem.get("color_stock").isJsonNull())
                                                        colorItem.setColorStock(jsonObject_ColorItem.get("color_stock").getAsString());
                                                    else
                                                        colorItem.setColorStock("");

                                                    if (jsonObject_ColorItem.has("color_price") && !jsonObject_ColorItem.get("color_price").isJsonNull())
                                                        colorItem.setColorPrice(jsonObject_ColorItem.get("color_price").getAsString());
                                                    else
                                                        colorItem.setColorPrice("");

                                                    list_ColorItem.add(colorItem);

                                                }

                                                productSizeItem.setColor(list_ColorItem);
                                            }

                                            list_ProductSizeItem.add(productSizeItem);

                                        }

                                        newProductItem.setProductSize(list_ProductSizeItem);
                                    }

                                    JsonArray product_attribute;

                                    if (jsonObject_new_product.has("product_attribute") && jsonObject_new_product.get("product_attribute").isJsonArray())

                                        product_attribute = jsonObject_new_product.get("product_attribute").getAsJsonArray();
                                    else
                                        product_attribute = null;

                                    if (product_attribute != null && product_attribute.size() > 0) {

                                        List<com.appsolace.estore.Model.RecentlyProduct.ProductAttributeItem> list_ProductAttributeItem = new ArrayList<>();

                                        for (int j = 0; j < product_attribute.size(); j++) {

                                            JsonObject jsonObject_product_attribute = product_attribute.get(j).getAsJsonObject();

                                            com.appsolace.estore.Model.RecentlyProduct.ProductAttributeItem productAttributeItem = new com.appsolace.estore.Model.RecentlyProduct.ProductAttributeItem();

                                            if (jsonObject_product_attribute.has("attribute_id") && !jsonObject_product_attribute.get("attribute_id").isJsonNull())
                                                productAttributeItem.setAttributeId(jsonObject_product_attribute.get("attribute_id").getAsString());
                                            else
                                                productAttributeItem.setAttributeId("");

                                            if (jsonObject_product_attribute.has("attribute_name") && !jsonObject_product_attribute.get("attribute_name").isJsonNull())
                                                productAttributeItem.setAttributeName(jsonObject_product_attribute.get("attribute_name").getAsString());
                                            else
                                                productAttributeItem.setAttributeName("");

                                            if (jsonObject_product_attribute.has("attribute_value") && !jsonObject_product_attribute.get("attribute_value").isJsonNull())
                                                productAttributeItem.setAttributeValue(jsonObject_product_attribute.get("attribute_value").getAsString());
                                            else
                                                productAttributeItem.setAttributeValue("");

                                            list_ProductAttributeItem.add(productAttributeItem);

                                        }

                                        newProductItem.setProductAttribute(list_ProductAttributeItem);
                                    }

                                    list_RecentlyProductItem.add(newProductItem);
                                }

                                Common.homeData.setRecentProduct(list_RecentlyProductItem);
                            }

                            JsonArray new_product;

                            if (home_data.has("new_product") && home_data.get("new_product").isJsonArray())

                                new_product = home_data.get("new_product").getAsJsonArray();
                            else
                                new_product = null;

                            if (new_product != null && new_product.size() > 0) {

                                List<NewProductItem> list_NewProductItem = new ArrayList<>();

                                for (int i = 0; i < new_product.size(); i++) {

                                    JsonObject jsonObject_new_product = new_product.get(i).getAsJsonObject();

                                    NewProductItem newProductItem = new NewProductItem();

                                    if (jsonObject_new_product.has("product_id") && !jsonObject_new_product.get("product_id").isJsonNull())
                                        newProductItem.setProductId(jsonObject_new_product.get("product_id").getAsString());
                                    else
                                        newProductItem.setProductId("");

                                    if (jsonObject_new_product.has("product_name") && !jsonObject_new_product.get("product_name").isJsonNull())
                                        newProductItem.setProductName(jsonObject_new_product.get("product_name").getAsString());
                                    else
                                        newProductItem.setProductName("");

                                    if (jsonObject_new_product.has("product_status") && !jsonObject_new_product.get("product_status").isJsonNull())
                                        newProductItem.setProductStatus(jsonObject_new_product.get("product_status").getAsString());
                                    else
                                        newProductItem.setProductStatus("");

                                    if (jsonObject_new_product.has("product_primary_image") && !jsonObject_new_product.get("product_primary_image").isJsonNull())
                                        newProductItem.setProductPrimaryImage(jsonObject_new_product.get("product_primary_image").getAsString());
                                    else
                                        newProductItem.setProductPrimaryImage("");

                                    if (jsonObject_new_product.has("product_price") && !jsonObject_new_product.get("product_price").isJsonNull())
                                        newProductItem.setProductPrice(jsonObject_new_product.get("product_price").getAsString());
                                    else
                                        newProductItem.setProductPrice("");

                                    if (jsonObject_new_product.has("product_old_price") && !jsonObject_new_product.get("product_old_price").isJsonNull())
                                        newProductItem.setProductOldPrice(jsonObject_new_product.get("product_old_price").getAsString());
                                    else
                                        newProductItem.setProductOldPrice("");

                                    if (jsonObject_new_product.has("product_stock") && !jsonObject_new_product.get("product_stock").isJsonNull())
                                        newProductItem.setProductStock(jsonObject_new_product.get("product_stock").getAsString());
                                    else
                                        newProductItem.setProductStock("");

                                    if (jsonObject_new_product.has("product_description") && !jsonObject_new_product.get("product_description").isJsonNull())
                                        newProductItem.setProductDescription(jsonObject_new_product.get("product_description").getAsString());
                                    else
                                        newProductItem.setProductDescription("");

                                    if (jsonObject_new_product.has("isProductCustomizable") && !jsonObject_new_product.get("isProductCustomizable").isJsonNull())
                                        newProductItem.setIsProductCustomizable(jsonObject_new_product.get("isProductCustomizable").getAsString());
                                    else
                                        newProductItem.setIsProductCustomizable("");

                                    if (jsonObject_new_product.has("product_brand") && !jsonObject_new_product.get("product_brand").isJsonNull())
                                        newProductItem.setProductBrand(jsonObject_new_product.get("product_brand").getAsString());
                                    else
                                        newProductItem.setProductBrand("");

                                    if (jsonObject_new_product.has("product_color") && !jsonObject_new_product.get("product_color").isJsonNull())
                                        newProductItem.setProductColor(jsonObject_new_product.get("product_color").getAsString());
                                    else
                                        newProductItem.setProductColor("");

                                    if (jsonObject_new_product.has("favorite_flag") && !jsonObject_new_product.get("favorite_flag").isJsonNull())
                                        newProductItem.setFavoriteFlag(jsonObject_new_product.get("favorite_flag").getAsInt());
                                    else
                                        newProductItem.setFavoriteFlag(0);


                                    JsonArray product_image;

                                    if (jsonObject_new_product.has("product_image") && jsonObject_new_product.get("product_image").isJsonArray())

                                        product_image = jsonObject_new_product.get("product_image").getAsJsonArray();
                                    else
                                        product_image = null;

                                    if (product_image != null && product_image.size() > 0) {

                                        List<ProductImageItem> list_ProductImageItem = new ArrayList<>();

                                        for (int j = 0; j < product_image.size(); j++) {

                                            JsonObject jsonObject_product_image = product_image.get(j).getAsJsonObject();

                                            ProductImageItem productImageItem = new ProductImageItem();

                                            if (jsonObject_product_image.has("image") && !jsonObject_product_image.get("image").isJsonNull())
                                                productImageItem.setImage(jsonObject_product_image.get("image").getAsString());
                                            else
                                                productImageItem.setImage("");

                                            list_ProductImageItem.add(productImageItem);

                                        }

                                        newProductItem.setProductImage(list_ProductImageItem);
                                    }

                                    JsonArray product_size;

                                    if (jsonObject_new_product.has("product_size") && jsonObject_new_product.get("product_size").isJsonArray())

                                        product_size = jsonObject_new_product.get("product_size").getAsJsonArray();
                                    else
                                        product_size = null;

                                    if (product_size != null && product_size.size() > 0) {

                                        List<ProductSizeItem> list_ProductSizeItem = new ArrayList<>();

                                        for (int j = 0; j < product_size.size(); j++) {

                                            JsonObject jsonObject_product_size = product_size.get(j).getAsJsonObject();

                                            ProductSizeItem productSizeItem = new ProductSizeItem();

                                            if (jsonObject_product_size.has("size_id") && !jsonObject_product_size.get("size_id").isJsonNull())
                                                productSizeItem.setSizeId(jsonObject_product_size.get("size_id").getAsString());
                                            else
                                                productSizeItem.setSizeId("");

                                            if (jsonObject_product_size.has("size_name") && !jsonObject_product_size.get("size_name").isJsonNull())
                                                productSizeItem.setSizeName(jsonObject_product_size.get("size_name").getAsString());
                                            else
                                                productSizeItem.setSizeName("");

                                            if (jsonObject_product_size.has("size_image") && !jsonObject_product_size.get("size_image").isJsonNull())
                                                productSizeItem.setSizeImage(jsonObject_product_size.get("size_image").getAsString());
                                            else
                                                productSizeItem.setSizeImage("");

                                            if (jsonObject_product_size.has("size_stock") && !jsonObject_product_size.get("size_stock").isJsonNull())
                                                productSizeItem.setSizeStock(jsonObject_product_size.get("size_stock").getAsString());
                                            else
                                                productSizeItem.setSizeStock("");

                                            if (jsonObject_product_size.has("size_color") && !jsonObject_product_size.get("size_color").isJsonNull())
                                                productSizeItem.setSizeColor(jsonObject_product_size.get("size_color").getAsString());
                                            else
                                                productSizeItem.setSizeColor("");

                                            if (jsonObject_product_size.has("size_price") && !jsonObject_product_size.get("size_price").isJsonNull())
                                                productSizeItem.setSizePrice(jsonObject_product_size.get("size_price").getAsString());
                                            else
                                                productSizeItem.setSizePrice("");


                                            JsonArray color;

                                            if (jsonObject_product_size.has("color") && jsonObject_product_size.get("color").isJsonArray())

                                                color = jsonObject_product_size.get("color").getAsJsonArray();
                                            else
                                                color = null;

                                            if (color != null && color.size() > 0) {

                                                List<ColorItem> list_ColorItem = new ArrayList<>();

                                                for (int k = 0; k < color.size(); k++) {

                                                    JsonObject jsonObject_ColorItem = color.get(k).getAsJsonObject();

                                                    ColorItem colorItem = new ColorItem();

                                                    if (jsonObject_ColorItem.has("color_id") && !jsonObject_ColorItem.get("color_id").isJsonNull())
                                                        colorItem.setColorId(jsonObject_ColorItem.get("color_id").getAsString());
                                                    else
                                                        colorItem.setColorId("");

                                                    if (jsonObject_ColorItem.has("color_code") && !jsonObject_ColorItem.get("color_code").isJsonNull())
                                                        colorItem.setColorCode(jsonObject_ColorItem.get("color_code").getAsString());
                                                    else
                                                        colorItem.setColorCode("");

                                                    if (jsonObject_ColorItem.has("color_image") && !jsonObject_ColorItem.get("color_image").isJsonNull())
                                                        colorItem.setColorImage(jsonObject_ColorItem.get("color_image").getAsString());
                                                    else
                                                        colorItem.setColorImage("");

                                                    if (jsonObject_ColorItem.has("color_stock") && !jsonObject_ColorItem.get("color_stock").isJsonNull())
                                                        colorItem.setColorStock(jsonObject_ColorItem.get("color_stock").getAsString());
                                                    else
                                                        colorItem.setColorStock("");

                                                    if (jsonObject_ColorItem.has("color_price") && !jsonObject_ColorItem.get("color_price").isJsonNull())
                                                        colorItem.setColorPrice(jsonObject_ColorItem.get("color_price").getAsString());
                                                    else
                                                        colorItem.setColorPrice("");

                                                    list_ColorItem.add(colorItem);

                                                }

                                                productSizeItem.setColor(list_ColorItem);
                                            }

                                            list_ProductSizeItem.add(productSizeItem);

                                        }

                                        newProductItem.setProductSize(list_ProductSizeItem);
                                    }

                                    JsonArray product_attribute;

                                    if (jsonObject_new_product.has("product_attribute") && jsonObject_new_product.get("product_attribute").isJsonArray())

                                        product_attribute = jsonObject_new_product.get("product_attribute").getAsJsonArray();
                                    else
                                        product_attribute = null;

                                    if (product_attribute != null && product_attribute.size() > 0) {

                                        List<ProductAttributeItem> list_ProductAttributeItem = new ArrayList<>();

                                        for (int j = 0; j < product_attribute.size(); j++) {

                                            JsonObject jsonObject_product_attribute = product_attribute.get(j).getAsJsonObject();

                                            ProductAttributeItem productAttributeItem = new ProductAttributeItem();

                                            if (jsonObject_product_attribute.has("attribute_id") && !jsonObject_product_attribute.get("attribute_id").isJsonNull())
                                                productAttributeItem.setAttributeId(jsonObject_product_attribute.get("attribute_id").getAsString());
                                            else
                                                productAttributeItem.setAttributeId("");

                                            if (jsonObject_product_attribute.has("attribute_name") && !jsonObject_product_attribute.get("attribute_name").isJsonNull())
                                                productAttributeItem.setAttributeName(jsonObject_product_attribute.get("attribute_name").getAsString());
                                            else
                                                productAttributeItem.setAttributeName("");

                                            if (jsonObject_product_attribute.has("attribute_value") && !jsonObject_product_attribute.get("attribute_value").isJsonNull())
                                                productAttributeItem.setAttributeValue(jsonObject_product_attribute.get("attribute_value").getAsString());
                                            else
                                                productAttributeItem.setAttributeValue("");

                                            list_ProductAttributeItem.add(productAttributeItem);

                                        }

                                        newProductItem.setProductAttribute(list_ProductAttributeItem);
                                    }

                                    list_NewProductItem.add(newProductItem);
                                }

                                Common.homeData.setNewProduct(list_NewProductItem);
                            }
                        }

                        JsonObject menu1_data;

                        if (jsonObject.has("menu1_data") && jsonObject.get("menu1_data").isJsonObject())

                            menu1_data = jsonObject.get("menu1_data").getAsJsonObject();
                        else
                            menu1_data = null;


                        if (menu1_data != null) {

                            Common.menu1Data = new Menu1Data();

                            JsonArray menu1_slider;

                            if (menu1_data.has("menu1_slider") && menu1_data.get("menu1_slider").isJsonArray())

                                menu1_slider = menu1_data.get("menu1_slider").getAsJsonArray();
                            else
                                menu1_slider = null;

                            if (menu1_slider != null && menu1_slider.size() > 0) {

                                List<Menu1SliderItem> list_Menu1SliderItem = new ArrayList<>();

                                for (int i = 0; i < menu1_slider.size(); i++) {

                                    JsonObject jsonObject_menu1_slider = menu1_slider.get(i).getAsJsonObject();

                                    Menu1SliderItem menu1SliderItem = new Menu1SliderItem();

                                    if (jsonObject_menu1_slider.has("image") && !jsonObject_menu1_slider.get("image").isJsonNull())
                                        menu1SliderItem.setImage(jsonObject_menu1_slider.get("image").getAsString());
                                    else
                                        menu1SliderItem.setImage("");

                                    if (jsonObject_menu1_slider.has("category_id") && !jsonObject_menu1_slider.get("category_id").isJsonNull())
                                        menu1SliderItem.setCategoryId(jsonObject_menu1_slider.get("category_id").getAsString());
                                    else
                                        menu1SliderItem.setCategoryId("");

                                    if (jsonObject_menu1_slider.has("flag") && !jsonObject_menu1_slider.get("flag").isJsonNull())
                                        menu1SliderItem.setFlag(jsonObject_menu1_slider.get("flag").getAsInt());
                                    else
                                        menu1SliderItem.setFlag(0);

                                    if (jsonObject_menu1_slider.has("category_name") && !jsonObject_menu1_slider.get("category_name").isJsonNull())
                                        menu1SliderItem.setCategoryName(jsonObject_menu1_slider.get("category_name").getAsString());
                                    else
                                        menu1SliderItem.setCategoryName("");

                                    list_Menu1SliderItem.add(menu1SliderItem);
                                }

                                Common.menu1Data.setMenu1Slider(list_Menu1SliderItem);
                            }

                            JsonArray menu1_category;

                            if (menu1_data.has("menu1_category") && menu1_data.get("menu1_category").isJsonArray())

                                menu1_category = menu1_data.get("menu1_category").getAsJsonArray();
                            else
                                menu1_category = null;

                            if (menu1_category != null && menu1_category.size() > 0) {

                                List<Menu1CategoryItem> list_Menu1CategoryItem = new ArrayList<>();

                                for (int i = 0; i < menu1_category.size(); i++) {

                                    JsonObject jsonObject_menu1_category = menu1_category.get(i).getAsJsonObject();

                                    Menu1CategoryItem menu1CategoryItem = new Menu1CategoryItem();

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

                                    list_Menu1CategoryItem.add(menu1CategoryItem);
                                }

                                Common.menu1Data.setMenu1Category(list_Menu1CategoryItem);
                            }

                        }

                        JsonObject menu2_data;

                        if (jsonObject.has("menu2_data") && jsonObject.get("menu2_data").isJsonObject())

                            menu2_data = jsonObject.get("menu2_data").getAsJsonObject();
                        else
                            menu2_data = null;


                        if (menu2_data != null) {

                            Common.menu2Data = new Menu2Data();

                            JsonArray menu2_slider;

                            if (menu2_data.has("menu2_slider") && menu2_data.get("menu2_slider").isJsonArray())

                                menu2_slider = menu2_data.get("menu2_slider").getAsJsonArray();
                            else
                                menu2_slider = null;

                            if (menu2_slider != null && menu2_slider.size() > 0) {

                                List<Menu2SliderItem> list_Menu2SliderItem = new ArrayList<>();

                                for (int i = 0; i < menu2_slider.size(); i++) {

                                    JsonObject jsonObject_menu2_slider = menu2_slider.get(i).getAsJsonObject();

                                    Menu2SliderItem menu2SliderItem = new Menu2SliderItem();

                                    if (jsonObject_menu2_slider.has("image") && !jsonObject_menu2_slider.get("image").isJsonNull())
                                        menu2SliderItem.setImage(jsonObject_menu2_slider.get("image").getAsString());
                                    else
                                        menu2SliderItem.setImage("");

                                    if (jsonObject_menu2_slider.has("category_id") && !jsonObject_menu2_slider.get("category_id").isJsonNull())
                                        menu2SliderItem.setCategoryId(jsonObject_menu2_slider.get("category_id").getAsString());
                                    else
                                        menu2SliderItem.setCategoryId("");

                                    if (jsonObject_menu2_slider.has("flag") && !jsonObject_menu2_slider.get("flag").isJsonNull())
                                        menu2SliderItem.setFlag(jsonObject_menu2_slider.get("flag").getAsInt());
                                    else
                                        menu2SliderItem.setFlag(0);

                                    if (jsonObject_menu2_slider.has("category_name") && !jsonObject_menu2_slider.get("category_name").isJsonNull())
                                        menu2SliderItem.setCategoryName(jsonObject_menu2_slider.get("category_name").getAsString());
                                    else
                                        menu2SliderItem.setCategoryName("");

                                    list_Menu2SliderItem.add(menu2SliderItem);
                                }

                                Common.menu2Data.setMenu2Slider(list_Menu2SliderItem);
                            }

                            JsonArray menu2_category;

                            if (menu2_data.has("menu2_category") && menu2_data.get("menu2_category").isJsonArray())

                                menu2_category = menu2_data.get("menu2_category").getAsJsonArray();
                            else
                                menu2_category = null;

                            if (menu2_category != null && menu2_category.size() > 0) {

                                List<Menu2CategoryItem> list_Menu2CategoryItem = new ArrayList<>();

                                for (int i = 0; i < menu2_category.size(); i++) {

                                    JsonObject jsonObjectmenu2_category = menu2_category.get(i).getAsJsonObject();

                                    Menu2CategoryItem menu2CategoryItem = new Menu2CategoryItem();

                                    if (jsonObjectmenu2_category.has("category_name") && !jsonObjectmenu2_category.get("category_name").isJsonNull())
                                        menu2CategoryItem.setCategoryName(jsonObjectmenu2_category.get("category_name").getAsString());
                                    else
                                        menu2CategoryItem.setCategoryName("");

                                    if (jsonObjectmenu2_category.has("category_id") && !jsonObjectmenu2_category.get("category_id").isJsonNull())
                                        menu2CategoryItem.setCategoryId(jsonObjectmenu2_category.get("category_id").getAsString());
                                    else
                                        menu2CategoryItem.setCategoryId("");

                                    if (jsonObjectmenu2_category.has("flag") && !jsonObjectmenu2_category.get("flag").isJsonNull())
                                        menu2CategoryItem.setFlag(jsonObjectmenu2_category.get("flag").getAsInt());
                                    else
                                        menu2CategoryItem.setFlag(0);

                                    if (jsonObjectmenu2_category.has("category_image") && !jsonObjectmenu2_category.get("category_image").isJsonNull())
                                        menu2CategoryItem.setCategoryImage(jsonObjectmenu2_category.get("category_image").getAsString());
                                    else
                                        menu2CategoryItem.setCategoryImage("");

                                    if (jsonObjectmenu2_category.has("category_color") && !jsonObjectmenu2_category.get("category_color").isJsonNull())
                                        menu2CategoryItem.setCategoryColor(jsonObjectmenu2_category.get("category_color").getAsString());
                                    else
                                        menu2CategoryItem.setCategoryColor("");

                                    list_Menu2CategoryItem.add(menu2CategoryItem);
                                }

                                Common.menu2Data.setMenu2Category(list_Menu2CategoryItem);
                            }

                        }

                        JsonObject menu3_data;

                        if (jsonObject.has("menu3_data") && jsonObject.get("menu3_data").isJsonObject())

                            menu3_data = jsonObject.get("menu3_data").getAsJsonObject();
                        else
                            menu3_data = null;


                        if (menu3_data != null) {

                            Common.menu3Data = new Menu3Data();

                            JsonArray menu3_slider;

                            if (menu3_data.has("menu3_slider") && menu3_data.get("menu3_slider").isJsonArray())

                                menu3_slider = menu3_data.get("menu3_slider").getAsJsonArray();
                            else
                                menu3_slider = null;

                            if (menu3_slider != null && menu3_slider.size() > 0) {

                                List<Menu3SliderItem> list_Menu3SliderItem = new ArrayList<>();

                                for (int i = 0; i < menu3_slider.size(); i++) {

                                    JsonObject jsonObject_menu3_slider = menu3_slider.get(i).getAsJsonObject();

                                    Menu3SliderItem menu3SliderItem = new Menu3SliderItem();

                                    if (jsonObject_menu3_slider.has("image") && !jsonObject_menu3_slider.get("image").isJsonNull())
                                        menu3SliderItem.setImage(jsonObject_menu3_slider.get("image").getAsString());
                                    else
                                        menu3SliderItem.setImage("");

                                    if (jsonObject_menu3_slider.has("category_id") && !jsonObject_menu3_slider.get("category_id").isJsonNull())
                                        menu3SliderItem.setCategoryId(jsonObject_menu3_slider.get("category_id").getAsString());
                                    else
                                        menu3SliderItem.setCategoryId("");

                                    if (jsonObject_menu3_slider.has("flag") && !jsonObject_menu3_slider.get("flag").isJsonNull())
                                        menu3SliderItem.setFlag(jsonObject_menu3_slider.get("flag").getAsInt());
                                    else
                                        menu3SliderItem.setFlag(0);

                                    if (jsonObject_menu3_slider.has("category_name") && !jsonObject_menu3_slider.get("category_name").isJsonNull())
                                        menu3SliderItem.setCategoryName(jsonObject_menu3_slider.get("category_name").getAsString());
                                    else
                                        menu3SliderItem.setCategoryName("");

                                    list_Menu3SliderItem.add(menu3SliderItem);
                                }

                                Common.menu3Data.setMenu3Slider(list_Menu3SliderItem);
                            }

                            JsonArray menu3_category;

                            if (menu3_data.has("menu3_category") && menu3_data.get("menu3_category").isJsonArray())

                                menu3_category = menu3_data.get("menu3_category").getAsJsonArray();
                            else
                                menu3_category = null;

                            if (menu3_category != null && menu3_category.size() > 0) {

                                List<Menu3CategoryItem> list_Menu3CategoryItem = new ArrayList<>();

                                for (int i = 0; i < menu3_category.size(); i++) {

                                    JsonObject jsonObjectmenu3_category = menu3_category.get(i).getAsJsonObject();

                                    Menu3CategoryItem menu3CategoryItem = new Menu3CategoryItem();

                                    if (jsonObjectmenu3_category.has("category_name") && !jsonObjectmenu3_category.get("category_name").isJsonNull())
                                        menu3CategoryItem.setCategoryName(jsonObjectmenu3_category.get("category_name").getAsString());
                                    else
                                        menu3CategoryItem.setCategoryName("");

                                    if (jsonObjectmenu3_category.has("category_id") && !jsonObjectmenu3_category.get("category_id").isJsonNull())
                                        menu3CategoryItem.setCategoryId(jsonObjectmenu3_category.get("category_id").getAsString());
                                    else
                                        menu3CategoryItem.setCategoryId("");

                                    if (jsonObjectmenu3_category.has("flag") && !jsonObjectmenu3_category.get("flag").isJsonNull())
                                        menu3CategoryItem.setFlag(jsonObjectmenu3_category.get("flag").getAsInt());
                                    else
                                        menu3CategoryItem.setFlag(0);

                                    if (jsonObjectmenu3_category.has("category_image") && !jsonObjectmenu3_category.get("category_image").isJsonNull())
                                        menu3CategoryItem.setCategoryImage(jsonObjectmenu3_category.get("category_image").getAsString());
                                    else
                                        menu3CategoryItem.setCategoryImage("");

                                    if (jsonObjectmenu3_category.has("category_color") && !jsonObjectmenu3_category.get("category_color").isJsonNull())
                                        menu3CategoryItem.setCategoryColor(jsonObjectmenu3_category.get("category_color").getAsString());
                                    else
                                        menu3CategoryItem.setCategoryColor("");

                                    list_Menu3CategoryItem.add(menu3CategoryItem);
                                }

                                Common.menu3Data.setMenu3Category(list_Menu3CategoryItem);
                            }
                        }


                        ly_loader.setVisibility(View.GONE);
                        initData();

                    } else {

                        ly_loader.setVisibility(View.GONE);

                        int Isactive=0;
                        if (jsonObject.has("Isactive") && !jsonObject.get("Isactive").isJsonNull()) {
                             Isactive = jsonObject.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(Common.Activity);
                        }
                        else if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                            ErrorMessage(jsonObject.get("message").toString());
                        }

                    }

                } else {

                    ly_loader.setVisibility(View.GONE);
                    ErrorMessage(getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {


                ly_loader.setVisibility(View.GONE);
                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(getActivity(), message);
                }
            }
        });

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

    private void initData() {

        tabtitle = new ArrayList<>();
        if (Common.menu_list != null && Common.menu_list.size() > 0) {

            for (int i = 0; i < Common.menu_list.size(); i++) {
                HashMap<String, String> map = Common.menu_list.get(i);
                if (i == 0) {
                    tabLayout.addTab(tabLayout.newTab().setText(map.get("name")));
                    tabtitle.add(map.get("name"));
                } else if (i == 1) {
                    tabLayout.addTab(tabLayout.newTab().setText(map.get("name")));
                    tabtitle.add(map.get("name"));
                } else if (i == 2) {
                    tabLayout.addTab(tabLayout.newTab().setText(map.get("name")));
                    tabtitle.add(map.get("name"));
                } else if (i == 3) {
                    tabLayout.addTab(tabLayout.newTab().setText(map.get("name")));
                    tabtitle.add(map.get("name"));
                }
            }
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //coding for add divider in tabLayout

        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.custom_gray));
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(20);
        linearLayout.setDividerDrawable(drawable);

        changeTabsFont();

        final Pager adapter = new Pager(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    private void changeTabsFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {

            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {

                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {

                    Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + getString(R.string.ubuntu_r));
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

        //private String[] tabTitles = new String[]{getString(R.string.upcoming), getString(R.string.assignjob), getString(R.string.history)};

        //Constructor to the class
        public Pager(FragmentManager fm, int tabCount) {
            super(fm);
            //Initializing tab count
            this.tabCount = tabCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // return tabTitles[position];
            return tabtitle.get(position);
        }

        //Overriding method getItem
        @Override
        public Fragment getItem(int position) {
            //Returning the current tabs
            switch (position) {
                case 0:
                    HomeFragment homeFragment = new HomeFragment();
                    return homeFragment;

                case 1:

                    MenFragment menFragment = new MenFragment();
                    return menFragment;

                case 2:

                    WomenFragment womenFragment = new WomenFragment();
                    return womenFragment;

                case 3:

                    KidsFragment kidsFragment = new KidsFragment();
                    return kidsFragment;
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

}
