package com.appsolace.estore.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.appsolace.estore.Model.FilterProduct.FilterTag;
import com.appsolace.estore.R;
import com.appsolace.estore.View.CustomTextView;

import java.util.ArrayList;
import java.util.List;



public class TagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private SharedPreferences userPref;
    private List<FilterTag> listItems, filterList;
    private Context mContext;

    public TagsAdapter(Context context, List<FilterTag> listItems) {
        userPref = PreferenceManager.getDefaultSharedPreferences(context);

        this.listItems = listItems;
        this.mContext = context;
        this.filterList = new ArrayList<FilterTag>();
        // we copy the original list to the filter list and use it for setting row values
        this.filterList.addAll(this.listItems);


    }

    public List<FilterTag> getlTaglist()
    {
        return listItems;
    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_filter_list, null);

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

        customViewHolder.txt_colorname.setText(filterList.get(position).getTag_name());

        customViewHolder.checkBox.setChecked(filterList.get(position).isTagselect());

        customViewHolder.checkBox.setTag(filterList.get(position));

        if (!id.equals("") && !id.isEmpty())
        {
            String[] data = id.split(",");

            for (int i = 0; i < data.length; i++)
            {
                if (data[i].equals(filterList.get(position).getTag_id()))
                {
                    customViewHolder.checkBox.setChecked(true);

                    filterList.get(pos).setTagselect(customViewHolder.checkBox.isChecked());

                    FilterTag listItem = filterList.get(pos);

                    for (int j = 0; j < listItems.size(); j++)
                    {
                        FilterTag listItemdata = listItems.get(i);

                        if (listItem.getTag_id().equals(listItemdata.getTag_id())) {
                            listItemdata.setTagselect(customViewHolder.checkBox.isChecked());
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
                FilterTag contact = (FilterTag) cb.getTag();

                contact.setTagselect(cb.isChecked());

                filterList.get(pos).setTagselect(cb.isChecked());

                FilterTag listItem = filterList.get(pos);


                for(int i=0; i<listItems.size(); i++)
                {
                    FilterTag listItemdata = listItems.get(i);

                    if (listItem.getTag_id().equals(listItemdata.getTag_id()))
                    {
                        listItemdata.setTagselect(cb.isChecked());
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
                    for (FilterTag item : listItems) {
                        if (item.getTag_name().toLowerCase().contains(text.toLowerCase())) {
                            // Adding Matched items
                            item.getTag_id();
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

        public ViewHolder(View view) {
            super(view);
            // this.txt_location_name = (TextView) view.findViewById(R.id.txt_location_name);

            this.lay_checkdata = (LinearLayout) view.findViewById(R.id.lay_checkdata);
            this.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            this.txt_colorname = (CustomTextView) view.findViewById(R.id.txt_colorname);


        }

    }

}
