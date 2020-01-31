package com.appsolace.estore.Model.Menu2;

import java.util.List;

public class Menu2Data{
	private List<Menu2CategoryItem> menu2Category;
	private List<Menu2SliderItem> menu2Slider;

	public void setMenu2Category(List<Menu2CategoryItem> menu2Category){
		this.menu2Category = menu2Category;
	}

	public List<Menu2CategoryItem> getMenu2Category(){
		return menu2Category;
	}

	public void setMenu2Slider(List<Menu2SliderItem> menu2Slider){
		this.menu2Slider = menu2Slider;
	}

	public List<Menu2SliderItem> getMenu2Slider(){
		return menu2Slider;
	}

	}