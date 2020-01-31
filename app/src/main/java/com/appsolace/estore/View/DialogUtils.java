package com.appsolace.estore.View;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;


import com.squareup.picasso.Picasso;
import com.appsolace.estore.Model.HomeData.NewProductItem;
import com.appsolace.estore.Model.MatchWithProduct.MatchProductItem;
import com.appsolace.estore.Model.ProductListModel.ProductSizeItem;
import com.appsolace.estore.Model.RecentlyProduct.RecentlyProductItem;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.Model.ProductListModel.ColorItem;
import com.appsolace.estore.Model.ProductListModel.ProductItem;
import com.appsolace.estore.Utili.Url;
import com.appsolace.estore.Utili.Utils;
import com.appsolace.estore.callback.CallbackInternate;
import com.appsolace.estore.callback.CallbackMessage;

import com.appsolace.estore.R;
import com.appsolace.estore.callback.ProductSizeCallbackMessage;

import java.util.List;

public class DialogUtils {

    private Activity activity;

    SharedPreferences sPref;

    LinearLayout layout_main;
    ImageView img_product;
    CustomTextView txt_buynow;
    CustomTextView txt_product_prise;


    List<com.appsolace.estore.Model.ProductListModel.ColorItem> color;
    List<com.appsolace.estore.Model.ProductListModel.ProductSizeItem> productSize;

    List<com.appsolace.estore.Model.MatchWithProduct.ColorItem> match_color;
    List<com.appsolace.estore.Model.MatchWithProduct.ProductSizeItem> match_productSize;

    List<com.appsolace.estore.Model.RecentlyProduct.ColorItem> recent_color;
    List<com.appsolace.estore.Model.RecentlyProduct.ProductSizeItem> recent_productSize;

    List<com.appsolace.estore.Model.HomeData.ColorItem> new_color;
    List<com.appsolace.estore.Model.HomeData.ProductSizeItem> new_productSize;

    //float discountAmount, discountAmountForServer;
    public DialogUtils(Activity activity) {
        this.activity = activity;
        sPref = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    private Dialog buildDialogView(@LayoutRes int layout, int styleAnimation) {
        final Dialog dialog = new Dialog(activity, R.style.DialogLevelCompleted);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (styleAnimation == 1)
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation1;
        else
            dialog.getWindow().getAttributes().windowAnimations = R.style.MoreDialogAnimation;
        return dialog;
    }

    //Dialog Msg
    public Dialog buildDialogMessage(final CallbackMessage callback, String message) {
        final Dialog dialog = buildDialogView(R.layout.dialog_alert, 1);
        ((CustomTextView) dialog.findViewById(R.id.txt_msg)).setText(message);
        ((CustomTextView) dialog.findViewById(R.id.txt_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSuccess(dialog);
            }
        });
        ((ImageButton) dialog.findViewById(R.id.image_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancel(dialog);
            }
        });

        return dialog;
    }

    //Dialog Msg
    public Dialog buildDialogRegister(final CallbackMessage callback, String message) {
        final Dialog dialog = buildDialogView(R.layout.dialog_alert, 1);
        ((CustomTextView) dialog.findViewById(R.id.txt_msg)).setText(message);
        ((LinearLayout) dialog.findViewById(R.id.layoutClose)).setVisibility(View.VISIBLE);
        ((CustomTextView) dialog.findViewById(R.id.txt_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSuccess(dialog);
            }
        });
        ((ImageButton) dialog.findViewById(R.id.image_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancel(dialog);
            }
        });

        return dialog;
    }

    //Dialog Internate
    public Dialog buildDialogInternate(final CallbackInternate callback) {


        final Dialog dialog = buildDialogView(R.layout.internetinfopanel, 1);
        ((RelativeLayout) dialog.findViewById(R.id.main)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSuccess(dialog);
            }
        });
        return dialog;
    }

    //Dialog Logout
    public Dialog buildDialogLogout(final CallbackMessage callback, String message) {
        final Dialog dialog = buildDialogView(R.layout.dialog_logout, 1);
        ((CustomTextView) dialog.findViewById(R.id.txt_msg)).setText(message);

        ((CustomTextView) dialog.findViewById(R.id.txt_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSuccess(dialog);
            }
        });

        ((CustomTextView) dialog.findViewById(R.id.txt_no)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancel(dialog);
            }
        });

        return dialog;
    }

    //Dialog ProductSize
    public Dialog buildDialogProdSizeColor(final ProductSizeCallbackMessage callback, ProductItem productItem) {
        final Dialog dialog = buildDialogView(R.layout.dialog_size_color, 1);
        //((CustomTextView) dialog.findViewById(R.id.txt_product_prise)).setText(activity.getResources().getString(R.string.EUR) + " " + productItem.getProductPrice());

        ((ImageButton) dialog.findViewById(R.id.image_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancel(dialog);
            }
        });

        layout_main = (LinearLayout) dialog.findViewById(R.id.layout_main);

        txt_product_prise = (CustomTextView) dialog.findViewById(R.id.txt_product_prise);
        txt_buynow = (CustomTextView) dialog.findViewById(R.id.txt_buynow);

        img_product = (ImageView) dialog.findViewById(R.id.img_product);
        final TabLayout tabLayout_addsize = (TabLayout) dialog.findViewById(R.id.tabLayout_addsize);
        final TabLayout tabLayout_addcolor = (TabLayout) dialog.findViewById(R.id.tabLayout_addcolor);

        txt_buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos_size = tabLayout_addsize.getSelectedTabPosition();
                int pos_color = tabLayout_addcolor.getSelectedTabPosition();

                final ProductSizeItem productSizeItem = productSize.get(pos_size);
                final ColorItem colorItem = color.get(pos_color);

                callback.onSuccess(dialog,productSizeItem.getSizeId(),colorItem.getColorCode());

            }
        });


        productSize = productItem.getProductSize();

        if (productSize != null && productSize.size() > 0) {

            for (int i = 0; i < productSize.size(); i++) {

                final ProductSizeItem productSizeItem = productSize.get(i);

                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.row_tabsize, layout_main, false);

                CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
                LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);

                tabOne.setText(productSizeItem.getSizeName());
                lay_size.setBackgroundResource(R.drawable.rounded_size);
                //tabLayout_addsize.getTabAt(Integer.parseInt(productSizeItem.getSizeId())).setCustomView(view);

                tabLayout_addsize.addTab(tabLayout_addsize.newTab().setCustomView(view));


            }

            TabLayout.Tab tab = tabLayout_addsize.getTabAt(0);
            tab.select();
            ProdselectSizeTab(0, tabLayout_addsize, tabLayout_addcolor);

            tabLayout_addsize.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    //tabLayout_addcolor.removeAllTabs();
                    int pos = tab.getPosition();
                    ProdselectSizeTab(pos, tabLayout_addsize, tabLayout_addcolor);

                    /*View view = tab.getCustomView();
                    CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
                    LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);
                    tabOne.setTextColor(activity.getResources().getColor(R.color.white));
                    lay_size.setBackgroundResource(R.drawable.rounded_sizefill);

                    ProductSizeItem productSizeItem = productSize.get(pos);
                    setColorData(productSizeItem.getColor());*/

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                    View view = tab.getCustomView();
                    CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
                    LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);

                    tabOne.setTextColor(activity.getResources().getColor(R.color.black_gray));
                    lay_size.setBackgroundResource(R.drawable.rounded_size);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }

        return dialog;
    }

    public void ProdselectSizeTab(int pos, TabLayout tabLayout_addsize, TabLayout tabLayout_addcolor) {
        TabLayout.Tab tab = tabLayout_addsize.getTabAt(pos);
        View view = tab.getCustomView();
        CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
        LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);
        tabOne.setTextColor(activity.getResources().getColor(R.color.white));
        lay_size.setBackgroundResource(R.drawable.rounded_sizefill);

        ProductSizeItem productSizeItem = productSize.get(pos);
        color = productSizeItem.getColor();
        ProdsetColorData(tabLayout_addcolor);

    }

    public void ProdsetColorData(final TabLayout tabLayout_addcolor) {
        tabLayout_addcolor.removeAllTabs();
        if (color != null && color.size() > 0) {

            for (int i = 0; i < color.size(); i++) {

                ColorItem colorItem = color.get(i);

                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.row_tabcolor, layout_main, false);

                CustomTextView txt_color = (CustomTextView) view.findViewById(R.id.txt_color);
                LinearLayout lay_color = (LinearLayout) view.findViewById(R.id.lay_color);
                ImageView img_color = (ImageView) view.findViewById(R.id.img_color);
                ImageView img_color1 = (ImageView) view.findViewById(R.id.img_color1);

                txt_color.setText(Utils.Getcolorname(colorItem.getColorCode()));
                if (!colorItem.getColorCode().equals("") && !colorItem.getColorCode().equalsIgnoreCase("null")) {
                    img_color.setBackgroundColor(Color.parseColor(colorItem.getColorCode()));
                    img_color1.setBackgroundColor(Color.parseColor(colorItem.getColorCode()));
                }

                lay_color.setBackgroundResource(R.drawable.rounded_size);

                //tabLayout_addcolor.getTabAt(Integer.parseInt(colorItem.getColorId())).setCustomView(view);
                tabLayout_addcolor.addTab(tabLayout_addcolor.newTab().setCustomView(view));

            }

            ColorItem colorItem = color.get(0);
            ProdselectColorTab(0, tabLayout_addcolor, colorItem);

            tabLayout_addcolor.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    int pos = tab.getPosition();
                    ColorItem colorItem = color.get(pos);
                    ProdselectColorTab(pos, tabLayout_addcolor, colorItem);

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                    View view = tab.getCustomView();

                    CustomTextView txt_color = (CustomTextView) view.findViewById(R.id.txt_color);
                    LinearLayout lay_color = (LinearLayout) view.findViewById(R.id.lay_color);
                    ImageView img_color = (ImageView) view.findViewById(R.id.img_color);
                    ImageView img_color1 = (ImageView) view.findViewById(R.id.img_color1);

                    txt_color.setTextColor(activity.getResources().getColor(R.color.black_gray));
                    lay_color.setBackgroundResource(R.drawable.rounded_size);
                    img_color.setVisibility(View.VISIBLE);
                    img_color1.setVisibility(View.GONE);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        }
    }

    public void ProdselectColorTab(int pos, TabLayout tabLayout_addcolor, ColorItem colorItem) {
        TabLayout.Tab tab = tabLayout_addcolor.getTabAt(pos);
        View view = tab.getCustomView();

        CustomTextView txt_color = (CustomTextView) view.findViewById(R.id.txt_color);
        LinearLayout lay_color = (LinearLayout) view.findViewById(R.id.lay_color);
        ImageView img_color = (ImageView) view.findViewById(R.id.img_color);
        ImageView img_color1 = (ImageView) view.findViewById(R.id.img_color1);

        String temp = Url.product_size_url + colorItem.getColorImage();

        boolean isWhitespace = Utils.containsWhitespace(temp);

        if (isWhitespace)
            temp = temp.replaceAll(" ", "%20");

        Picasso.with(activity).load(temp).into(img_product);

        if (!colorItem.getColorStock().equals("")) {
            int stock = Integer.parseInt(colorItem.getColorStock());

            if (stock > 0) {
                txt_buynow.setEnabled(true);
                txt_buynow.setText(activity.getString(R.string.buy_now));
                txt_buynow.setBackgroundResource(R.drawable.rounded_corner_red);

                txt_color.setTextColor(activity.getResources().getColor(R.color.white));
                lay_color.setBackgroundResource(R.drawable.rounded_sizefill);
                img_color1.setVisibility(View.VISIBLE);
                img_color.setVisibility(View.GONE);

            } else {
                txt_buynow.setEnabled(false);
                txt_buynow.setText(activity.getString(R.string.out_of_stock));
                txt_buynow.setBackgroundResource(R.drawable.rounded_graycolor);

                txt_color.setTextColor(activity.getResources().getColor(R.color.black_gray));
                lay_color.setBackgroundResource(R.drawable.rounded_size);
                img_color.setVisibility(View.VISIBLE);
                img_color1.setVisibility(View.GONE);
            }
        }

        txt_product_prise.setText(sPref.getString("CUR", "") + " " + colorItem.getColorPrice());
    }


    //Dialog MatchProductSize
    public Dialog buildDialogMatchSizeColor(final ProductSizeCallbackMessage callback, MatchProductItem productItem) {
        final Dialog dialog = buildDialogView(R.layout.dialog_size_color, 1);
        //((CustomTextView) dialog.findViewById(R.id.txt_product_prise)).setText(activity.getResources().getString(R.string.EUR) + " " + productItem.getProductPrice());

        ((ImageButton) dialog.findViewById(R.id.image_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancel(dialog);
            }
        });

        layout_main = (LinearLayout) dialog.findViewById(R.id.layout_main);

        txt_product_prise = (CustomTextView) dialog.findViewById(R.id.txt_product_prise);
        txt_buynow = (CustomTextView) dialog.findViewById(R.id.txt_buynow);

        img_product = (ImageView) dialog.findViewById(R.id.img_product);
        final TabLayout tabLayout_addsize = (TabLayout) dialog.findViewById(R.id.tabLayout_addsize);
        final TabLayout tabLayout_addcolor = (TabLayout) dialog.findViewById(R.id.tabLayout_addcolor);

        txt_buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int pos_size = tabLayout_addsize.getSelectedTabPosition();
                int pos_color = tabLayout_addcolor.getSelectedTabPosition();

                final com.appsolace.estore.Model.MatchWithProduct.ProductSizeItem productSizeItem = match_productSize.get(pos_size);

                com.appsolace.estore.Model.MatchWithProduct.ColorItem colorItem = match_color.get(pos_color);

                callback.onSuccess(dialog,productSizeItem.getSizeId(),colorItem.getColorCode());
            }
        });


        match_productSize = productItem.getProductSize();

        if (match_productSize != null && match_productSize.size() > 0) {

            for (int i = 0; i < match_productSize.size(); i++) {

                final com.appsolace.estore.Model.MatchWithProduct.ProductSizeItem productSizeItem = match_productSize.get(i);

                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.row_tabsize, layout_main, false);

                CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
                LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);

                tabOne.setText(productSizeItem.getSizeName());
                lay_size.setBackgroundResource(R.drawable.rounded_size);
                //tabLayout_addsize.getTabAt(Integer.parseInt(productSizeItem.getSizeId())).setCustomView(view);

                tabLayout_addsize.addTab(tabLayout_addsize.newTab().setCustomView(view));


            }

            TabLayout.Tab tab = tabLayout_addsize.getTabAt(0);
            tab.select();
            MatchselectSizeTab(0, tabLayout_addsize, tabLayout_addcolor);

            tabLayout_addsize.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    //tabLayout_addcolor.removeAllTabs();
                    int pos = tab.getPosition();
                    MatchselectSizeTab(pos, tabLayout_addsize, tabLayout_addcolor);

                    /*View view = tab.getCustomView();
                    CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
                    LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);
                    tabOne.setTextColor(activity.getResources().getColor(R.color.white));
                    lay_size.setBackgroundResource(R.drawable.rounded_sizefill);

                    ProductSizeItem productSizeItem = productSize.get(pos);
                    setColorData(productSizeItem.getColor());*/

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                    View view = tab.getCustomView();
                    CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
                    LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);

                    tabOne.setTextColor(activity.getResources().getColor(R.color.black_gray));
                    lay_size.setBackgroundResource(R.drawable.rounded_size);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }


        return dialog;
    }

    public void MatchselectSizeTab(int pos, TabLayout tabLayout_addsize, TabLayout tabLayout_addcolor) {
        TabLayout.Tab tab = tabLayout_addsize.getTabAt(pos);
        View view = tab.getCustomView();
        CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
        LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);
        tabOne.setTextColor(activity.getResources().getColor(R.color.white));
        lay_size.setBackgroundResource(R.drawable.rounded_sizefill);

        com.appsolace.estore.Model.MatchWithProduct.ProductSizeItem productSizeItem = match_productSize.get(pos);
        match_color = productSizeItem.getColor();
        MatchsetColorData(tabLayout_addcolor);

    }

    public void MatchsetColorData(final TabLayout tabLayout_addcolor) {
        tabLayout_addcolor.removeAllTabs();
        if (match_color != null && match_color.size() > 0) {

            for (int i = 0; i < match_color.size(); i++) {

                com.appsolace.estore.Model.MatchWithProduct.ColorItem colorItem = match_color.get(i);

                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.row_tabcolor, layout_main, false);

                CustomTextView txt_color = (CustomTextView) view.findViewById(R.id.txt_color);
                LinearLayout lay_color = (LinearLayout) view.findViewById(R.id.lay_color);
                ImageView img_color = (ImageView) view.findViewById(R.id.img_color);
                ImageView img_color1 = (ImageView) view.findViewById(R.id.img_color1);

                txt_color.setText(Utils.Getcolorname(colorItem.getColorCode()));
                if (!colorItem.getColorCode().equals("") && !colorItem.getColorCode().equalsIgnoreCase("null")) {
                    img_color.setBackgroundColor(Color.parseColor(colorItem.getColorCode()));
                    img_color1.setBackgroundColor(Color.parseColor(colorItem.getColorCode()));
                }

                lay_color.setBackgroundResource(R.drawable.rounded_size);

                //tabLayout_addcolor.getTabAt(Integer.parseInt(colorItem.getColorId())).setCustomView(view);
                tabLayout_addcolor.addTab(tabLayout_addcolor.newTab().setCustomView(view));

            }

            com.appsolace.estore.Model.MatchWithProduct.ColorItem colorItem = match_color.get(0);
            MatchselectColorTab(0, tabLayout_addcolor, colorItem);

            tabLayout_addcolor.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    int pos = tab.getPosition();
                    com.appsolace.estore.Model.MatchWithProduct.ColorItem colorItem = match_color.get(pos);
                    MatchselectColorTab(pos, tabLayout_addcolor, colorItem);

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                    View view = tab.getCustomView();

                    CustomTextView txt_color = (CustomTextView) view.findViewById(R.id.txt_color);
                    LinearLayout lay_color = (LinearLayout) view.findViewById(R.id.lay_color);
                    ImageView img_color = (ImageView) view.findViewById(R.id.img_color);
                    ImageView img_color1 = (ImageView) view.findViewById(R.id.img_color1);

                    txt_color.setTextColor(activity.getResources().getColor(R.color.black_gray));
                    lay_color.setBackgroundResource(R.drawable.rounded_size);
                    img_color.setVisibility(View.VISIBLE);
                    img_color1.setVisibility(View.GONE);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        }
    }

    public void MatchselectColorTab(int pos, TabLayout tabLayout_addcolor, com.appsolace.estore.Model.MatchWithProduct.ColorItem colorItem) {
        TabLayout.Tab tab = tabLayout_addcolor.getTabAt(pos);
        View view = tab.getCustomView();

        CustomTextView txt_color = (CustomTextView) view.findViewById(R.id.txt_color);
        LinearLayout lay_color = (LinearLayout) view.findViewById(R.id.lay_color);
        ImageView img_color = (ImageView) view.findViewById(R.id.img_color);
        ImageView img_color1 = (ImageView) view.findViewById(R.id.img_color1);

        String temp = Url.product_size_url + colorItem.getColorImage();

        boolean isWhitespace = Utils.containsWhitespace(temp);

        if (isWhitespace)
            temp = temp.replaceAll(" ", "%20");

        Picasso.with(activity).load(temp).into(img_product);

        if (!colorItem.getColorStock().equals("")) {
            int stock = Integer.parseInt(colorItem.getColorStock());

            if (stock > 0) {
                txt_buynow.setEnabled(true);
                txt_buynow.setText(activity.getString(R.string.buy_now));
                txt_buynow.setBackgroundResource(R.drawable.rounded_corner_red);

                txt_color.setTextColor(activity.getResources().getColor(R.color.white));
                lay_color.setBackgroundResource(R.drawable.rounded_sizefill);
                img_color1.setVisibility(View.VISIBLE);
                img_color.setVisibility(View.GONE);

            } else {
                txt_buynow.setEnabled(false);
                txt_buynow.setText(activity.getString(R.string.out_of_stock));
                txt_buynow.setBackgroundResource(R.drawable.rounded_graycolor);

                txt_color.setTextColor(activity.getResources().getColor(R.color.black_gray));
                lay_color.setBackgroundResource(R.drawable.rounded_size);
                img_color.setVisibility(View.VISIBLE);
                img_color1.setVisibility(View.GONE);
            }
        }

        txt_product_prise.setText(sPref.getString("CUR", "") + " " + colorItem.getColorPrice());
    }


    //Dialog RecentProductSize
    public Dialog buildDialogRecentSizeColor(final ProductSizeCallbackMessage callback, RecentlyProductItem productItem) {
        final Dialog dialog = buildDialogView(R.layout.dialog_size_color, 1);
        //((CustomTextView) dialog.findViewById(R.id.txt_product_prise)).setText(activity.getResources().getString(R.string.EUR) + " " + productItem.getProductPrice());

        ((ImageButton) dialog.findViewById(R.id.image_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancel(dialog);
            }
        });

        layout_main = (LinearLayout) dialog.findViewById(R.id.layout_main);

        txt_product_prise = (CustomTextView) dialog.findViewById(R.id.txt_product_prise);
        txt_buynow = (CustomTextView) dialog.findViewById(R.id.txt_buynow);

        img_product = (ImageView) dialog.findViewById(R.id.img_product);
        final TabLayout tabLayout_addsize = (TabLayout) dialog.findViewById(R.id.tabLayout_addsize);
        final TabLayout tabLayout_addcolor = (TabLayout) dialog.findViewById(R.id.tabLayout_addcolor);

        txt_buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos_size = tabLayout_addsize.getSelectedTabPosition();
                int pos_color = tabLayout_addcolor.getSelectedTabPosition();

                com.appsolace.estore.Model.RecentlyProduct.ProductSizeItem productSizeItem = recent_productSize.get(pos_size);

                com.appsolace.estore.Model.RecentlyProduct.ColorItem colorItem = recent_color.get(pos_color);

                callback.onSuccess(dialog,productSizeItem.getSizeId(),colorItem.getColorCode());
            }
        });


        recent_productSize = productItem.getProductSize();

        if (recent_productSize != null && recent_productSize.size() > 0) {

            for (int i = 0; i < recent_productSize.size(); i++) {

                final com.appsolace.estore.Model.RecentlyProduct.ProductSizeItem productSizeItem = recent_productSize.get(i);

                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.row_tabsize, layout_main, false);

                CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
                LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);

                tabOne.setText(productSizeItem.getSizeName());
                lay_size.setBackgroundResource(R.drawable.rounded_size);
                //tabLayout_addsize.getTabAt(Integer.parseInt(productSizeItem.getSizeId())).setCustomView(view);

                tabLayout_addsize.addTab(tabLayout_addsize.newTab().setCustomView(view));


            }

            TabLayout.Tab tab = tabLayout_addsize.getTabAt(0);
            tab.select();
            RecentselectSizeTab(0, tabLayout_addsize, tabLayout_addcolor);

            tabLayout_addsize.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    //tabLayout_addcolor.removeAllTabs();
                    int pos = tab.getPosition();
                    RecentselectSizeTab(pos, tabLayout_addsize, tabLayout_addcolor);

                    /*View view = tab.getCustomView();
                    CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
                    LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);
                    tabOne.setTextColor(activity.getResources().getColor(R.color.white));
                    lay_size.setBackgroundResource(R.drawable.rounded_sizefill);

                    ProductSizeItem productSizeItem = productSize.get(pos);
                    setColorData(productSizeItem.getColor());*/


                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                    View view = tab.getCustomView();
                    CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
                    LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);

                    tabOne.setTextColor(activity.getResources().getColor(R.color.black_gray));
                    lay_size.setBackgroundResource(R.drawable.rounded_size);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }

        return dialog;
    }

    public void RecentselectSizeTab(int pos, TabLayout tabLayout_addsize, TabLayout tabLayout_addcolor) {
        TabLayout.Tab tab = tabLayout_addsize.getTabAt(pos);
        View view = tab.getCustomView();
        CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
        LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);
        tabOne.setTextColor(activity.getResources().getColor(R.color.white));
        lay_size.setBackgroundResource(R.drawable.rounded_sizefill);

        com.appsolace.estore.Model.RecentlyProduct.ProductSizeItem productSizeItem = recent_productSize.get(pos);
        recent_color = productSizeItem.getColor();
        RecentsetColorData(tabLayout_addcolor);

    }

    public void RecentsetColorData(final TabLayout tabLayout_addcolor) {
        tabLayout_addcolor.removeAllTabs();
        if (recent_color != null && recent_color.size() > 0) {

            for (int i = 0; i < recent_color.size(); i++) {

                com.appsolace.estore.Model.RecentlyProduct.ColorItem colorItem = recent_color.get(i);

                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.row_tabcolor, layout_main, false);

                CustomTextView txt_color = (CustomTextView) view.findViewById(R.id.txt_color);
                LinearLayout lay_color = (LinearLayout) view.findViewById(R.id.lay_color);
                ImageView img_color = (ImageView) view.findViewById(R.id.img_color);
                ImageView img_color1 = (ImageView) view.findViewById(R.id.img_color1);

                txt_color.setText(Utils.Getcolorname(colorItem.getColorCode()));
                if (!colorItem.getColorCode().equals("") && !colorItem.getColorCode().equalsIgnoreCase("null")) {
                    img_color.setBackgroundColor(Color.parseColor(colorItem.getColorCode()));
                    img_color1.setBackgroundColor(Color.parseColor(colorItem.getColorCode()));
                }

                lay_color.setBackgroundResource(R.drawable.rounded_size);

                //tabLayout_addcolor.getTabAt(Integer.parseInt(colorItem.getColorId())).setCustomView(view);
                tabLayout_addcolor.addTab(tabLayout_addcolor.newTab().setCustomView(view));

            }

            com.appsolace.estore.Model.RecentlyProduct.ColorItem colorItem = recent_color.get(0);
            RecentselectColorTab(0, tabLayout_addcolor, colorItem);

            tabLayout_addcolor.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    int pos = tab.getPosition();
                    com.appsolace.estore.Model.RecentlyProduct.ColorItem colorItem = recent_color.get(pos);
                    RecentselectColorTab(pos, tabLayout_addcolor, colorItem);

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                    View view = tab.getCustomView();

                    CustomTextView txt_color = (CustomTextView) view.findViewById(R.id.txt_color);
                    LinearLayout lay_color = (LinearLayout) view.findViewById(R.id.lay_color);
                    ImageView img_color = (ImageView) view.findViewById(R.id.img_color);
                    ImageView img_color1 = (ImageView) view.findViewById(R.id.img_color1);

                    txt_color.setTextColor(activity.getResources().getColor(R.color.black_gray));
                    lay_color.setBackgroundResource(R.drawable.rounded_size);
                    img_color.setVisibility(View.VISIBLE);
                    img_color1.setVisibility(View.GONE);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        }
    }

    public void RecentselectColorTab(int pos, TabLayout tabLayout_addcolor, com.appsolace.estore.Model.RecentlyProduct.ColorItem colorItem) {
        TabLayout.Tab tab = tabLayout_addcolor.getTabAt(pos);
        View view = tab.getCustomView();

        CustomTextView txt_color = (CustomTextView) view.findViewById(R.id.txt_color);
        LinearLayout lay_color = (LinearLayout) view.findViewById(R.id.lay_color);
        ImageView img_color = (ImageView) view.findViewById(R.id.img_color);
        ImageView img_color1 = (ImageView) view.findViewById(R.id.img_color1);

        String temp = Url.product_size_url + colorItem.getColorImage();

        boolean isWhitespace = Utils.containsWhitespace(temp);

        if (isWhitespace)
            temp = temp.replaceAll(" ", "%20");

        Picasso.with(activity).load(temp).into(img_product);

        if (!colorItem.getColorStock().equals("")) {
            int stock = Integer.parseInt(colorItem.getColorStock());

            if (stock > 0) {
                txt_buynow.setEnabled(true);
                txt_buynow.setText(activity.getString(R.string.buy_now));
                txt_buynow.setBackgroundResource(R.drawable.rounded_corner_red);

                txt_color.setTextColor(activity.getResources().getColor(R.color.white));
                lay_color.setBackgroundResource(R.drawable.rounded_sizefill);
                img_color1.setVisibility(View.VISIBLE);
                img_color.setVisibility(View.GONE);

            } else {
                txt_buynow.setEnabled(false);
                txt_buynow.setText(activity.getString(R.string.out_of_stock));
                txt_buynow.setBackgroundResource(R.drawable.rounded_graycolor);

                txt_color.setTextColor(activity.getResources().getColor(R.color.black_gray));
                lay_color.setBackgroundResource(R.drawable.rounded_size);
                img_color.setVisibility(View.VISIBLE);
                img_color1.setVisibility(View.GONE);
            }
        }

        txt_product_prise.setText(sPref.getString("CUR", "") + " " + colorItem.getColorPrice());
    }


    //Dialog NewProductSize
    public Dialog buildDialogNewSizeColor(final ProductSizeCallbackMessage callback, NewProductItem productItem) {
        final Dialog dialog = buildDialogView(R.layout.dialog_size_color, 1);
        //((CustomTextView) dialog.findViewById(R.id.txt_product_prise)).setText(activity.getResources().getString(R.string.EUR) + " " + productItem.getProductPrice());

        ((ImageButton) dialog.findViewById(R.id.image_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancel(dialog);
            }
        });

        layout_main = (LinearLayout) dialog.findViewById(R.id.layout_main);

        txt_product_prise = (CustomTextView) dialog.findViewById(R.id.txt_product_prise);
        txt_buynow = (CustomTextView) dialog.findViewById(R.id.txt_buynow);

        img_product = (ImageView) dialog.findViewById(R.id.img_product);
        final TabLayout tabLayout_addsize = (TabLayout) dialog.findViewById(R.id.tabLayout_addsize);
        final TabLayout tabLayout_addcolor = (TabLayout) dialog.findViewById(R.id.tabLayout_addcolor);

        txt_buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos_size = tabLayout_addsize.getSelectedTabPosition();
                int pos_color = tabLayout_addcolor.getSelectedTabPosition();

                com.appsolace.estore.Model.HomeData.ProductSizeItem productSizeItem = new_productSize.get(pos_size);

                com.appsolace.estore.Model.HomeData.ColorItem colorItem = new_color.get(pos_color);

                callback.onSuccess(dialog,productSizeItem.getSizeId(),colorItem.getColorCode());
            }
        });


        new_productSize = productItem.getProductSize();

        if (new_productSize != null && new_productSize.size() > 0) {

            for (int i = 0; i < new_productSize.size(); i++) {

                final com.appsolace.estore.Model.HomeData.ProductSizeItem productSizeItem = new_productSize.get(i);

                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.row_tabsize, layout_main, false);

                CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
                LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);

                tabOne.setText(productSizeItem.getSizeName());
                lay_size.setBackgroundResource(R.drawable.rounded_size);
                //tabLayout_addsize.getTabAt(Integer.parseInt(productSizeItem.getSizeId())).setCustomView(view);

                tabLayout_addsize.addTab(tabLayout_addsize.newTab().setCustomView(view));


            }

            TabLayout.Tab tab = tabLayout_addsize.getTabAt(0);
            tab.select();
            NewselectSizeTab(0, tabLayout_addsize, tabLayout_addcolor);

            tabLayout_addsize.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    //tabLayout_addcolor.removeAllTabs();
                    int pos = tab.getPosition();
                    NewselectSizeTab(pos, tabLayout_addsize, tabLayout_addcolor);

                    /*View view = tab.getCustomView();
                    CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
                    LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);
                    tabOne.setTextColor(activity.getResources().getColor(R.color.white));
                    lay_size.setBackgroundResource(R.drawable.rounded_sizefill);

                    ProductSizeItem productSizeItem = productSize.get(pos);
                    setColorData(productSizeItem.getColor());*/


                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                    View view = tab.getCustomView();
                    CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
                    LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);

                    tabOne.setTextColor(activity.getResources().getColor(R.color.black_gray));
                    lay_size.setBackgroundResource(R.drawable.rounded_size);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }


        return dialog;
    }

    public void NewselectSizeTab(int pos, TabLayout tabLayout_addsize, TabLayout tabLayout_addcolor) {
        TabLayout.Tab tab = tabLayout_addsize.getTabAt(pos);
        View view = tab.getCustomView();
        CustomTextView tabOne = (CustomTextView) view.findViewById(R.id.txt_size);
        LinearLayout lay_size = (LinearLayout) view.findViewById(R.id.lay_size);
        tabOne.setTextColor(activity.getResources().getColor(R.color.white));
        lay_size.setBackgroundResource(R.drawable.rounded_sizefill);

        com.appsolace.estore.Model.HomeData.ProductSizeItem productSizeItem = new_productSize.get(pos);
        new_color = productSizeItem.getColor();
        NewsetColorData(tabLayout_addcolor);

    }

    public void NewsetColorData(final TabLayout tabLayout_addcolor) {
        tabLayout_addcolor.removeAllTabs();
        if (new_color != null && new_color.size() > 0) {

            for (int i = 0; i < new_color.size(); i++) {

                com.appsolace.estore.Model.HomeData.ColorItem colorItem = new_color.get(i);

                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.row_tabcolor, layout_main, false);

                CustomTextView txt_color = (CustomTextView) view.findViewById(R.id.txt_color);
                LinearLayout lay_color = (LinearLayout) view.findViewById(R.id.lay_color);
                ImageView img_color = (ImageView) view.findViewById(R.id.img_color);
                ImageView img_color1 = (ImageView) view.findViewById(R.id.img_color1);

                txt_color.setText(Utils.Getcolorname(colorItem.getColorCode()));
                if (!colorItem.getColorCode().equals("") && !colorItem.getColorCode().equalsIgnoreCase("null")) {
                    img_color.setBackgroundColor(Color.parseColor(colorItem.getColorCode()));
                    img_color1.setBackgroundColor(Color.parseColor(colorItem.getColorCode()));
                }

                lay_color.setBackgroundResource(R.drawable.rounded_size);

                //tabLayout_addcolor.getTabAt(Integer.parseInt(colorItem.getColorId())).setCustomView(view);
                tabLayout_addcolor.addTab(tabLayout_addcolor.newTab().setCustomView(view));

            }

            com.appsolace.estore.Model.HomeData.ColorItem colorItem = new_color.get(0);
            NewselectColorTab(0, tabLayout_addcolor, colorItem);

            tabLayout_addcolor.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    int pos = tab.getPosition();
                    com.appsolace.estore.Model.HomeData.ColorItem colorItem = new_color.get(pos);
                    NewselectColorTab(pos, tabLayout_addcolor, colorItem);

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                    View view = tab.getCustomView();

                    CustomTextView txt_color = (CustomTextView) view.findViewById(R.id.txt_color);
                    LinearLayout lay_color = (LinearLayout) view.findViewById(R.id.lay_color);
                    ImageView img_color = (ImageView) view.findViewById(R.id.img_color);
                    ImageView img_color1 = (ImageView) view.findViewById(R.id.img_color1);

                    txt_color.setTextColor(activity.getResources().getColor(R.color.black_gray));
                    lay_color.setBackgroundResource(R.drawable.rounded_size);
                    img_color.setVisibility(View.VISIBLE);
                    img_color1.setVisibility(View.GONE);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        }
    }

    public void NewselectColorTab(int pos, TabLayout tabLayout_addcolor, com.appsolace.estore.Model.HomeData.ColorItem colorItem) {
        TabLayout.Tab tab = tabLayout_addcolor.getTabAt(pos);
        View view = tab.getCustomView();

        CustomTextView txt_color = (CustomTextView) view.findViewById(R.id.txt_color);
        LinearLayout lay_color = (LinearLayout) view.findViewById(R.id.lay_color);
        ImageView img_color = (ImageView) view.findViewById(R.id.img_color);
        ImageView img_color1 = (ImageView) view.findViewById(R.id.img_color1);

        String temp = Url.product_size_url + colorItem.getColorImage();

        boolean isWhitespace = Utils.containsWhitespace(temp);

        if (isWhitespace)
            temp = temp.replaceAll(" ", "%20");

        Picasso.with(activity).load(temp).into(img_product);

        if (!colorItem.getColorStock().equals("")) {
            int stock = Integer.parseInt(colorItem.getColorStock());

            if (stock > 0) {
                txt_buynow.setEnabled(true);
                txt_buynow.setText(activity.getString(R.string.buy_now));
                txt_buynow.setBackgroundResource(R.drawable.rounded_corner_red);

                txt_color.setTextColor(activity.getResources().getColor(R.color.white));
                lay_color.setBackgroundResource(R.drawable.rounded_sizefill);
                img_color1.setVisibility(View.VISIBLE);
                img_color.setVisibility(View.GONE);

            } else {
                txt_buynow.setEnabled(false);
                txt_buynow.setText(activity.getString(R.string.out_of_stock));
                txt_buynow.setBackgroundResource(R.drawable.rounded_graycolor);

                txt_color.setTextColor(activity.getResources().getColor(R.color.black_gray));
                lay_color.setBackgroundResource(R.drawable.rounded_size);
                img_color.setVisibility(View.VISIBLE);
                img_color1.setVisibility(View.GONE);
            }
        }

        txt_product_prise.setText(sPref.getString("CUR", "") + " " + colorItem.getColorPrice());
    }


    //Dialog Msg
    public Dialog buildDialogProductSort(final CallbackMessage callback) {
        final Dialog dialog = buildDialogView(R.layout.dialog_product_sort, 1);

        ((CustomTextView) dialog.findViewById(R.id.txt_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSuccess(dialog);
            }
        });
        ((ImageButton) dialog.findViewById(R.id.image_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancel(dialog);
                Common.product_sort_flag = "";
            }
        });

        final RadioGroup radiogroup = (RadioGroup) dialog.findViewById(R.id.radiogroup);


        final RadioButton radio1 = (RadioButton) dialog.findViewById(R.id.radio1);
        final RadioButton radio2 = (RadioButton) dialog.findViewById(R.id.radio2);
        final RadioButton radio3 = (RadioButton) dialog.findViewById(R.id.radio3);

        if (Common.product_sort_flag.equals(activity.getResources().getString(R.string.popular))) {
            radio1.setChecked(true);
        } else if (Common.product_sort_flag.equals(activity.getResources().getString(R.string.high))) {
            radio2.setChecked(true);
        } else if (Common.product_sort_flag.equals(activity.getResources().getString(R.string.low))) {
            radio3.setChecked(true);
        }

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Common.product_sort_flag = "";
                // find which radio button is selected
                if (checkedId == R.id.radio1) {
                    Common.product_sort_flag = radio1.getText().toString().trim();
                } else if (checkedId == R.id.radio2) {
                    Common.product_sort_flag = radio2.getText().toString().trim();
                } else if (checkedId == R.id.radio3) {
                    Common.product_sort_flag = radio3.getText().toString().trim();
                }

            }
        });

        return dialog;
    }
}