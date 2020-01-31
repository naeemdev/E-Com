package com.appsolace.estore.Actvity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.appsolace.estore.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;
import com.appsolace.estore.Adapter.AddressAdapter;
import com.appsolace.estore.Model.Address;
import com.appsolace.estore.SwipeRow.ItemTouchHelperCallback;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.callback.CallbackInternate;
import com.appsolace.estore.callback.CallbackMessage;
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

public class ActivityAddressList extends AppCompatActivity implements View.OnClickListener {

    //Shared Preferences
    private SharedPreferences sPref;

    Dialog ProgressDialog;

    @BindView(R.id.address_recycler)
    RecyclerView address_recycler;

    @BindView(R.id.ly_address)
    RelativeLayout ly_address;

    @BindView(R.id.ly_back)
    RelativeLayout ly_back;


    List<Address> list_address;

    private AddressAdapter mAdapter;
    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ButterKnife.bind(this);

        sPref = PreferenceManager.getDefaultSharedPreferences(ActivityAddressList.this);

        initComponent();

        if (Common.isNetworkAvailable(ActivityAddressList.this)) {
            requestAddressList();
        } else {
            RetriveCall();
        }
    }

    private void initComponent() {

        ProgressDialog = new Dialog(ActivityAddressList.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);
        //progressBar = (ProgressBar) ProgressDialog.findViewById(R.id.progress_circular);

        ly_back.setOnClickListener(this);
        ly_address.setOnClickListener(this);
    }

    private void RetriveCall() {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(ActivityAddressList.this).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                if (Common.isNetworkAvailable(ActivityAddressList.this)) {
                    requestAddressList();
                } else {
                    RetriveCall();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ly_back:

                finish();
                break;

            case R.id.ly_address:

                SharedPreferences.Editor sh =sPref.edit();
                sh.putBoolean("isEdit",false);
                sh.commit();

                Intent i = new Intent(ActivityAddressList.this, ActivityAddAddress.class);
                i.putExtra("Address", "");
                startActivity(i);

                break;
        }
    }

    private void requestAddressList() {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();

        Call<JsonObject> callBooking = api.address_list(sPref.getString("uid", ""));

        callBooking.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject resp = response.body();
                Log.e(Common.TAG, "onResponse-add_list" + resp);
                if (resp != null) {
                   /* {"address_detail":[{"address_id":"40","name":"Rupen","address":"150ft ring road,imperial heights Rajkot","pincode":"360001","city":"Rajkot","state":"Gujarat","mobile_no":"2255889966"},{"address_id":"39","name":"KKK","address":"Rajkot","pincode":"360001","city":"rajkot","state":"Gujarat","mobile_no":"9988776655"}],"status":"success","Isactive":"1"}*/

                    if (resp.get("status").getAsString().equals("success")) {

                        JsonArray address_detail;

                        if (resp.has("address_detail") && resp.get("address_detail").isJsonArray())

                            address_detail = resp.get("address_detail").getAsJsonArray();
                        else
                            address_detail = null;

                        if (address_detail != null && address_detail.size() > 0) {

                            list_address = new ArrayList<>();

                            for (int i = 0; i < address_detail.size(); i++) {

                                JsonObject jsonObject_new_product = address_detail.get(i).getAsJsonObject();

                                Address newProductItem = new Address();

                                if (jsonObject_new_product.has("address_id") && !jsonObject_new_product.get("address_id").isJsonNull())
                                    newProductItem.setAddress_id(jsonObject_new_product.get("address_id").getAsString());
                                else
                                    newProductItem.setAddress_id("");

                                if (jsonObject_new_product.has("name") && !jsonObject_new_product.get("name").isJsonNull())
                                    newProductItem.setName(jsonObject_new_product.get("name").getAsString());
                                else
                                    newProductItem.setName("");

                                if (jsonObject_new_product.has("address") && !jsonObject_new_product.get("address").isJsonNull())
                                    newProductItem.setAddress(jsonObject_new_product.get("address").getAsString());
                                else
                                    newProductItem.setAddress("");

                                if (jsonObject_new_product.has("pincode") && !jsonObject_new_product.get("pincode").isJsonNull())
                                    newProductItem.setPincode(jsonObject_new_product.get("pincode").getAsString());
                                else
                                    newProductItem.setPincode("");

                                if (jsonObject_new_product.has("city") && !jsonObject_new_product.get("city").isJsonNull())
                                    newProductItem.setCity(jsonObject_new_product.get("city").getAsString());
                                else
                                    newProductItem.setCity("");

                                if (jsonObject_new_product.has("state") && !jsonObject_new_product.get("state").isJsonNull())
                                    newProductItem.setState(jsonObject_new_product.get("state").getAsString());
                                else
                                    newProductItem.setState("");

                                if (jsonObject_new_product.has("mobile_no") && !jsonObject_new_product.get("mobile_no").isJsonNull())
                                    newProductItem.setMobile_no(jsonObject_new_product.get("mobile_no").getAsString());
                                else
                                    newProductItem.setMobile_no("");

                                list_address.add(newProductItem);
                            }

                            if (list_address.size() > 0) {

                                displayApiResult(list_address);

                            } else {
                                //
                            }

                        } else {

                            //
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
                            Common.AccountLock(ActivityAddressList.this);
                        }
                        else if (resp.has("message") && !resp.get("message").isJsonNull()) {
                            Common.Errordialog(ActivityAddressList.this,resp.get("message").toString());
                        }

                    } else if (resp.get("status").getAsString().equals("failed")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();


                        ErrorMessage(getString(R.string.error));

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
                    Common.ShowHttpErrorMessage(ActivityAddressList.this, message);
                }
            }
        });
    }

    private void ErrorMessage(String Message) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(ActivityAddressList.this).buildDialogMessage(new CallbackMessage() {
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

    private void displayApiResult(List<Address> list_address) {


        address_recycler.setLayoutManager(new LinearLayoutManager(ActivityAddressList.this));
        mAdapter = new AddressAdapter(ActivityAddressList.this, list_address);
        address_recycler.setAdapter(mAdapter);
       // address_recycler.addItemDecoration(new DividerItemDecoration(ActivityAddressList.this));
        mCallback = new ItemTouchHelperCallback();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(address_recycler);


        mAdapter.setOnItemClickListener(new AddressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Address obj, int position) {

                Intent payment = new Intent(ActivityAddressList.this,ActivityPayment.class);
                payment.putExtra("Address",obj);
                startActivity(payment);

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(sPref.getBoolean("Add",false)) {
            list_address = new ArrayList<>();
            if (Common.isNetworkAvailable(ActivityAddressList.this)) {
                requestAddressList();
            } else {
                RetriveCall();
            }
        }
    }
}
