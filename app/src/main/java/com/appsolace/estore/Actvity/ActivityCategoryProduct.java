package com.appsolace.estore.Actvity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsolace.estore.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.appsolace.estore.Adapter.CategoryProductItemAdapter;
import com.appsolace.estore.Model.FilterProduct.FilterData;
import com.appsolace.estore.Model.ProductListModel.ProductAttributeItem;
import com.appsolace.estore.Model.ProductListModel.ProductItem;
import com.appsolace.estore.Model.ProductListModel.ProductSizeItem;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.callback.CallbackInternate;
import com.appsolace.estore.callback.CallbackMessage;
import com.appsolace.estore.connection.API;


import com.appsolace.estore.Model.ProductListModel.ColorItem;
import com.appsolace.estore.Model.ProductListModel.ProductImageItem;
import com.appsolace.estore.Utili.GridSpacingItemDecoration;
import com.appsolace.estore.connection.RestAdapter;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCategoryProduct extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.ll_main)
    LinearLayout ll_main_upcoming;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.lyt_no_item)
    View lyt_no_item;

    @BindView(R.id.lyt_failed)
    View lyt_failed;

    @BindView(R.id.ly_back)
    RelativeLayout ly_back;

    @BindView(R.id.layout_cart)
    RelativeLayout layout_cart;

    @BindView(R.id.layout_cart_cout)
    RelativeLayout layout_cart_cout;

    @BindView(R.id.txt_cart_cout)
    CustomTextView txt_cart_cout;

    @BindView(R.id.txt_sort)
    CustomTextView txt_sort;

    @BindView(R.id.txt_filter)
    CustomTextView txt_filter;

    @BindView(R.id.lay_arcloader)
    LinearLayout lay_arcloader;

    private int failed_page = 0;
    CategoryProductItemAdapter adapter;
    List<ProductItem> categoryproductlist;
    int curPage = 0;
    Call<JsonObject> callBooking;
    public String filterIds;
    public boolean isFilterApply = false;
    int height, width;

    //Shared Preferences
    private SharedPreferences sPref;

    Dialog ProgressDialog;

    String Categoryid, Categoryname, f_tags, f_brand, f_size, f_color, f_startprice, f_endprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product);
        ButterKnife.bind(this);

        sPref = PreferenceManager.getDefaultSharedPreferences(ActivityCategoryProduct.this);
        Categoryid = sPref.getString("Categoryid", "");
        Categoryname = sPref.getString("Categoryname", "");

        initComponent();
        requestAction(0);
    }

    private void initComponent() {


        if (sPref.getInt("Cart", 0) == 0) {
            txt_cart_cout.setText("");
            layout_cart_cout.setVisibility(View.GONE);
        } else {
            txt_cart_cout.setText("" + sPref.getInt("Cart", 0));
            layout_cart_cout.setVisibility(View.VISIBLE);
        }


        ProgressDialog = new Dialog(ActivityCategoryProduct.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);
        //progressBar = (ProgressBar) ProgressDialog.findViewById(R.id.progress_circular);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        height = localDisplayMetrics.heightPixels;
        width = localDisplayMetrics.widthPixels;

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(this, 2, 10, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        SetAdpterData();

        // on swipe list
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callBooking != null && callBooking.isExecuted()) callBooking.cancel();
                adapter.resetListData();
                lay_arcloader.setVisibility(View.GONE);
                requestAction(0);

            }
        });


        ly_back.setOnClickListener(this);
        txt_sort.setOnClickListener(this);
        txt_filter.setOnClickListener(this);
        layout_cart.setOnClickListener(this);


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
            lay_arcloader.setVisibility(View.VISIBLE);
        }
        if (Common.isNetworkAvailable(ActivityCategoryProduct.this)) {
            requestListOrder(page_no);
        } else {
            RetriverRequest(page_no);
        }

    }

    private void RetriverRequest(final int page_no) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(ActivityCategoryProduct.this).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                if (Common.isNetworkAvailable(ActivityCategoryProduct.this)) {
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

        if (Common.filterData.isIsfilter())
            callBooking = api.product_filter(sPref.getString("uid", ""), Categoryid, Common.filterData.getTag(), Common.filterData.getSize(), Common.filterData.getBrand(), Common.filterData.getColor(), Common.filterData.getStart_price(), Common.filterData.getEnd_price(), Common.product_sort_flag, page_no + "");
        else if (!Common.product_sort_flag.equals(""))
            callBooking = api.short_product(sPref.getString("uid", ""), Categoryid, Common.product_sort_flag, page_no + "");
        else
            callBooking = api.product_list(sPref.getString("uid", ""), Categoryid, page_no + "");

        callBooking.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-subcategory" + resp);
                if (resp != null) {
                    if (resp.get("status").getAsString().equals("success")) {

                        JsonObject filter_menus;

                        if (resp.has("filter_menus") && resp.get("filter_menus").isJsonObject())
                            filter_menus = resp.get("filter_menus").getAsJsonObject();
                        else
                            filter_menus = null;


                        if (filter_menus != null) {

                            if (filter_menus.has("menu1") && !filter_menus.get("menu1").isJsonNull())
                                Common.filtermenu1 = filter_menus.get("menu1").getAsString();
                            else
                                Common.filtermenu1 = "";

                            if (filter_menus.has("menu2") && !filter_menus.get("menu2").isJsonNull())
                                Common.filtermenu2 = filter_menus.get("menu2").getAsString();
                            else
                                Common.filtermenu2 = "";

                            if (filter_menus.has("menu3") && !filter_menus.get("menu3").isJsonNull())
                                Common.filtermenu3 = filter_menus.get("menu3").getAsString();
                            else
                                Common.filtermenu3 = "";

                            if (filter_menus.has("menu4") && !filter_menus.get("menu4").isJsonNull())
                                Common.filtermenu4 = filter_menus.get("menu4").getAsString();
                            else
                                Common.filtermenu4 = "";

                            if (filter_menus.has("menu5") && !filter_menus.get("menu5").isJsonNull())
                                Common.filtermenu5 = filter_menus.get("menu5").getAsString();
                            else
                                Common.filtermenu5 = "";
                        }


                        JsonArray product_detail;

                        if (Common.filterData.isIsfilter()) {
                            if (resp.has("filter") && resp.get("filter").isJsonArray())
                                product_detail = resp.get("filter").getAsJsonArray();
                            else
                                product_detail = null;

                        } else if (!Common.product_sort_flag.equals("")) {
                            if (resp.has("filter") && resp.get("filter").isJsonArray())
                                product_detail = resp.get("filter").getAsJsonArray();
                            else
                                product_detail = null;

                        } else {

                            if (resp.has("product_detail") && resp.get("product_detail").isJsonArray())
                                product_detail = resp.get("product_detail").getAsJsonArray();
                            else
                                product_detail = null;
                        }


                        if (product_detail != null && product_detail.size() > 0) {

                            List<ProductItem> list_ProductItem = new ArrayList<>();

                            for (int i = 0; i < product_detail.size(); i++) {

                                JsonObject jsonObject_new_product = product_detail.get(i).getAsJsonObject();

                                ProductItem newProductItem = new ProductItem();

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

                                list_ProductItem.add(newProductItem);
                            }

                            if (list_ProductItem.size() > 0) {
                                displayApiResult(list_ProductItem);
                            } else {
                                onFailRequest(page_no);
                            }

                        } else {
                            onFailRequest(page_no);
                        }

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();


                    } else if (resp.get("status").getAsString().equals("false")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive=0;
                        if (resp.has("Isactive") && !resp.get("Isactive").isJsonNull()) {
                            Isactive = resp.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(ActivityCategoryProduct.this);
                        }
                        else if (resp.has("message") && !resp.get("message").isJsonNull()) {
                            Common.Errordialog(ActivityCategoryProduct.this,resp.get("message").toString());
                        }

                    } else if (resp.get("status").getAsString().equals("failed")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        if (resp.has("error code")) {

                            if (resp.get("error code").getAsString().equals("15")) {
                                onFailRequest(page_no);
                            } else
                                onFailRequest(page_no);
                            //Common.showMkError(ActivitySubCategory.this, resp.get("error code").getAsString());
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
                    Common.ShowHttpErrorMessage(ActivityCategoryProduct.this, message);
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

    private void displayApiResult(final List<ProductItem> items) {
        lay_arcloader.setVisibility(View.GONE);
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
        lay_arcloader.setVisibility(View.GONE);
        swipeProgress(false);
        if (Common.isNetworkAvailable(ActivityCategoryProduct.this)) {
            adapter.setScroll(false);
            Snackbar.make(ll_main_upcoming, R.string.no_item, Snackbar.LENGTH_SHORT).show();
        } else {
            RetriverRequest(page_no);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        ly_back.performClick();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.ly_back) {
            Common.filterData = new FilterData();
            categoryproductlist = new ArrayList<>();
            categoryproductlist.clear();
            finish();

        } else if (id == R.id.txt_sort) {

            Dialog dialog = new DialogUtils(ActivityCategoryProduct.this).buildDialogProductSort(new CallbackMessage() {
                @Override
                public void onSuccess(Dialog dialog) {
                    dialog.dismiss();
                    SetAdpterData();
                    requestAction(0);

                }

                @Override
                public void onCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        } else if (id == R.id.txt_filter) {

            Intent filter_intent = new Intent(ActivityCategoryProduct.this, ProductFilter.class);
            startActivity(filter_intent);
        } else if (id == R.id.layout_cart)
        {
            Intent main = new Intent(ActivityCategoryProduct.this,MainActivity.class);
            SharedPreferences.Editor sh = sPref.edit();
            sh.putBoolean("Iscart",true);
            sh.commit();
            startActivity(main);
        }
    }

    private void SetAdpterData() {
        categoryproductlist = new ArrayList<>();
        adapter = new CategoryProductItemAdapter(ActivityCategoryProduct.this, recyclerView, categoryproductlist, width, height);
        recyclerView.setAdapter(adapter);
        adapter.resetListData();
        lay_arcloader.setVisibility(View.GONE);

        // detect when scroll reach bottom
        adapter.setOnLoadMoreListener(new CategoryProductItemAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (!swipeRefreshLayout.isRefreshing())
                    requestAction(current_page);
            }
        });

        adapter.setOnItemClickListener(new CategoryProductItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ProductItem obj, int position) {


                Intent intent = new Intent(ActivityCategoryProduct.this, ActivityProductDetails.class);
                intent.putExtra("ProductItem", obj);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sPref.getInt("ProductFilter", 0) == 1) {

            SetAdpterData();
            requestAction(0);

        }


        if (sPref.getInt("Cart", 0) == 0) {
            txt_cart_cout.setText("");
            layout_cart_cout.setVisibility(View.GONE);
        } else {
            txt_cart_cout.setText("" + sPref.getInt("Cart", 0));
            layout_cart_cout.setVisibility(View.VISIBLE);
        }
    }


}
