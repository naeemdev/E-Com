package com.appsolace.estore.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.customanimation.SimpleArcLoader;
import com.appsolace.estore.Model.Subcategorydata;
import com.appsolace.estore.R;
import com.appsolace.estore.Utili.Url;
import com.appsolace.estore.Utili.Utils;

import java.util.ArrayList;
import java.util.List;

public class AllProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Subcategorydata> listUpcoming;
    private Activity activity;
    private boolean loading;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private boolean isScrollEnabled = true;
    private OnItemClickListener onItemClickListener;
    private OnLoadMoreListener onLoadMoreListener;
    private int height, width;

    public AllProductAdapter(Activity activity, RecyclerView view, List<Subcategorydata> items, int width, int height) {
        this.listUpcoming = items;
        this.activity = activity;
        this.width = width;
        this.height = height;
        lastItemViewDetector(view);
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


    public void setItems(List<Subcategorydata> items) {
        this.listUpcoming = items;
        notifyDataSetChanged();
    }


    public void insertData(List<Subcategorydata> items) {
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_categoryproduct_item, parent, false);
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
            final Subcategorydata c = listUpcoming.get(position);

            if(position %2 == 0) {
                vItem.layout_one.setVisibility(View.VISIBLE);
                vItem.layout_two.setVisibility(View.GONE);
                vItem.category_name1.setText(c.getCategory_name());
                vItem.layoutClick.setBackgroundColor(Color.parseColor(c.getCategory_color()));

                String temp= Url.category_url+ c.getCategory_image();

                boolean isWhitespace = Utils.containsWhitespace(temp);
                if (isWhitespace)
                    temp = temp.replaceAll(" ", "%20");

                Picasso.with(activity).load(temp).resize(width / 2, width / 2).into(vItem.item_img1);

            }
            else
            {
                vItem.layout_one.setVisibility(View.GONE);
                vItem.layout_two.setVisibility(View.VISIBLE);
                vItem.category_name2.setText(c.getCategory_name());
                vItem.layoutClick.setBackgroundColor(Color.parseColor(c.getCategory_color()));


                String temp= Url.category_url+ c.getCategory_image();

                boolean isWhitespace = Utils.containsWhitespace(temp);
                if (isWhitespace)
                    temp = temp.replaceAll(" ", "%20");


                Picasso.with(activity).load(temp).resize(width / 2, width / 2).into(vItem.item_img2);

            }

            vItem.layoutClick.setTag(position);
            vItem.layoutClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, c, Integer.parseInt(v.getTag().toString()));
                    }

                }
            });

        }
        // else ((ProgressViewHolder) holder).arc_loader.start();
    }


    @Override
    public int getItemCount() {
        return listUpcoming.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout layoutClick;

        /*one*/
        LinearLayout layout_one;
        ImageView item_img1;
        CustomTextView category_name1, category_shop1;

        /*Two*/
        LinearLayout layout_two;
        ImageView item_img2;
        CustomTextView category_name2, category_shop2;

        public OriginalViewHolder(View v) {
            super(v);

            layoutClick = (RelativeLayout) v.findViewById(R.id.layoutClick);

              /*one*/
            layout_one = (LinearLayout) v.findViewById(R.id.layout_one);
            category_name1 = (CustomTextView) v.findViewById(R.id.category_name1);
            category_shop1 = (CustomTextView) v.findViewById(R.id.category_shop1);
            item_img1 = (ImageView) v.findViewById(R.id.item_img1);

            /*two*/
            layout_two = (LinearLayout) v.findViewById(R.id.layout_two);
            category_name2 = (CustomTextView) v.findViewById(R.id.category_name2);
            category_shop2 = (CustomTextView) v.findViewById(R.id.category_shop2);
            item_img2 = (ImageView) v.findViewById(R.id.item_img2);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Subcategorydata obj, int position);
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


}
