package com.appsolace.estore.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.appsolace.estore.Actvity.ActivityNewProductDetails;
import com.appsolace.estore.Model.HomeData.NewProductItem;
import com.appsolace.estore.R;
import com.appsolace.estore.Utili.Url;
import com.appsolace.estore.Utili.Utils;
import com.appsolace.estore.View.CustomTextView;

import java.util.List;

public class Home_NewproductAdapter extends RecyclerView.Adapter<Home_NewproductAdapter.MyViewHolder> {
    private List<NewProductItem> newProductsList;
    int width, height;
    private Activity activity;

    private SharedPreferences sPref;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CustomTextView product_name, price;
        public ImageView imageView;
        LinearLayout rl;

        public MyViewHolder(View view) {
            super(view);
            product_name = (CustomTextView) view.findViewById(R.id.product_name);
            price = (CustomTextView) view.findViewById(R.id.price);
            imageView = (ImageView) view.findViewById(R.id.item_img);
            rl = (LinearLayout) view.findViewById(R.id.category_rl);

        }

    }

    public Home_NewproductAdapter(Activity activity, List<NewProductItem> newProductsList, int width, int height) {
        this.activity = activity;
        this.newProductsList = newProductsList;
        this.width = width;
        this.height = height;

        sPref = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_newprod_signle_item, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final NewProductItem newProductItem = newProductsList.get(position);

        holder.product_name.setText(newProductItem.getProductName());
        holder.price.setText(sPref.getString("CUR", "") + " " + newProductItem.getProductPrice());

        String temp = Url.product_url + newProductItem.getProductPrimaryImage();

        boolean isWhitespace = Utils.containsWhitespace(temp);

        if (isWhitespace)
            temp = temp.replaceAll(" ", "%20");


        Log.d("lemon", String.valueOf(temp));
        Picasso.with(holder.imageView.getContext()).load(temp).into(holder.imageView);

        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, ActivityNewProductDetails.class);
                intent.putExtra("NewProductItem", newProductItem);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newProductsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;

        return viewType;
    }

}