package com.appsolace.estore.Model.FilterProduct;

import java.util.List;


public class FilterData {

    private String minimum_price = "";
    private String high_price = "";

    private List<FilterBrand> filterBrandList;
    private List<FilterTag> filterTagList;
    private List<FilterSize> filterSizeList;
    private List<FilterColor> filterColorList;


    boolean isfilter=false;
    private String start_price="";
    private String end_price="";
    private String tag;
    private String size;
    private String color;
    private String brand;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStart_price() {
        return start_price;
    }

    public void setStart_price(String start_price) {
        this.start_price = start_price;
    }

    public String getEnd_price() {
        return end_price;
    }

    public void setEnd_price(String end_price) {
        this.end_price = end_price;
    }

    public boolean isIsfilter() {
        return isfilter;
    }

    public void setIsfilter(boolean isfilter) {
        this.isfilter = isfilter;
    }

    public String getMinimum_price() {
        return minimum_price;
    }

    public void setMinimum_price(String minimum_price) {
        this.minimum_price = minimum_price;
    }

    public String getHigh_price() {
        return high_price;
    }

    public void setHigh_price(String high_price) {
        this.high_price = high_price;
    }

    public List<FilterBrand> getFilterBrandList() {
        return filterBrandList;
    }

    public void setFilterBrandList(List<FilterBrand> filterBrandList) {
        this.filterBrandList = filterBrandList;
    }

    public List<FilterTag> getFilterTagList() {
        return filterTagList;
    }

    public void setFilterTagList(List<FilterTag> filterTagList) {
        this.filterTagList = filterTagList;
    }

    public List<FilterSize> getFilterSizeList() {
        return filterSizeList;
    }

    public void setFilterSizeList(List<FilterSize> filterSizeList) {
        this.filterSizeList = filterSizeList;
    }

    public List<FilterColor> getFilterColorList() {
        return filterColorList;
    }

    public void setFilterColorList(List<FilterColor> filterColorList) {
        this.filterColorList = filterColorList;
    }



}
