package com.appsolace.estore.Model.Menu1;

import java.util.List;

public class Menu1Data{
	private List<Menu1SliderItem> menu1Slider;
	private List<Menu1CategoryItem> menu1Category;

	public void setMenu1Slider(List<Menu1SliderItem> menu1Slider){
		this.menu1Slider = menu1Slider;
	}

	public List<Menu1SliderItem> getMenu1Slider(){
		return menu1Slider;
	}

	public void setMenu1Category(List<Menu1CategoryItem> menu1Category){
		this.menu1Category = menu1Category;
	}

	public List<Menu1CategoryItem> getMenu1Category(){
		return menu1Category;
	}


}