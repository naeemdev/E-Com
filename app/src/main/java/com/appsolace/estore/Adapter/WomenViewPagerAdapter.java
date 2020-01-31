package com.appsolace.estore.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.appsolace.estore.Model.Menu2.Menu2SliderItem;
import com.appsolace.estore.R;
import com.appsolace.estore.Utili.Url;
import com.appsolace.estore.Utili.Utils;

import java.util.List;


public class WomenViewPagerAdapter extends PagerAdapter {

    Context context;
    String Image_path;
    List<Menu2SliderItem> banner;
    LayoutInflater inflater;


    public WomenViewPagerAdapter(Context context, List<Menu2SliderItem> banner) {
        this.context = context;
        this.banner = banner;
    }

    @Override
    public int getCount() {
       return banner.size();
        //return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.custom_viewpagergellery, container, false);


        // Locate the ImageView in viewpager_item.xml
        final ImageView pager_image = (ImageView) itemView.findViewById(R.id.pager_image);

        Menu2SliderItem imageItem = banner.get(position);

        String temp = "" + Url.slider_url + imageItem.getImage();
        boolean isWhitespace = Utils.containsWhitespace(temp);
        if (isWhitespace)
            temp = temp.replaceAll(" ", "%20");

        if (!temp.equalsIgnoreCase("null") && !temp.equals("")) {
            Picasso.with(context).load(temp).into(pager_image);
        }

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);

    }

}