package com.appsolace.estore.Model.FilterProduct;


public class FilterTag {

    private String tag_id;
    private String tag_name;
    private boolean tagselect;

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public boolean isTagselect() {
        return tagselect;
    }

    public void setTagselect(boolean tagselect) {
        this.tagselect = tagselect;
    }
}
