package com.appsolace.estore.Model.HomeData;

import com.appsolace.estore.Model.RecentlyProduct.RecentlyProductItem;

import java.io.Serializable;
import java.util.List;

public class HomeData implements Serializable{
	private List<CategoryDataItem> categoryData;
	private List<HomeSliderImageItem> homeSliderImage;
	private List<RecentlyProductItem> recentProduct;
	private List<NewProductItem> newProduct;

	public void setCategoryData(List<CategoryDataItem> categoryData){
		this.categoryData = categoryData;
	}

	public List<CategoryDataItem> getCategoryData(){
		return categoryData;
	}

	public void setHomeSliderImage(List<HomeSliderImageItem> homeSliderImage){
		this.homeSliderImage = homeSliderImage;
	}

	public List<HomeSliderImageItem> getHomeSliderImage(){
		return homeSliderImage;
	}

	public void setRecentProduct(List<RecentlyProductItem> recentProduct){
		this.recentProduct = recentProduct;
	}

	public List<RecentlyProductItem> getRecentProduct(){
		return recentProduct;
	}

	public void setNewProduct(List<NewProductItem> newProduct){
		this.newProduct = newProduct;
	}

	public List<NewProductItem> getNewProduct(){
		return newProduct;
	}


}