package com.appsolace.estore.Model.RecentlyProduct;

import java.io.Serializable;

public class ColorItem implements Serializable {
	private String colorPrice;
	private String colorStock;
	private String colorId;
	private String colorImage;
	private String colorCode;

	public void setColorPrice(String colorPrice){
		this.colorPrice = colorPrice;
	}

	public String getColorPrice(){
		return colorPrice;
	}

	public void setColorStock(String colorStock){
		this.colorStock = colorStock;
	}

	public String getColorStock(){
		return colorStock;
	}

	public void setColorId(String colorId){
		this.colorId = colorId;
	}

	public String getColorId(){
		return colorId;
	}

	public void setColorImage(String colorImage){
		this.colorImage = colorImage;
	}

	public String getColorImage(){
		return colorImage;
	}

	public void setColorCode(String colorCode){
		this.colorCode = colorCode;
	}

	public String getColorCode(){
		return colorCode;
	}

}
