package com.appsolace.estore.Model.Menu3;

import java.util.List;

public class Menu3Data{
	private List<Menu3SliderItem> menu3Slider;
	private List<Menu3CategoryItem> menu3Category;

	public void setMenu3Slider(List<Menu3SliderItem> menu3Slider){
		this.menu3Slider = menu3Slider;
	}

	public List<Menu3SliderItem> getMenu3Slider(){
		return menu3Slider;
	}

	public void setMenu3Category(List<Menu3CategoryItem> menu3Category){
		this.menu3Category = menu3Category;
	}

	public List<Menu3CategoryItem> getMenu3Category(){
		return menu3Category;
	}

}