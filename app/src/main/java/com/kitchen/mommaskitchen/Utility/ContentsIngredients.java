package com.kitchen.mommaskitchen.Utility;

public class ContentsIngredients {

    String ingredient_name;
    float ingredient_quantity;
    String unit;

    public ContentsIngredients(String ingredient_name, float ingredient_quantity, String unit) {
        this.ingredient_name = ingredient_name;
        this.ingredient_quantity = ingredient_quantity;
        this.unit = unit;
    }

    public String getIngredient_name() {
        return ingredient_name;
    }

    public void setIngredient_name(String ingredient_name) {
        this.ingredient_name = ingredient_name;
    }

    public float getIngredient_quantity() {
        return ingredient_quantity;
    }

    public void setIngredient_quantity(float ingredient_quantity) {
        this.ingredient_quantity = ingredient_quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
