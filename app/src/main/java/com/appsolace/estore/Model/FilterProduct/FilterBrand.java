package com.appsolace.estore.Model.FilterProduct;



public class FilterBrand {

    private String brand_id;
    private String brand_name;
    private boolean brandselect;

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public boolean isBrandselect() {
        return brandselect;
    }

    public void setBrandselect(boolean brandselect) {
        this.brandselect = brandselect;
    }

}
