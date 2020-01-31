package com.appsolace.estore.Actvity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.appsolace.estore.R;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.appsolace.estore.Model.OrderHistory.OrderHistoryItem;
import com.appsolace.estore.Model.ProductListModel.ProductItem;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.Utili.Url;
import com.appsolace.estore.Utili.Utils;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.connection.RestAdapter;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityOrderDetails extends AppCompatActivity {

    //Shared Preferences
    private SharedPreferences sPref;

    Dialog ProgressDialog;

    @BindView(R.id.ly_back)
    RelativeLayout ly_back;

    @BindView(R.id.txt_ordernoanddate)
    CustomTextView txt_ordernoanddate;

    @BindView(R.id.txt_cancle)
    CustomTextView txt_cancle;

    @BindView(R.id.txt_name)
    CustomTextView txt_name;

    @BindView(R.id.txt_city)
    CustomTextView txt_city;

    @BindView(R.id.txt_address)
    CustomTextView txt_address;

    @BindView(R.id.txt_mobile)
    CustomTextView txt_mobile;

    @BindView(R.id.layout_addproduct)
    LinearLayout layout_addproduct;

    @BindView(R.id.layout_main)
    LinearLayout layout_main;

    @BindView(R.id.txt_pament)
    CustomTextView txt_pament;

    @BindView(R.id.txt_subtotal)
    CustomTextView txt_subtotal;

    @BindView(R.id.txt_shipping)
    CustomTextView txt_shipping;

    @BindView(R.id.txt_tax)
    CustomTextView txt_tax;

    @BindView(R.id.txt_taxwithtotal)
    CustomTextView txt_taxwithtotal;

    @BindView(R.id.txt_total)
    CustomTextView txt_total;

    OrderHistoryItem orderHistoryItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        orderHistoryItem = (OrderHistoryItem) intent.getSerializableExtra("OrderHistoryItem");

        sPref = PreferenceManager.getDefaultSharedPreferences(ActivityOrderDetails.this);

        initdata();
    }

    private void initdata() {


        ProgressDialog = new Dialog(ActivityOrderDetails.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);

        ly_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_ordernoanddate.setText(getResources().getString(R.string.your_order) + " " + orderHistoryItem.getOrderId() + " " + getResources().getString(R.string.placeon) + " ");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(orderHistoryItem.getOrderDate());
            //SimpleDateFormat pickupDateTime = new SimpleDateFormat("MMMM d - hh:mm a");
            SimpleDateFormat pickupDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm aaa");
            String strPickup = pickupDateTime.format(date);

            txt_ordernoanddate.setText(getResources().getString(R.string.your_order) + " " + orderHistoryItem.getOrderId() + " " + getResources().getString(R.string.placeon) + " " + strPickup);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (orderHistoryItem.getBookStatus().equals("0")) {
            txt_cancle.setText(getResources().getString(R.string.cancel));
            txt_cancle.setBackgroundColor(getResources().getColor(R.color.gray_100));
        } else if (orderHistoryItem.getBookStatus().equals("2")) {
            txt_cancle.setText(getResources().getString(R.string.cancelled));
            txt_cancle.setBackgroundColor(getResources().getColor(R.color.red_light));
        }

        txt_name.setText(orderHistoryItem.getAddressName());
        txt_city.setText(orderHistoryItem.getAddress1());
        txt_address.setText(orderHistoryItem.getCity() + "," + orderHistoryItem.getState() + "," + orderHistoryItem.getPincode());
        txt_mobile.setText(getResources().getString(R.string.phone) + " " + orderHistoryItem.getMobile());

        if (!orderHistoryItem.getPaymentType().equals("") && orderHistoryItem.getPaymentType().equalsIgnoreCase("cash"))
            txt_pament.setText(getResources().getString(R.string.cod));
        else
            txt_pament.setText("" + orderHistoryItem.getPaymentType());

        txt_subtotal.setText(sPref.getString("CUR", "") + " " + orderHistoryItem.getTotalPrice());
        txt_tax.setText(getResources().getString(R.string.tax) + " (" + orderHistoryItem.getTax() + " %)");
        txt_taxwithtotal.setText(sPref.getString("CUR", "") + " " + orderHistoryItem.getTaxPrice());

        float sub_totol = 0, tax_price = 0;

        if (!orderHistoryItem.getTotalPrice().equals(""))
            sub_totol = Float.parseFloat(orderHistoryItem.getTotalPrice());

        if (!orderHistoryItem.getTaxPrice().equals(""))
            tax_price = Float.parseFloat(orderHistoryItem.getTaxPrice());


        txt_total.setText(sPref.getString("CUR", "") + " " + (sub_totol + tax_price));

        if (orderHistoryItem.getProductDetail() != null && orderHistoryItem.getProductDetail().size() > 0)
            SetProductList(orderHistoryItem.getProductDetail());


        txt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestCancelOrder(orderHistoryItem,orderHistoryItem.getOrderId());
            }
        });
    }

    private void SetProductList(final List<ProductItem> productItemList) {
        layout_addproduct.removeAllViews();

        for (int i = 0; i < productItemList.size(); i++) {

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.row_orderdetails_product, layout_main, false);

            ImageView image_order_product = (ImageView) view.findViewById(R.id.image_order_product);
            CustomTextView txt_order_productname = (CustomTextView) view.findViewById(R.id.txt_order_productname);
            CustomTextView txt_order_productprice = (CustomTextView) view.findViewById(R.id.txt_order_productprice);
            CustomTextView txt_order_productquatity = (CustomTextView) view.findViewById(R.id.txt_order_productquatity);
            CustomTextView txt_order_totalprice = (CustomTextView) view.findViewById(R.id.txt_order_totalprice);
            LinearLayout layout_product = (LinearLayout) view.findViewById(R.id.layout_product);

            ProductItem productItem = productItemList.get(i);

            if (!productItem.getColorCode().equals("") && !productItem.getSizeName().equals(""))
                txt_order_productname.setText(productItem.getProductName() + " - " + getResources().getString(R.string.color) + productItem.getColorName() + "," + getResources().getString(R.string.size) + productItem.getSizeName());
            else if (!productItem.getColorCode().equals(""))
                txt_order_productname.setText(productItem.getProductName() + " - " + getResources().getString(R.string.color) + productItem.getColorName());
            else if (!productItem.getSizeName().equals(""))
                txt_order_productname.setText(productItem.getProductName() + " - " + getResources().getString(R.string.size) + productItem.getSizeName());
            else
                txt_order_productname.setText(productItem.getProductName());


            txt_order_productprice.setText(sPref.getString("CUR", "") + " " + productItem.getPrice());

            txt_order_productquatity.setText(productItem.getQuantity());

            float price = 0, quatity = 0;

            if (!productItem.getPrice().equals(""))
                price = Float.parseFloat(productItem.getPrice());

            if (!productItem.getQuantity().equals(""))
                quatity = Float.parseFloat(productItem.getQuantity());

            txt_order_totalprice.setText(sPref.getString("CUR", "") + " " + (price * quatity));

            if (!productItem.getOrderImage().equals("")) {
                String temp = "" + Url.product_size_url + productItem.getOrderImage();

                boolean isWhitespace = Utils.containsWhitespace(temp);
                if (isWhitespace)
                    temp = temp.replaceAll(" ", "%20");

                if (!temp.equalsIgnoreCase("null") && !temp.equals("")) {
                    Picasso.with(ActivityOrderDetails.this).load(temp).into(image_order_product);
                }
            } else {
                String temp = "" + Url.product_url + productItem.getProductPrimaryImage();

                boolean isWhitespace = Utils.containsWhitespace(temp);
                if (isWhitespace)
                    temp = temp.replaceAll(" ", "%20");

                if (!temp.equalsIgnoreCase("null") && !temp.equals("")) {
                    Picasso.with(ActivityOrderDetails.this).load(temp).into(image_order_product);
                }
            }

            layout_product.setId(i);
            layout_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ProductItem productItem = productItemList.get(v.getId());

                    Intent intent = new Intent(ActivityOrderDetails.this, ActivityProductDetails.class);
                    intent.putExtra("ProductItem", productItem);
                    startActivity(intent);
                }
            });


            layout_addproduct.addView(view);
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

                        txt_cancle.setText(getResources().getString(R.string.cancelled));
                        txt_cancle.setBackgroundColor(getResources().getColor(R.color.red_light));

                        obj.setBookStatus("2");

                        SharedPreferences.Editor sh = sPref.edit();
                        sh.putBoolean("iscall",true);
                        sh.commit();



                    } else if (resp.get("status").getAsString().equals("false")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive=0;
                        if (resp.has("Isactive") && !resp.get("Isactive").isJsonNull()) {
                            Isactive = resp.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(ActivityOrderDetails.this);
                        }
                        else if (resp.has("message") && !resp.get("message").isJsonNull()) {
                            Common.Errordialog(ActivityOrderDetails.this,resp.get("message").toString());
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
                    Common.ShowHttpErrorMessage(ActivityOrderDetails.this, message);
                }
            }
        });
    }
}
