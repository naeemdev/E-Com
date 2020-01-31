package com.appsolace.estore.Model.HomeData;

public class CategoryDataItem{
	private String categoryImage;
	private String categoryName;
	private int flag;
	private String categoryId;
	private String categoryColor;

	public void setCategoryImage(String categoryImage){
		this.categoryImage = categoryImage;
	}

	public String getCategoryImage(){
		return categoryImage;
	}

	public void setCategoryName(String categoryName){
		this.categoryName = categoryName;
	}

	public String getCategoryName(){
		return categoryName;
	}

	public void setFlag(int flag){
		this.flag = flag;
	}

	public int getFlag(){
		return flag;
	}

	public void setCategoryId(String categoryId){
		this.categoryId = categoryId;
	}

	public String getCategoryId(){
		return categoryId;
	}

	public void setCategoryColor(String categoryColor){
		this.categoryColor = categoryColor;
	}

	public String getCategoryColor(){
		return categoryColor;
	}


}
