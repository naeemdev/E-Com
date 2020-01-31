package com.appsolace.estore.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.appsolace.estore.Actvity.ActivityOrderDetails;
import com.appsolace.estore.Adapter.OrderHistoryItemAdapter;
import com.appsolace.estore.Model.OrderHistory.OrderHistoryItem;
import com.appsolace.estore.Model.ProductListModel.ColorItem;
import com.appsolace.estore.Model.ProductListModel.ProductAttributeItem;
import com.appsolace.estore.Model.ProductListModel.ProductImageItem;
import com.appsolace.estore.Model.ProductListModel.ProductItem;
import com.appsolace.estore.Model.ProductListModel.ProductSizeItem;
import com.appsolace.estore.R;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.callback.CallbackInternate;
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


public class OrderHistoryFragment extends Fragment {

    //Shared Preferences
    private SharedPreferences sPref;

    Dialog ProgressDialog;

    @BindView(R.id.ly_shopnow)
    RelativeLayout ly_shopnow;

    @BindView(R.id.txt_shopnow)
    CustomTextView txt_shopnow;

    @BindView(R.id.ll_main)
    LinearLayout ll_main;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.lyt_no_item)
    View lyt_no_item;

    @BindView(R.id.lyt_failed)
    View lyt_failed;

    private int failed_page = 0;
    OrderHistoryItemAdapter adapter;
    List<OrderHistoryItem> subcategorydataList;
    int curPage = 0;
    Call<JsonObject> callBooking;
    public String filterIds;
    public boolean isFilterApply = false;
    int height, width;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.orderhistory_fragment, container, false);
        ButterKnife.bind(this, view);

        sPref = PreferenceManager.getDefaultSharedPreferences(Common.Activity);
        initComponent();
        requestAction(0);
        return view;
    }

    private void initComponent() {

        ProgressDialog = new Dialog(Common.Activity, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Common.Activity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);


        subcategorydataList = new ArrayList<>();
        adapter = new OrderHistoryItemAdapter(Common.Activity, recyclerView, subcategorydataList);
        recyclerView.setAdapter(adapter);

        // detect when scroll reach bottom
        adapter.setOnLoadMoreListener(new OrderHistoryItemAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (!swipeRefreshLayout.isRefreshing())
                    requestAction(current_page);
            }
        });

        adapter.setOnItemClickListener(new OrderHistoryItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, OrderHistoryItem obj, int position) {

                Intent intent = new Intent(Common.Activity, ActivityOrderDetails.class);
                intent.putExtra("OrderHistoryItem", obj);
                startActivity(intent);

            }
        });

        adapter.setOnItemCancel(new OrderHistoryItemAdapter.OnItemCancle() {
            @Override
            public void oncancel(View view, OrderHistoryItem obj, int position) {

                requestCancelOrder(obj, obj.getOrderId());
            }
        });

        // on swipe list
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callBooking != null && callBooking.isExecuted()) callBooking.cancel();
                adapter.resetListData();
                requestAction(0);
            }
        });

        txt_shopnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MenuFragment fragment = new MenuFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.layout_item, fragment)
                        .commit();
            }
        });
    }

    private void requestAction(final int page_no) {
        if (page_no == 0) {
            if (!swipeRefreshLayout.isRefreshing()) {
                //Loader
                ProgressDialog.show();
            } else {
                swipeProgress(true);
            }
        } else {
            adapter.setLoading();
        }
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

        callBooking = api.confirm_order_history(sPref.getString("uid", ""), page_no + "");

        callBooking.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-subcategory" + resp);
                if (resp != null) {
                    if (resp.get("status").getAsString().equals("success")) {


                        JsonArray order_history;

                        if (resp.has("order_history") && resp.get("order_history").isJsonArray())

                            order_history = resp.get("order_history").getAsJsonArray();
                        else
                            order_history = null;


                        if (order_history != null && order_history.size() > 0) {

                            if (page_no == 0)
                                ly_shopnow.setVisibility(View.GONE);

                            List<OrderHistoryItem> list_orderhistory = new ArrayList<>();

                            for (int i = 0; i < order_history.size(); i++) {

                                JsonObject jsonObject_orderhistory = order_history.get(i).getAsJsonObject();

                                OrderHistoryItem orderHistoryItem = new OrderHistoryItem();

                                if (jsonObject_orderhistory.has("order_id") && !jsonObject_orderhistory.get("order_id").isJsonNull())
                                    orderHistoryItem.setOrderId(jsonObject_orderhistory.get("order_id").getAsString());
                                else
                                    orderHistoryItem.setOrderId("");

                                if (jsonObject_orderhistory.has("book_status") && !jsonObject_orderhistory.get("book_status").isJsonNull())
                                    orderHistoryItem.setBookStatus(jsonObject_orderhistory.get("book_status").getAsString());
                                else
                                    orderHistoryItem.setBookStatus("");

                                if (jsonObject_orderhistory.has("order_date") && !jsonObject_orderhistory.get("order_date").isJsonNull())
                                    orderHistoryItem.setOrderDate(jsonObject_orderhistory.get("order_date").getAsString());
                                else
                                    orderHistoryItem.setOrderDate("");

                                if (jsonObject_orderhistory.has("cancel_date") && !jsonObject_orderhistory.get("cancel_date").isJsonNull())
                                    orderHistoryItem.setCancelDate(jsonObject_orderhistory.get("cancel_date").getAsString());
                                else
                                    orderHistoryItem.setCancelDate("");

                                if (jsonObject_orderhistory.has("total_price") && !jsonObject_orderhistory.get("total_price").isJsonNull())
                                    orderHistoryItem.setTotalPrice(jsonObject_orderhistory.get("total_price").getAsString());
                                else
                                    orderHistoryItem.setTotalPrice("");

                                if (jsonObject_orderhistory.has("total_quantity") && !jsonObject_orderhistory.get("total_quantity").isJsonNull())
                                    orderHistoryItem.setTotalQuantity(jsonObject_orderhistory.get("total_quantity").getAsString());
                                else
                                    orderHistoryItem.setTotalQuantity("");

                                if (jsonObject_orderhistory.has("user_id") && !jsonObject_orderhistory.get("user_id").isJsonNull())
                                    orderHistoryItem.setUserId(jsonObject_orderhistory.get("user_id").getAsString());
                                else
                                    orderHistoryItem.setUserId("");

                                if (jsonObject_orderhistory.has("user_name") && !jsonObject_orderhistory.get("user_name").isJsonNull())
                                    orderHistoryItem.setUserName(jsonObject_orderhistory.get("user_name").getAsString());
                                else
                                    orderHistoryItem.setUserName("");

                                if (jsonObject_orderhistory.has("address_name") && !jsonObject_orderhistory.get("address_name").isJsonNull())
                                    orderHistoryItem.setAddressName(jsonObject_orderhistory.get("address_name").getAsString());
                                else
                                    orderHistoryItem.setAddressName("");

                                if (jsonObject_orderhistory.has("address1") && !jsonObject_orderhistory.get("address1").isJsonNull())
                                    orderHistoryItem.setAddress1(jsonObject_orderhistory.get("address1").getAsString());
                                else
                                    orderHistoryItem.setAddress1("");

                                if (jsonObject_orderhistory.has("address2") && !jsonObject_orderhistory.get("address2").isJsonNull())
                                    orderHistoryItem.setAddress2(jsonObject_orderhistory.get("address2").getAsString());
                                else
                                    orderHistoryItem.setAddress2("");

                                if (jsonObject_orderhistory.has("pincode") && !jsonObject_orderhistory.get("pincode").isJsonNull())
                                    orderHistoryItem.setPincode(jsonObject_orderhistory.get("pincode").getAsString());
                                else
                                    orderHistoryItem.setPincode("");

                                if (jsonObject_orderhistory.has("city") && !jsonObject_orderhistory.get("city").isJsonNull())
                                    orderHistoryItem.setCity(jsonObject_orderhistory.get("city").getAsString());
                                else
                                    orderHistoryItem.setCity("");

                                if (jsonObject_orderhistory.has("state") && !jsonObject_orderhistory.get("state").isJsonNull())
                                    orderHistoryItem.setState(jsonObject_orderhistory.get("state").getAsString());
                                else
                                    orderHistoryItem.setState("");

                                if (jsonObject_orderhistory.has("mobile") && !jsonObject_orderhistory.get("mobile").isJsonNull())
                                    orderHistoryItem.setMobile(jsonObject_orderhistory.get("mobile").getAsString());
                                else
                                    orderHistoryItem.setMobile("");

                                if (jsonObject_orderhistory.has("tax") && !jsonObject_orderhistory.get("tax").isJsonNull())
                                    orderHistoryItem.setTax(jsonObject_orderhistory.get("tax").getAsString());
                                else
                                    orderHistoryItem.setTax("");

                                if (jsonObject_orderhistory.has("tax_price") && !jsonObject_orderhistory.get("tax_price").isJsonNull())
                                    orderHistoryItem.setTaxPrice(jsonObject_orderhistory.get("tax_price").getAsString());
                                else
                                    orderHistoryItem.setTaxPrice("");

                                if (jsonObject_orderhistory.has("payment_type") && !jsonObject_orderhistory.get("payment_type").isJsonNull())
                                    orderHistoryItem.setPaymentType(jsonObject_orderhistory.get("payment_type").getAsString());
                                else
                                    orderHistoryItem.setPaymentType("");


                                JsonArray product_detail;

                                if (jsonObject_orderhistory.has("product_detail") && jsonObject_orderhistory.get("product_detail").isJsonArray())

                                    product_detail = jsonObject_orderhistory.get("product_detail").getAsJsonArray();
                                else
                                    product_detail = null;

                                if (product_detail != null && product_detail.size() > 0) {


                                    List<ProductItem> list_ProductItem = new ArrayList<>();

                                    for (int v = 0; v < product_detail.size(); v++) {

                                        JsonObject jsonObject_new_product = product_detail.get(v).getAsJsonObject();

                                        ProductItem newProductItem = new ProductItem();

                                        if (jsonObject_new_product.has("product_id") && !jsonObject_new_product.get("product_id").isJsonNull())
                                            newProductItem.setProductId(jsonObject_new_product.get("product_id").getAsString());
                                        else
                                            newProductItem.setProductId("");

                                        if (jsonObject_new_product.has("size_id") && !jsonObject_new_product.get("size_id").isJsonNull())
                                            newProductItem.setSizeId(jsonObject_new_product.get("size_id").getAsString());
                                        else
                                            newProductItem.setSizeId("");

                                        if (jsonObject_new_product.has("size_name") && !jsonObject_new_product.get("size_name").isJsonNull())
                                            newProductItem.setSizeName(jsonObject_new_product.get("size_name").getAsString());
                                        else
                                            newProductItem.setSizeName("");

                                        if (jsonObject_new_product.has("order_image") && !jsonObject_new_product.get("order_image").isJsonNull())
                                            newProductItem.setOrderImage(jsonObject_new_product.get("order_image").getAsString());
                                        else
                                            newProductItem.setOrderImage("");

                                        if (jsonObject_new_product.has("color_code") && !jsonObject_new_product.get("color_code").isJsonNull())
                                            newProductItem.setColorCode(jsonObject_new_product.get("color_code").getAsString());
                                        else
                                            newProductItem.setColorCode("");

                                        if (jsonObject_new_product.has("color_name") && !jsonObject_new_product.get("color_name").isJsonNull())
                                            newProductItem.setColorName(jsonObject_new_product.get("color_name").getAsString());
                                        else
                                            newProductItem.setColorName("");

                                        if (jsonObject_new_product.has("price") && !jsonObject_new_product.get("price").isJsonNull())
                                            newProductItem.setPrice(jsonObject_new_product.get("price").getAsString());
                                        else
                                            newProductItem.setPrice("");

                                        if (jsonObject_new_product.has("comment") && !jsonObject_new_product.get("comment").isJsonNull())
                                            newProductItem.setComment(jsonObject_new_product.get("comment").getAsString());
                                        else
                                            newProductItem.setComment("");

                                        if (jsonObject_new_product.has("quantity") && !jsonObject_new_product.get("quantity").isJsonNull())
                                            newProductItem.setQuantity(jsonObject_new_product.get("quantity").getAsString());
                                        else
                                            newProductItem.setQuantity("");

                                        if (jsonObject_new_product.has("book_status") && !jsonObject_new_product.get("book_status").isJsonNull())
                                            newProductItem.setBookStatus(jsonObject_new_product.get("book_status").getAsString());
                                        else
                                            newProductItem.setBookStatus("");

                                        if (jsonObject_new_product.has("amount") && !jsonObject_new_product.get("amount").isJsonNull())
                                            newProductItem.setAmount(jsonObject_new_product.get("amount").getAsString());
                                        else
                                            newProductItem.setAmount("");

                                        if (jsonObject_new_product.has("price_item") && !jsonObject_new_product.get("price_item").isJsonNull())
                                            newProductItem.setPriceItem(jsonObject_new_product.get("price_item").getAsString());
                                        else
                                            newProductItem.setPriceItem("");

                                        if (jsonObject_new_product.has("price_item") && !jsonObject_new_product.get("price_item").isJsonNull())
                                            newProductItem.setPriceItem(jsonObject_new_product.get("price_item").getAsString());
                                        else
                                            newProductItem.setPriceItem("");

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

                                        list_ProductItem.add(newProductItem);
                                    }

                                    orderHistoryItem.setProductDetail(list_ProductItem);
                                }

                                JsonArray placed_product_image;

                                if (jsonObject_orderhistory.has("placed_product_image") && jsonObject_orderhistory.get("placed_product_image").isJsonArray())

                                    placed_product_image = jsonObject_orderhistory.get("placed_product_image").getAsJsonArray();
                                else
                                    placed_product_image = null;

                                if (placed_product_image != null && placed_product_image.size() > 0) {

                                    List<String> list_Productimage = new ArrayList<>();
                                    for (int k = 0; k < placed_product_image.size(); k++) {
                                        String product_image = placed_product_image.get(k).getAsString();

                                        list_Productimage.add(product_image);
                                    }

                                    orderHistoryItem.setPlacedProductImage(list_Productimage);

                                }

                                list_orderhistory.add(orderHistoryItem);
                            }

                            if (list_orderhistory.size() > 0) {

                                displayApiResult(list_orderhistory);
                            } else {
                                onFailRequest(page_no);
                            }
                        } else {

                            if (page_no == 0)
                                ly_shopnow.setVisibility(View.VISIBLE);

                            onFailRequest(page_no);
                        }


                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        if (resp.has("total")) curPage = resp.get("total").getAsInt();

                    } else if (resp.get("status").getAsString().equals("false")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

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

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        if (resp.has("error code")) {

                            if (resp.get("error code").getAsString().equals("15")) {
                                onFailRequest(page_no);
                            } else
                                onFailRequest(page_no);
                            //Common.showMkError(Common.Activity, resp.get("error code").getAsString());
                        } else {
                            onFailRequest(page_no);
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
                    Common.ShowHttpErrorMessage(Common.Activity, message);
                }
            }
        });
    }

    private void upcomingEmpty() {
        if (adapter.getItemCount() == 0) {
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void displayApiResult(final List<OrderHistoryItem> items) {
        adapter.insertData(items);
        if (adapter.getItemCount() < 10) {
            adapter.setScroll(false);
        } else {
            adapter.setScroll(true);
        }
        swipeProgress(false);
        if (items.size() == 0) showNoItemView(true);
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipeRefreshLayout.setRefreshing(show);
            return;
        }
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(show);
            }
        });
    }

    private void showNoItemView(boolean show) {

        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void showFailedView(boolean show, String message) {

        ((TextView) lyt_failed.findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) lyt_failed.findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        adapter.setLoaded();
        swipeProgress(false);
        if (Common.isNetworkAvailable(Common.Activity)) {
            adapter.setScroll(false);
            Snackbar.make(ll_main, R.string.no_item, Snackbar.LENGTH_SHORT).show();
        } else {
            RetriverRequest(page_no);
        }
    }

    private void requestCancelOrder(final OrderHistoryItem obj, final String orderid) {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();

        Call<JsonObject> callcancel = api.cancel_confirm_order(sPref.getString("uid", ""), orderid);

        callcancel.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-subcategory" + resp);
                if (resp != null) {
                    if (resp.get("status").getAsString().equals("success")) {

                          /* {"user_id":"2271","order_id":"62","book_status":"2","cancel_date":"2018-06-05 06:52:14","message":"cancel order successfully","status":"success","Isactive":"1"}*/


                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        obj.setBookStatus("2");
                        adapter.notifyDataSetChanged();

                    } else if (resp.get("status").getAsString().equals("false")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

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
                    Common.ShowHttpErrorMessage(Common.Activity, message);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sPref.getBoolean("iscall",false))
        {
            SharedPreferences.Editor sh = sPref.edit();
            sh.putBoolean("iscall",false);
            sh.commit();

            subcategorydataList = new ArrayList<>();
            adapter = new OrderHistoryItemAdapter(Common.Activity, recyclerView, subcategorydataList);
            recyclerView.setAdapter(adapter);

            requestAction(0);

            // detect when scroll reach bottom
            adapter.setOnLoadMoreListener(new OrderHistoryItemAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore(int current_page) {
                    if (!swipeRefreshLayout.isRefreshing())
                        requestAction(current_page);
                }
            });

            adapter.setOnItemClickListener(new OrderHistoryItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, OrderHistoryItem obj, int position) {

                    Intent intent = new Intent(Common.Activity, ActivityOrderDetails.class);
                    intent.putExtra("OrderHistoryItem", obj);
                    startActivity(intent);

                }
            });

            adapter.setOnItemCancel(new OrderHistoryItemAdapter.OnItemCancle() {
                @Override
                public void oncancel(View view, OrderHistoryItem obj, int position) {

                    requestCancelOrder(obj, obj.getOrderId());
                }
            });
        }

    }
}
