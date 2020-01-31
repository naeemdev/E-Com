package com.appsolace.estore.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appsolace.estore.Model.FilterProduct.FilterColor;
import com.appsolace.estore.R;
import com.appsolace.estore.View.CustomTextView;

import java.util.ArrayList;
import java.util.List;



public class ColorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SharedPreferences userPref;
    private List<FilterColor> listItems, filterList;
    private Context mContext;

    public ColorAdapter(Context context, List<FilterColor> listItems) {
        userPref = PreferenceManager.getDefaultSharedPreferences(context);

        this.listItems = listItems;
        this.mContext = context;
        this.filterList = new ArrayList<FilterColor>();
        // we copy the original list to the filter list and use it for setting row values
        this.filterList.addAll(this.listItems);


    }

    public List<FilterColor> getColorlist() {
        return listItems;
    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_filter_color_list, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final int pos = position;

        String id = userPref.getString("FilterLocid", "");
        String name = userPref.getString("FilterLocanm", "");

        final ViewHolder customViewHolder = (ViewHolder) holder;

        if (!filterList.get(position).getColor_code().equals("") && !filterList.get(position).getColor_code().equals("null"))
            customViewHolder.image_color.setBackgroundColor(Color.parseColor(filterList.get(position).getColor_code()));

        customViewHolder.txt_colorname.setText(filterList.get(position).getColor_name());

        customViewHolder.checkBox.setChecked(filterList.get(position).isColorselect());

        customViewHolder.checkBox.setTag(filterList.get(position));

        if (!id.equals("") && !id.isEmpty()) {
            String[] data = id.split(",");

            for (int i = 0; i < data.length; i++) {
                if (data[i].equals(filterList.get(position).getColor_id())) {
                    customViewHolder.checkBox.setChecked(true);

                    filterList.get(pos).setColorselect(customViewHolder.checkBox.isChecked());

                    FilterColor listItem = filterList.get(pos);

                    for (int j = 0; j < listItems.size(); j++) {
                        FilterColor listItemdata = listItems.get(i);

                        if (listItem.getColor_id().equals(listItemdata.getColor_id())) {
                            listItemdata.setColorselect(customViewHolder.checkBox.isChecked());
                            break;
                        }
                    }
                }

            }

        }

        customViewHolder.lay_checkdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customViewHolder.checkBox.performClick();
            }
        });

        customViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                FilterColor contact = (FilterColor) cb.getTag();

                contact.setColorselect(cb.isChecked());

                filterList.get(pos).setColorselect(cb.isChecked());

                FilterColor listItem = filterList.get(pos);


                for (int i = 0; i < listItems.size(); i++) {
                    FilterColor listItemdata = listItems.get(i);

                    if (listItem.getColor_id().equals(listItemdata.getColor_id())) {
                        listItemdata.setColorselect(cb.isChecked());
                        break;
                    }
                }
                // Toast.makeText(v.getContext(), "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Do Search...
    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Clear the filter list
                filterList.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {

                    filterList.addAll(listItems);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (FilterColor item : listItems) {
                        if (item.getColor_name().toLowerCase().contains(text.toLowerCase())) {
                            // Adding Matched items
                            item.getColor_id();
                            filterList.add(item);
                        }
                    }
                }

                // Set on UI Thread
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();

    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        //return listItems.size();
        return (null != filterList ? filterList.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        // protected TextView txt_location_name;
        CheckBox checkBox;
        LinearLayout lay_checkdata;
        CustomTextView txt_colorname;
        ImageView image_color;

        public ViewHolder(View view) {
            super(view);
            // this.txt_location_name = (TextView) view.findViewById(R.id.txt_location_name);

            this.lay_checkdata = (LinearLayout) view.findViewById(R.id.lay_checkdata);
            this.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            this.txt_colorname = (CustomTextView) view.findViewById(R.id.txt_colorname);
            this.txt_colorname = (CustomTextView) view.findViewById(R.id.txt_colorname);
            this.image_color = (ImageView) view.findViewById(R.id.image_color);


        }

    }

}
