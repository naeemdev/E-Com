package com.appsolace.estore.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appsolace.estore.R;
import com.google.gson.JsonObject;
import com.loopeer.itemtouchhelperextension.Extension;
import com.appsolace.estore.Actvity.ActivityAddAddress;
import com.appsolace.estore.Model.Address;

import com.appsolace.estore.Utili.Common;
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

public class AddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    SharedPreferences sPref;
    private List<Address> mDatas;
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

    public AddressAdapter(Activity context, List<Address> datas) {
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

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_address, parent, false);
        vh = new ItemSwipeWithActionWidthViewHolder(v);

        return vh;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof AddressAdapter.ItemBaseViewHolder) {

            ItemBaseViewHolder vholder = (ItemBaseViewHolder) holder;

            vholder.bind(mDatas.get(position));

            final Address address = mDatas.get(position);


            vholder.mViewContent.setTag(position);
            vholder.mViewContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, address, Integer.parseInt(v.getTag().toString()));
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

                viewHolder.mActionViewEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Edit Data

                        SharedPreferences.Editor sh =sPref.edit();
                        sh.putBoolean("isEdit",true);
                        sh.commit();

                        Intent i = new Intent(mContext,ActivityAddAddress.class);
                        i.putExtra("Address",address);
                        mContext.startActivity(i);
                    }
                });

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
        Address prev = mDatas.remove(from);
        mDatas.add(to > from ? to - 1 : to, prev);
        notifyItemMoved(from, to);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class ItemBaseViewHolder extends RecyclerView.ViewHolder {

        CustomTextView txt_name, txt_address, txt_phone;

        public View mViewContent;
        public View mActionContainerParent;

        public ItemBaseViewHolder(View itemView) {
            super(itemView);

            txt_name = (CustomTextView) itemView.findViewById(R.id.txt_name);
            txt_address = (CustomTextView) itemView.findViewById(R.id.txt_address);
            txt_phone = (CustomTextView) itemView.findViewById(R.id.txt_phone);

            mViewContent = itemView.findViewById(R.id.view_list_main_content);
            mActionContainerParent = itemView.findViewById(R.id.view_list_repo_action_container);
        }

        public void bind(final Address address) {

            txt_name.setText("" + address.getName());
            txt_address.setText("" + address.getAddress());
            txt_phone.setText(mContext.getResources().getString(R.string.phone) + " " + address.getMobile_no());

        }
    }

    class ItemSwipeWithActionWidthViewHolder extends ItemBaseViewHolder implements Extension {

        View mActionViewDelete;
        View mActionViewEdit;

        public ItemSwipeWithActionWidthViewHolder(View itemView) {
            super(itemView);
            mActionViewDelete = itemView.findViewById(R.id.view_list_repo_action_delete);
            mActionViewEdit = itemView.findViewById(R.id.view_list_repo_action_update);
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
                    Address productItem = mDatas.get(position);
                    requestDeletCartItem(position, productItem.getAddress_id());
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

    public void setItems(ArrayList<Address> items) {
        this.mDatas = items;
        notifyDataSetChanged();
    }

    public void insertData(ArrayList<Address> items) {
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
        void onItemClick(View view, Address obj, int position);
    }

    private void requestDeletCartItem(final int position, String addressid) {

        mArcLoader = new SimpleArcDialog(mContext);
        ArcConfiguration configuration = new ArcConfiguration(mContext);
        configuration.setLoaderStyle(SimpleArcLoader.STYLE.SIMPLE_ARC);
        mArcLoader.setConfiguration(configuration);
        mArcLoader.show();

        API api = RestAdapter.createAPI();
        Call<JsonObject> callbackforgotpassword = api.remove_address(sPref.getString("uid", ""), addressid);
        callbackforgotpassword.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("onResponse", "=>" + response.body());

                JsonObject jsonObject = response.body();

                if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {



                    doDelete(position);


                    if (mArcLoader != null && mArcLoader.isShowing())
                        mArcLoader.dismiss();

                } else {

                    if (mArcLoader != null && mArcLoader.isShowing())
                        mArcLoader.dismiss();

                    int Isactive=0;
                    if (jsonObject.has("Isactive") && !jsonObject.get("Isactive").isJsonNull()) {
                        Isactive = jsonObject.get("Isactive").getAsInt();
                    }

                    if (Isactive == 0) {
                        Common.AccountLock(mContext);
                    }
                    else if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                        Common.Errordialog(mContext,jsonObject.get("message").toString());
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
