package com.appsolace.estore.Adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.appsolace.estore.Model.OrderHistory.OrderHistoryItem;
import com.appsolace.estore.R;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.customanimation.SimpleArcLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderHistoryItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<OrderHistoryItem> listUpcoming;
    private Activity activity;
    private boolean loading;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private boolean isScrollEnabled = true;
    private OnItemClickListener onItemClickListener;
    private OnItemCancle onItemCancel;
    private OnLoadMoreListener onLoadMoreListener;
    private int height, width;
    private SharedPreferences sPref;

    public OrderHistoryItemAdapter(Activity activity, RecyclerView view, List<OrderHistoryItem> items) {
        this.listUpcoming = items;
        this.activity = activity;
        lastItemViewDetector(view);

        sPref = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public SimpleArcLoader arc_loader;

        public ProgressViewHolder(View v) {
            super(v);
            arc_loader = (SimpleArcLoader) v.findViewById(R.id.arc_loader);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return this.listUpcoming.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.listUpcoming.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        isScrollEnabled = true;
        this.listUpcoming = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setItems(List<OrderHistoryItem> items) {
        this.listUpcoming = items;
        notifyDataSetChanged();
    }

    public void insertData(List<OrderHistoryItem> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.listUpcoming.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (listUpcoming.get(i) == null) {
                listUpcoming.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setScroll(boolean scrollEnabled) {
        isScrollEnabled = scrollEnabled;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_orderhistory, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof OriginalViewHolder) {

            OriginalViewHolder vItem = (OriginalViewHolder) holder;
            final OrderHistoryItem c = listUpcoming.get(position);


            if (c.getBookStatus().equals("0")) {
                vItem.txt_cancle.setText(activity.getResources().getString(R.string.cancel));
                vItem.lay_orderview.setBackgroundResource(R.drawable.hafl_round_gray);
            } else if (c.getBookStatus().equals("2")) {
                vItem.txt_cancle.setText(activity.getResources().getString(R.string.cancelled));
                vItem.lay_orderview.setBackgroundResource(R.drawable.hafl_round_red);
            }

            vItem.txt_orderno.setText(activity.getResources().getString(R.string.order_no) + " " + c.getOrderId());

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(c.getOrderDate());
                //SimpleDateFormat pickupDateTime = new SimpleDateFormat("MMMM d - hh:mm a");
                SimpleDateFormat pickupDateTime = new SimpleDateFormat("EEEE, dd MMM yyyy");
                String strPickup = pickupDateTime.format(date);

                vItem.txt_placeon.setText(activity.getResources().getString(R.string.placed_on) + " " + strPickup);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            float price = 0, tax_price = 0;
            if (!c.getTotalPrice().equals(""))
                price = Float.parseFloat(c.getTotalPrice());

            if (!c.getTaxPrice().equals(""))
                tax_price = Float.parseFloat(c.getTaxPrice());

            vItem.txt_price_and_item.setText(sPref.getString("CUR", "") + " " + (price + tax_price) + " / " + c.getTotalQuantity() + activity.getString(R.string.item));

            List<String> strings_image = c.getPlacedProductImage();

            if (strings_image != null && strings_image.size() > 0)
                setImageList(strings_image, vItem);

            vItem.lay_orderview.setTag(position);
            vItem.lay_orderview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, c, Integer.parseInt(v.getTag().toString()));
                    }

                }
            });

            vItem.txt_cancle.setTag(position);
            vItem.txt_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onItemCancel != null) {
                        onItemCancel.oncancel(v, c, Integer.parseInt(v.getTag().toString()));
                    }

                }
            });

        }
        // else ((ProgressViewHolder) holder).arc_loader.start();
    }

    private void setImageList(List<String> imagelist, OriginalViewHolder vItem) {
        vItem.layout_addimage.removeAllViews();

        for (int i = 0; i < imagelist.size(); i++) {

            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.row_orderhistory_imageview, vItem.layout_main, false);

            ImageView image_order = (ImageView) view.findViewById(R.id.image_order);

            Picasso.with(activity).load(imagelist.get(i)).into(image_order);

            vItem.layout_addimage.addView(view);
        }
    }

    @Override
    public int getItemCount() {
        return listUpcoming.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout layoutClick;

        LinearLayout layout_main, layout_addimage, lay_orderview;
        CustomTextView txt_orderno, txt_placeon, txt_price_and_item, txt_cancle;


        public OriginalViewHolder(View v) {
            super(v);

            layoutClick = (RelativeLayout) v.findViewById(R.id.layoutClick);

              /*one*/
            layout_main = (LinearLayout) v.findViewById(R.id.layout_main);
            layout_addimage = (LinearLayout) v.findViewById(R.id.layout_addimage);
            lay_orderview = (LinearLayout) v.findViewById(R.id.lay_orderview);
            txt_orderno = (CustomTextView) v.findViewById(R.id.txt_orderno);
            txt_placeon = (CustomTextView) v.findViewById(R.id.txt_placeon);
            txt_price_and_item = (CustomTextView) v.findViewById(R.id.txt_price_and_item);
            txt_cancle = (CustomTextView) v.findViewById(R.id.txt_cancle);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, OrderHistoryItem obj, int position);
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null && isScrollEnabled) {
                        if (onLoadMoreListener != null) {
                            int current_page = getItemCount();
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    public void setOnItemCancel(OnItemCancle onItemCancle) {
        this.onItemCancel = onItemCancle;
    }

    public interface OnItemCancle {
        void oncancel(View view, OrderHistoryItem obj, int position);
    }

}
