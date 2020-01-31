package com.appsolace.estore.Actvity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.appsolace.estore.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.appsolace.estore.Model.Address;
import com.appsolace.estore.Model.ProductListModel.ProductItem;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.callback.CallbackMessage;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.connection.RestAdapter;

import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPayment extends AppCompatActivity {

    //Shared Preferences
    private SharedPreferences sPref;

    Dialog ProgressDialog;

    @BindView(R.id.ly_back)
    RelativeLayout ly_back;

    @BindView(R.id.lay_status_delivery)
    LinearLayout lay_status_delivery;

    @BindView(R.id.txt_place_order)
    CustomTextView txt_place_order;

    @BindView(R.id.image_status)
    ImageView image_status;

    Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        sPref = PreferenceManager.getDefaultSharedPreferences(ActivityPayment.this);

        initdata();
    }

    private void initdata() {
        Intent intent = getIntent();
        address = (Address) intent.getSerializableExtra("Address");

        ProgressDialog = new Dialog(ActivityPayment.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);

        lay_status_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_status.setVisibility(View.VISIBLE);
            }
        });

        txt_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.list_ProductItem != null && Common.list_ProductItem.size() > 0) {
                    GetJsonObject();
                }
            }
        });

        ly_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void GetJsonObject() {
        JsonObject finalobject = new JsonObject();

        JsonArray jsonArray = new JsonArray();

        for (int i = 0; i < Common.list_ProductItem.size(); i++) {

            ProductItem productItem = Common.list_ProductItem.get(i);

            JsonObject product = new JsonObject();

            product.addProperty("Font_type_1", "");
            product.addProperty("Font_type_2", "");
            product.addProperty("Font_type_3", "");
            product.addProperty("Font_type_4", "");
            product.addProperty("Font_type_5", "");
            product.addProperty("Font_type_6", "");
            product.addProperty("Font_type_7", "");
            product.addProperty("Font_type_8", "");
            product.addProperty("Text_1", "");
            product.addProperty("Text_2", "");
            product.addProperty("Text_3", "");
            product.addProperty("Text_4", "");
            product.addProperty("Text_5", "");
            product.addProperty("Text_6", "");
            product.addProperty("Text_7", "");
            product.addProperty("Text_8", "");
            product.addProperty("Text_comment", "");
            product.addProperty("Text_line_instruction", "");
            product.addProperty("Text_quantity", "");

            product.addProperty("back_Font_type_1", "");
            product.addProperty("back_Font_type_2", "");
            product.addProperty("back_Font_type_3", "");
            product.addProperty("back_Font_type_4", "");
            product.addProperty("back_Font_type_5", "");
            product.addProperty("back_Font_type_6", "");
            product.addProperty("back_Font_type_7", "");
            product.addProperty("back_Font_type_8", "");
            product.addProperty("back_Text_1", "");
            product.addProperty("back_Text_2", "");
            product.addProperty("back_Text_3", "");
            product.addProperty("back_Text_4", "");
            product.addProperty("back_Text_5", "");
            product.addProperty("back_Text_6", "");
            product.addProperty("back_Text_7", "");
            product.addProperty("back_Text_8", "");
            product.addProperty("back_Text_comment", "");
            product.addProperty("back_Text_line_instruction", "");
            product.addProperty("back_Text_quantity", "");
            product.addProperty("city", "");
            product.addProperty("comment", "");
            product.addProperty("image_2_image_path", "");
            product.addProperty("image_path", "");

            product.addProperty("color", productItem.getColorCode());
            product.addProperty("isFormSelected", productItem.getIsFormSelected());
            product.addProperty("price", productItem.getProductPrice());
            product.addProperty("product_id", productItem.getProductId());
            product.addProperty("quantity", productItem.getQuantity());
            product.addProperty("size_id", productItem.getSizeId());

            jsonArray.add(product);
        }

        finalobject.add("product_order_detail", jsonArray);

        finalobject.addProperty("user_id", "" + sPref.getString("uid", ""));
        finalobject.addProperty("user_name", sPref.getString("name", ""));
        finalobject.addProperty("user_email", sPref.getString("email", ""));
        finalobject.addProperty("address1", "" + address.getAddress());
        finalobject.addProperty("address2", "");
        finalobject.addProperty("address_name", "" + address.getName());
        finalobject.addProperty("city", "" + address.getCity());
        finalobject.addProperty("mobile", "" + address.getMobile_no());
        finalobject.addProperty("payment_type", "cash");
        finalobject.addProperty("pincode", "" + address.getPincode());
        finalobject.addProperty("state", "" + address.getState());
        finalobject.addProperty("tax", sPref.getString("TAX", "0"));
        finalobject.addProperty("tax_price", sPref.getString("tax_value", "0"));
        finalobject.addProperty("transaction_id", "");


        Log.e("Product Object", "" + finalobject.toString());

        requestsubmitProductOrder(finalobject);
    }

    private void requestsubmitProductOrder(JsonObject finalobject) {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();
        Call<JsonObject> callback_login = api.checkout_ProductOrder(finalobject);
        callback_login.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("Submit Order", "=>" + response.body());

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();

                    if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {

                        /*{"total_order":"11","status":"success","Isactive":"1"}*/

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                      /*  {"status":"success","order_id":62,"Isactive":"1"}*/

                        int order_id=0;
                        if (jsonObject.has("order_id") && !jsonObject.get("order_id").isJsonNull())
                            order_id = jsonObject.get("order_id").getAsInt();
                        else
                            order_id = 0;

                        SharedPreferences.Editor sh = sPref.edit();
                        sh.putInt("Cart", 0);
                        sh.putInt("order_id", order_id);
                        sh.putString("mobile", "" + address.getMobile_no());
                        sh.commit();

                        SuccessDailog(getString(R.string.place_order_successful));

                    } else {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive=0;
                        if (jsonObject.has("Isactive") && !jsonObject.get("Isactive").isJsonNull()) {
                            Isactive = jsonObject.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(ActivityPayment.this);
                        }
                        else if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                            Common.Errordialog(ActivityPayment.this,jsonObject.get("message").toString());
                        }

                    }

                } else {

                    if (ProgressDialog != null && ProgressDialog.isShowing())
                        ProgressDialog.dismiss();

                    ErrorMessage(getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                if (ProgressDialog != null && ProgressDialog.isShowing())
                    ProgressDialog.dismiss();

                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(ActivityPayment.this, message);
                }
            }
        });

    }

    private void ErrorMessage(String Message) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(ActivityPayment.this).buildDialogMessage(new CallbackMessage() {

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

    private void SuccessDailog(String Message) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(ActivityPayment.this).buildDialogMessage(new CallbackMessage() {

            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                Intent thank = new Intent(ActivityPayment.this, ActivityThank.class);
                startActivity(thank);
            }

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }

        }, Message);
        dialog.show();
    }
}
