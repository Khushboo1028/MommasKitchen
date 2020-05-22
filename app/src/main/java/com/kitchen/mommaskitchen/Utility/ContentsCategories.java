package com.kitchen.mommaskitchen.Utility;

import java.io.Serializable;

public class ContentsCategories implements Serializable {

    String category_name;
    String category_image_url;
    String category_id;

    public ContentsCategories(String category_name, String category_image_url, String category_id) {
        this.category_name = category_name;
        this.category_image_url = category_image_url;
        this.category_id = category_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_image_url() {
        return category_image_url;
    }

    public void setCategory_image_url(String category_image_url) {
        this.category_image_url = category_image_url;
    }
}
