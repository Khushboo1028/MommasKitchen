package com.kitchen.mommaskitchen.Utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class ContentsRecipe implements Serializable {

    String image_url;
    String recipe_name;
    String prep_time;
    Map <String, Map<String,Object>> ingredients;
    ArrayList<String> directions;
    String date_created;
    Map<String,Object> portion;
    String category_id;
    String document_id;

    public ContentsRecipe(String image_url, String recipe_name, String prep_time,Map<String, Map<String, Object>> ingredients, ArrayList<String> directions, String date_created, Map<String, Object> portion, String category_id,String document_id) {
        this.image_url = image_url;
        this.recipe_name = recipe_name;
        this.prep_time = prep_time;
        this.ingredients = ingredients;
        this.directions = directions;
        this.date_created = date_created;
        this.portion = portion;
        this.category_id = category_id;
        this.document_id = document_id;
    }

    public String getPrep_time() {
        return prep_time;
    }

    public void setPrep_time(String prep_time) {
        this.prep_time = prep_time;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    public Map<String, Map<String, Object>> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<String, Map<String, Object>> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getDirections() {
        return directions;
    }

    public void setDirections(ArrayList<String> directions) {
        this.directions = directions;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public Map<String, Object> getPortion() {
        return portion;
    }

    public void setPortion(Map<String, Object> portion) {
        this.portion = portion;
    }
}
