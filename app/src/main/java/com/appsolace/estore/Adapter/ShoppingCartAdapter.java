package com.appsolace.estore.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.loopeer.itemtouchhelperextension.Extension;
import com.squareup.picasso.Picasso;
import com.appsolace.estore.Model.ProductListModel.ProductItem;
import com.appsolace.estore.R;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.Utili.Utils;
import com.appsolace.estore.View.CustomTextView;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.callback.CallbackMessage;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.connection.RestAdapter;
import com.appsolace.estore.customanimation.ArcConfiguration;
import com.appsolace.estore.customanimation.SimpleArcDialog;
import com.appsolace.estore.customanimation.SimpleArcLoader;


import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    SharedPreferences sPref;
    private List<ProductItem> mDatas;
    private Activity mContext;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private boolean loading;
    private boolean isScrollEnabled = true;
    private OnLoadMoreListener onLoadMoreListener;

    //Loader
    SimpleArcDialog mArcLoader = null;

    private Dialog dialog;

    private OnItemClickListener onItemClickListener;
    private OnItemMinsClick onItemMinsClick;
    private OnItemPuchClick onItemPuchClick;
    private OnItemDelete onItemDelete;

    public ShoppingCartAdapter(Activity context, List<ProductItem> datas) {
        mDatas = datas;
        mContext = context;
        sPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shopingcart, parent, false);
        vh = new ItemSwipeWithActionWidthViewHolder(v);

        return vh;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof ShoppingCartAdapter.ItemBaseViewHolder) {

            ItemBaseViewHolder vholder = (ItemBaseViewHolder) holder;

            vholder.bind(mDatas.get(position));

            final ProductItem productItem = mDatas.get(position);


            vholder.btn_plus.setTag(position);
            vholder.btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onItemPuchClick != null) {
                        onItemPuchClick.onItemClick(v, productItem, Integer.parseInt(v.getTag().toString()));
                    }
                }
            });

            vholder.btn_min.setTag(position);
            vholder.btn_min.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onItemMinsClick != null) {
                        onItemMinsClick.onItemClick(v, productItem, Integer.parseInt(v.getTag().toString()));
                    }
                }
            });

            vholder.mViewContent.setTag(position);
            vholder.mViewContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, productItem, Integer.parseInt(v.getTag().toString()));
                    }
                }
            });

            if (holder instanceof ItemSwipeWithActionWidthViewHolder) {

                ItemSwipeWithActionWidthViewHolder viewHolder = (ItemSwipeWithActionWidthViewHolder) holder;
                /*viewHolder.mActionContainerParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteConfirmation(holder.getAdapterPosition());
                    }
                });*/

             /*   viewHolder.mActionViewRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Edit Data

                    }
                });*/

                viewHolder.mActionViewDelete.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Delete Data
                                //doDelete(holder.getAdapterPosition());

                                deleteConfirmation(holder.getAdapterPosition());
                            }
                        }

                );

            }
        }
    }

    private void doDelete(int adapterPosition) {
        mDatas.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);

    }

    public void move(int from, int to) {
        ProductItem prev = mDatas.remove(from);
        mDatas.add(to > from ? to - 1 : to, prev);
        notifyItemMoved(from, to);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class ItemBaseViewHolder extends RecyclerView.ViewHolder {

        CustomTextView txt_order_productname, txt_order_productcolorsize, txt_order_productprice;
        ImageView image_order_product;
        Button btn_min, btn_quty, btn_plus;
        public View mViewContent;
        public View mActionContainerParent;

        public ItemBaseViewHolder(View itemView) {
            super(itemView);

            txt_order_productname = (CustomTextView) itemView.findViewById(R.id.txt_order_productname);
            txt_order_productcolorsize = (CustomTextView) itemView.findViewById(R.id.txt_order_productcolorsize);
            txt_order_productprice = (CustomTextView) itemView.findViewById(R.id.txt_order_productprice);
            image_order_product = (ImageView) itemView.findViewById(R.id.image_order_product);
            btn_min = (Button) itemView.findViewById(R.id.btn_min);
            btn_quty = (Button) itemView.findViewById(R.id.btn_quty);
            btn_plus = (Button) itemView.findViewById(R.id.btn_plus);

            mViewContent = itemView.findViewById(R.id.view_list_main_content);
            mActionContainerParent = itemView.findViewById(R.id.view_list_repo_action_container);
        }

        public void bind(final ProductItem productItem) {

            txt_order_productname.setText("" + productItem.getProductName());

            if (!productItem.getColorCode().equals("") && !productItem.getSizeName().equals(""))
                txt_order_productcolorsize.setText("" + productItem.getSizeName() + "," + productItem.getColorName());
            else if (!productItem.getColorCode().equals(""))
                txt_order_productcolorsize.setText("" + productItem.getColorName());
            else if (!productItem.getSizeName().equals(""))
                txt_order_productcolorsize.setText("" + productItem.getSizeName());
            else
                txt_order_productcolorsize.setVisibility(View.GONE);

            txt_order_productprice.setText("" + sPref.getString("CUR", "") + " " + productItem.getPrice());

            btn_quty.setText("" + productItem.getQuantity());

            if (!productItem.getProductPrimaryImage().equals("")) {

                String temp = "" + productItem.getProductPrimaryImage();

                boolean isWhitespace = Utils.containsWhitespace(temp);
                if (isWhitespace)
                    temp = temp.replaceAll(" ", "%20");

                if (!temp.equalsIgnoreCase("null") && !temp.equals("")) {
                    Picasso.with(mContext).load(temp).into(image_order_product);
                }
            }
        }
    }

    class ItemSwipeWithActionWidthViewHolder extends ItemBaseViewHolder implements Extension {

        View mActionViewDelete;
        // View mActionViewRefresh;

        public ItemSwipeWithActionWidthViewHolder(View itemView) {
            super(itemView);
            mActionViewDelete = itemView.findViewById(R.id.view_list_repo_action_delete);
            //mActionViewRefresh = itemView.findViewById(R.id.view_list_repo_action_update);
        }

        @Override
        public float getActionWidth() {
            return mActionContainerParent.getWidth();
        }
    }

    private void deleteConfirmation(final int position) {

        Dialog dialogDelete = new DialogUtils(mContext).buildDialogLogout(new CallbackMessage() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                //doDelete(position);

                if (Common.isNetworkAvailable(mContext)) {
                    ProductItem productItem = mDatas.get(position);
                    requestDeletCartItem(position, productItem.getOrder_id());
                }

            }

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }

        }, mContext.getResources().getString(R.string.delete_confirmation));
        dialogDelete.show();

    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoading() {

        if (getItemCount() != 0) {
            this.mDatas.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }

    }

    public void resetListData() {
        isScrollEnabled = true;
        this.mDatas = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setScroll(boolean scrollEnabled) {
        isScrollEnabled = scrollEnabled;
    }

    public void setItems(ArrayList<ProductItem> items) {
        this.mDatas = items;
        notifyDataSetChanged();
    }

    public void insertData(ArrayList<ProductItem> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.mDatas.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (mDatas.get(i) == null) {
                mDatas.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ProductItem obj, int position);
    }

    public void setOnItemMinsClick(OnItemMinsClick onItemClickListener) {
        this.onItemMinsClick = onItemClickListener;
    }

    public interface OnItemMinsClick {
        void onItemClick(View view, ProductItem obj, int position);
    }

    public void setOnItemPlusClick(OnItemPuchClick onItemClickListener) {
        this.onItemPuchClick = onItemClickListener;
    }

    public interface OnItemPuchClick {
        void onItemClick(View view, ProductItem obj, int position);
    }

    public void setOnItemDelete(OnItemDelete onItemDelete) {
        this.onItemDelete = onItemDelete;
    }

    public interface OnItemDelete {
        void onDelete(List<ProductItem> mDatas);
    }

    private void requestDeletCartItem(final int position, String orderid) {

        mArcLoader = new SimpleArcDialog(mContext);
        ArcConfiguration configuration = new ArcConfiguration(mContext);
        configuration.setLoaderStyle(SimpleArcLoader.STYLE.SIMPLE_ARC);
        mArcLoader.setConfiguration(configuration);
        mArcLoader.show();

        API api = RestAdapter.createAPI();
        Call<JsonObject> callbackforgotpassword = api.remove_order(sPref.getString("uid", ""), orderid);
        callbackforgotpassword.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("onResponse", "=>" + response.body());

                JsonObject jsonObject = response.body();

                if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {

                    doDelete(position);

                    if (onItemDelete != null) {
                        onItemDelete.onDelete(mDatas);
                    }

                    if (mArcLoader != null && mArcLoader.isShowing())
                        mArcLoader.dismiss();

                } else {

                    if (mArcLoader != null && mArcLoader.isShowing())
                        mArcLoader.dismiss();

                    int Isactive = 0;
                    if (jsonObject.has("Isactive") && !jsonObject.get("Isactive").isJsonNull()) {
                        Isactive = jsonObject.get("Isactive").getAsInt();
                    }

                    if (Isactive == 0) {
                        Common.AccountLock(mContext);
                    } else if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                        Common.Errordialog(mContext, jsonObject.get("message").toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                if (mArcLoader != null && mArcLoader.isShowing())
                    mArcLoader.dismiss();

                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(mContext, message);
                }
            }
        });
    }

    private void ErrorMessage(String Message) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(mContext).buildDialogMessage(new CallbackMessage() {
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
}
