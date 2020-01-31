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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.appsolace.estore.Model.ProductListModel.ProductSizeItem;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.Actvity.ActivityProductDetails;
import com.appsolace.estore.Adapter.WishListItemAdapter;
import com.appsolace.estore.Model.ProductListModel.ColorItem;
import com.appsolace.estore.Model.ProductListModel.ProductAttributeItem;
import com.appsolace.estore.Model.ProductListModel.ProductImageItem;
import com.appsolace.estore.Model.ProductListModel.ProductItem;
import com.appsolace.estore.R;
import com.appsolace.estore.Utili.GridSpacingItemDecoration;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.callback.CallbackInternate;
import com.appsolace.estore.connection.RestAdapter;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class WishListFragment extends Fragment {

    //Shared Preferences
    private SharedPreferences sPref;

    Dialog ProgressDialog;

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

    @BindView(R.id.lay_arcloader)
    LinearLayout lay_arcloader;

    @BindView(R.id.ly_shopnow)
    RelativeLayout ly_shopnow;

    @BindView(R.id.txt_shopnow)
    CustomTextView txt_shopnow;

    private int failed_page = 0;
    WishListItemAdapter adapter;
    List<ProductItem> wishlistproductlist;
    int curPage = 0;
    Call<JsonObject> callBooking;
    int height, width;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wishlist_fragment, container, false);
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


        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        Common.Activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        height = localDisplayMetrics.heightPixels;
        width = localDisplayMetrics.widthPixels;

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(Common.Activity, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(Common.Activity, 2, 10, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        wishlistproductlist = new ArrayList<>();
        adapter = new WishListItemAdapter(Common.Activity, recyclerView, wishlistproductlist, width, height);
        recyclerView.setAdapter(adapter);


        // detect when scroll reach bottom
        adapter.setOnLoadMoreListener(new WishListItemAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (!swipeRefreshLayout.isRefreshing())
                    requestAction(current_page);
            }
        });

        adapter.setOnItemClickListener(new WishListItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ProductItem obj, int position) {

                Intent intent = new Intent(Common.Activity, ActivityProductDetails.class);
                intent.putExtra("ProductItem", obj);
                startActivity(intent);
            }
        });

        adapter.setOnItemRemove(new WishListItemAdapter.OnItemRemove() {
            @Override
            public void onRemove(View view, ProductItem obj, int position,List<ProductItem> listUpcoming) {

                if (listUpcoming != null && listUpcoming.size() > 0)
                {
                    listUpcoming.remove(position);
                    adapter.notifyDataSetChanged();

                    requestRemoveFavrite(""+obj.getProductId());
                    if (listUpcoming != null && listUpcoming.size() == 0)
                    {
                        ly_shopnow.setVisibility(View.VISIBLE);
                    }



                } else {
                    ly_shopnow.setVisibility(View.VISIBLE);

                    SharedPreferences.Editor sh = sPref.edit();
                    sh.putInt("wishlist", listUpcoming.size());
                    sh.commit();

                    Intent iReceiver = new Intent("com.megastore.wishlist");
                    getActivity().sendBroadcast(iReceiver);
                }

            }
        });

        // on swipe list
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callBooking != null && callBooking.isExecuted()) callBooking.cancel();
                adapter.resetListData();
                wishlistproductlist = new ArrayList<>();
                lay_arcloader.setVisibility(View.GONE);
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
            lay_arcloader.setVisibility(View.VISIBLE);
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

        callBooking = api.user_favorite_list(sPref.getString("uid", ""), page_no + "");

        callBooking.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-subcategory" + resp);
                if (resp != null) {
                    if (resp.get("status").getAsString().equals("success")) {

                        JsonArray product_detail;

                        if (resp.has("product_detail") && resp.get("product_detail").isJsonArray())

                            product_detail = resp.get("product_detail").getAsJsonArray();
                        else
                            product_detail = null;

                        if (product_detail != null && product_detail.size() > 0) {

                            if (page_no == 0)
                                ly_shopnow.setVisibility(View.GONE);

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

                            if (page_no == 0)
                                ly_shopnow.setVisibility(View.VISIBLE);

                            onFailRequest(page_no);
                        }

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        if (resp.has("total")) curPage = resp.get("total").getAsInt();

                    } else if (resp.get("status").getAsString().equals("false")) {

                        if (page_no == 0)
                            ly_shopnow.setVisibility(View.VISIBLE);

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

                        if (page_no == 0)
                            ly_shopnow.setVisibility(View.VISIBLE);

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

                if (page_no == 0)
                    ly_shopnow.setVisibility(View.VISIBLE);

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
        lay_arcloader.setVisibility(View.GONE);
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

    private void requestRemoveFavrite(String ProductId) {

        API api = RestAdapter.createAPI();

        Call<JsonObject> callBooking = api.remove_user_favorite(sPref.getString("uid", ""), ProductId);

        callBooking.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-subcategory" + resp);
                if (resp != null) {
                    if (resp.get("status").getAsString().equals("success")) {

                        Toast.makeText(Common.Activity, Common.Activity.getResources().getString(R.string.remove_wishlist), Toast.LENGTH_SHORT).show();

                        if (resp.has("total_favorite") && !resp.get("total_favorite").isJsonNull()) {
                            int total_favorite = resp.get("total_favorite").getAsInt();

                            SharedPreferences.Editor sh = sPref.edit();
                            sh.putInt("wishlist", total_favorite);
                            sh.commit();

                            Intent iReceiver = new Intent("com.megastore.wishlist");
                            getActivity().sendBroadcast(iReceiver);

                        }


                    } else if (resp.get("status").getAsString().equals("false")) {


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


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(Common.Activity, message);
                }
            }
        });
    }
}
