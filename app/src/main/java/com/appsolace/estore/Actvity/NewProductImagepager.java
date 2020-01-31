package com.appsolace.estore.Actvity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.appsolace.estore.R;
import com.squareup.picasso.Picasso;
import com.appsolace.estore.Model.HomeData.NewProductItem;
import com.appsolace.estore.Utili.TouchImageView;
import com.appsolace.estore.Utili.Url;
import com.appsolace.estore.Utili.Utils;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewProductImagepager extends AppCompatActivity {

    private NewProductItem productItem;

    //Shared Preferences
    private SharedPreferences sPref;

    @BindView(R.id.product_pager)
    ViewPager product_pager;

    @BindView(R.id.product_indicator)
    CirclePageIndicator product_indicator;

    @BindView(R.id.ly_back)
    RelativeLayout ly_back;

    int position1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_imagepager);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        productItem = (NewProductItem) intent.getSerializableExtra("NewProductItem");
        position1 = intent.getExtras().getInt("position");

        sPref = PreferenceManager.getDefaultSharedPreferences(NewProductImagepager.this);

        initdata();
    }

    private void initdata()
    {
        ly_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ProductViewPager mCustomPagerAdapter = new ProductViewPager(NewProductImagepager.this, productItem.getProductImage());
        product_pager.setAdapter(mCustomPagerAdapter);
        product_indicator.setViewPager(product_pager);
        product_pager.setCurrentItem(position1);
    }

    public class ProductViewPager extends PagerAdapter {

        Context context;
        String Image_path;
        List<com.appsolace.estore.Model.HomeData.ProductImageItem> banner;
        LayoutInflater inflater;

        public ProductViewPager(Context context, List<com.appsolace.estore.Model.HomeData.ProductImageItem> banner) {
            this.context = context;
            this.banner = banner;
        }

        @Override
        public int getCount() {
            return banner.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.row_image_viewpager, container, false);


            // Locate the ImageView in viewpager_item.xml
            final TouchImageView pager_image = (TouchImageView) itemView.findViewById(R.id.pager_image);

            com.appsolace.estore.Model.HomeData.ProductImageItem imageItem = banner.get(position);

            String temp = "" + Url.product_url + imageItem.getImage();
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
}
