package com.appsolace.estore.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.Model.TestimonialData;
import com.appsolace.estore.R;
import com.appsolace.estore.Utili.CircleTransform;
import com.appsolace.estore.Utili.Url;

import java.util.List;

public class TestimonialAdapter extends RecyclerView.Adapter<TestimonialAdapter.MyViewHolder> {

    private Activity activity;
    private List<TestimonialData> newProductsList;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CustomTextView txt_name, txt_dcription;
        public ImageView testmonial_image;
        RelativeLayout ly_testimonial,rl_imageview,card_view;

        public MyViewHolder(View view) {
            super(view);
            txt_name = (CustomTextView) view.findViewById(R.id.txt_name);
            txt_dcription = (CustomTextView) view.findViewById(R.id.txt_dcription);
            testmonial_image = (ImageView) view.findViewById(R.id.testmonial_image);
            ly_testimonial = (RelativeLayout) view.findViewById(R.id.ly_testimonial);
            rl_imageview = (RelativeLayout) view.findViewById(R.id.rl_imageview);
            card_view = (RelativeLayout) view.findViewById(R.id.card_view);
        }
    }

    public TestimonialAdapter(Activity activity,List<TestimonialData> newProductsList) {
        this.newProductsList = newProductsList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_testimonial, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final TestimonialData newProductItem = newProductsList.get(position);

        holder.txt_name.setText(newProductItem.getName());
        holder.txt_dcription.setText("" + newProductItem.getDescription());

        String temp = Url.testimonial_url + newProductItem.getImage();
        temp = temp.replaceAll(" ", "%20");


        Picasso.with(activity).load(temp).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).transform(new CircleTransform()).into(holder.testmonial_image);


        holder.ly_testimonial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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