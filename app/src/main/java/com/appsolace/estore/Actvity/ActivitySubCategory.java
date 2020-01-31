package com.appsolace.estore.Actvity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.appsolace.estore.Adapter.SubCategoryItemAdapter;
import com.appsolace.estore.Model.FilterProduct.FilterData;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.Model.Subcategorydata;
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

public class ActivitySubCategory extends AppCompatActivity {

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

    @BindView(R.id.txt_categoryname)
    CustomTextView txt_categoryname;

    @BindView(R.id.ly_back)
    RelativeLayout ly_back;

    private int failed_page = 0;
    SubCategoryItemAdapter adapter;
    List<Subcategorydata> subcategorydataList;
    int curPage = 0;
    Call<JsonObject> callBooking;
    public String filterIds;
    public boolean isFilterApply = false;
    int height, width;

    //Shared Preferences
    private SharedPreferences sPref;

    Dialog ProgressDialog;


    String Categoryid, Categoryname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        ButterKnife.bind(this);

        sPref = PreferenceManager.getDefaultSharedPreferences(ActivitySubCategory.this);
        Categoryid = sPref.getString("Categoryid", "");
        Categoryname = sPref.getString("Categoryname", "");

        initComponent();
        requestAction(0);
    }

    private void initComponent() {

        ProgressDialog = new Dialog(ActivitySubCategory.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);


        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        height = localDisplayMetrics.heightPixels;
        width = localDisplayMetrics.widthPixels;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ActivitySubCategory.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        subcategorydataList = new ArrayList<>();
        adapter = new SubCategoryItemAdapter(ActivitySubCategory.this, recyclerView, subcategorydataList, width, height);
        recyclerView.setAdapter(adapter);

        // detect when scroll reach bottom
        adapter.setOnLoadMoreListener(new SubCategoryItemAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (!swipeRefreshLayout.isRefreshing())
                    requestAction(current_page);
            }
        });

        adapter.setOnItemClickListener(new SubCategoryItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Subcategorydata obj, int position) {

                if (obj.getFlag() == 1) {
                    Intent intent = new Intent(ActivitySubCategory.this, ActivitySubCategory.class);
                    SharedPreferences.Editor hsid = sPref.edit();
                    hsid.putString("Categoryid", obj.getCategory_id());
                    hsid.putString("Categoryname", obj.getCategory_name());
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
                    Intent intent = new Intent(ActivitySubCategory.this, ActivityCategoryProduct.class);
                    SharedPreferences.Editor hsid = sPref.edit();
                    hsid.putString("Categoryid", obj.getCategory_id());
                    hsid.putString("Categoryname", obj.getCategory_name());
                    hsid.putInt("ProductFilter",2);
                    hsid.commit();
                    startActivity(intent);
                }

            }
        });

        txt_categoryname.setText("" + getResources().getString(R.string.all_in) + " " + Categoryname);
        // on swipe list
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callBooking != null && callBooking.isExecuted()) callBooking.cancel();
                adapter.resetListData();
                requestAction(0);
            }
        });

        ly_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                subcategorydataList = new ArrayList<>();
                subcategorydataList.clear();
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
        if (Common.isNetworkAvailable(ActivitySubCategory.this)) {
            requestListOrder(page_no);
        } else {
            RetriverRequest(page_no);
        }

    }

    private void RetriverRequest(final int page_no) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(ActivitySubCategory.this).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                if (Common.isNetworkAvailable(ActivitySubCategory.this)) {
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

        callBooking = api.category_list(sPref.getString("uid", ""), Categoryid, page_no + "");

        callBooking.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-subcategory" + resp);
                if (resp != null) {
                    if (resp.get("status").getAsString().equals("success")) {

                        JsonArray menu1_category;

                        if (resp.has("category_data") && resp.get("category_data").isJsonArray())

                            menu1_category = resp.get("category_data").getAsJsonArray();
                        else
                            menu1_category = null;

                        if (menu1_category != null && menu1_category.size() > 0) {

                            List<Subcategorydata> subcategorydataList = new ArrayList<>();

                            for (int i = 0; i < menu1_category.size(); i++) {

                                JsonObject jsonObject_menu1_category = menu1_category.get(i).getAsJsonObject();

                                Subcategorydata subcategorydata = new Subcategorydata();

                                if (jsonObject_menu1_category.has("category_name") && !jsonObject_menu1_category.get("category_name").isJsonNull())
                                    subcategorydata.setCategory_name(jsonObject_menu1_category.get("category_name").getAsString());
                                else
                                    subcategorydata.setCategory_name("");

                                if (jsonObject_menu1_category.has("category_id") && !jsonObject_menu1_category.get("category_id").isJsonNull())
                                    subcategorydata.setCategory_id(jsonObject_menu1_category.get("category_id").getAsString());
                                else
                                    subcategorydata.setCategory_id("");

                                if (jsonObject_menu1_category.has("flag") && !jsonObject_menu1_category.get("flag").isJsonNull())
                                    subcategorydata.setFlag(jsonObject_menu1_category.get("flag").getAsInt());
                                else
                                    subcategorydata.setFlag(0);

                                if (jsonObject_menu1_category.has("category_image") && !jsonObject_menu1_category.get("category_image").isJsonNull())
                                    subcategorydata.setCategory_image(jsonObject_menu1_category.get("category_image").getAsString());
                                else
                                    subcategorydata.setCategory_image("");

                                if (jsonObject_menu1_category.has("category_color") && !jsonObject_menu1_category.get("category_color").isJsonNull())
                                    subcategorydata.setCategory_color(jsonObject_menu1_category.get("category_color").getAsString());
                                else
                                    subcategorydata.setCategory_color("");

                                subcategorydataList.add(subcategorydata);
                            }

                            if (subcategorydataList.size() > 0) {
                                displayApiResult(subcategorydataList);
                            } else {
                                onFailRequest(page_no);
                            }


                        } else {
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
                            Common.AccountLock(ActivitySubCategory.this);
                        }
                        else if (resp.has("message") && !resp.get("message").isJsonNull()) {
                            Common.Errordialog(ActivitySubCategory.this,resp.get("message").toString());
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
                    Common.ShowHttpErrorMessage(ActivitySubCategory.this, message);
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

    private void displayApiResult(final List<Subcategorydata> items) {
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
        if (Common.isNetworkAvailable(ActivitySubCategory.this)) {
            adapter.setScroll(false);
            Snackbar.make(ll_main_upcoming, R.string.no_item, Snackbar.LENGTH_SHORT).show();
        } else {
            RetriverRequest(page_no);
        }
    }


}
