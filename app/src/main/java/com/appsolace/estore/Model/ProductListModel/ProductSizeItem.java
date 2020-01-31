package com.appsolace.estore.Model.ProductListModel;

import java.io.Serializable;
import java.util.List;

public class ProductSizeItem implements Serializable {
	private String sizeName;
	private String sizeColor;
	private String sizeStock;
	private List<ColorItem> color;
	private String sizeImage;
	private String sizeId;
	private String sizePrice;

	public void setSizeName(String sizeName){
		this.sizeName = sizeName;
	}

	public String getSizeName(){
		return sizeName;
	}

	public void setSizeColor(String sizeColor){
		this.sizeColor = sizeColor;
	}

	public String getSizeColor(){
		return sizeColor;
	}

	public void setSizeStock(String sizeStock){
		this.sizeStock = sizeStock;
	}

	public String getSizeStock(){
		return sizeStock;
	}

	public void setColor(List<ColorItem> color){
		this.color = color;
	}

	public List<ColorItem> getColor(){
		return color;
	}

	public void setSizeImage(String sizeImage){
		this.sizeImage = sizeImage;
	}

	public String getSizeImage(){
		return sizeImage;
	}

	public void setSizeId(String sizeId){
		this.sizeId = sizeId;
	}

	public String getSizeId(){
		return sizeId;
	}

	public void setSizePrice(String sizePrice){
		this.sizePrice = sizePrice;
	}

	public String getSizePrice(){
		return sizePrice;
	}

	@Override
 	public String toString(){
		return 
			"ProductSizeItem{" + 
			"size_name = '" + sizeName + '\'' + 
			",size_color = '" + sizeColor + '\'' + 
			",size_stock = '" + sizeStock + '\'' + 
			",color = '" + color + '\'' + 
			",size_image = '" + sizeImage + '\'' + 
			",size_id = '" + sizeId + '\'' + 
			",size_price = '" + sizePrice + '\'' + 
			"}";
		}
}