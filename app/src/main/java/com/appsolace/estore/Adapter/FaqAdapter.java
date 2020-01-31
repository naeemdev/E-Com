package com.appsolace.estore.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.appsolace.estore.Model.FaqData;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.R;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.MyViewHolder> {

    private Activity activity;
    private List<FaqData> newProductsList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CustomTextView txt_question, txt_dcription;
        RelativeLayout ly_faq;

        public MyViewHolder(View view) {
            super(view);
            txt_question = (CustomTextView) view.findViewById(R.id.txt_question);
            txt_dcription = (CustomTextView) view.findViewById(R.id.txt_dcription);
            ly_faq = (RelativeLayout) view.findViewById(R.id.ly_faq);
        }
    }

    public FaqAdapter(Activity activity, List<FaqData> newProductsList) {
        this.newProductsList = newProductsList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_faq, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final FaqData newProductItem = newProductsList.get(position);

        holder.txt_question.setText("" + newProductItem.getQuestion());
        holder.txt_dcription.setText("" + newProductItem.getDescription());

        holder.ly_faq.setOnClickListener(new View.OnClickListener() {
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