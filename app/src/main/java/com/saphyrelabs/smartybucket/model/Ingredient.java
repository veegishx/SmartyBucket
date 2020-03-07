package com.saphyrelabs.smartybucket.model;
/**
 * Data Model for Ingredients
 * This data model contains all the properties and getters/setters to allow this application to store and retrieve ingredients.
 */
public class Ingredient {
    private String ingredientName;
    private String ingredientQty;

    public Ingredient(String ingredientName, String ingredientQty) {
        this.ingredientName = ingredientName;
        this.ingredientQty = ingredientQty;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientQty() {
        return ingredientQty;
    }

    public void setIngredientQty(String ingredientQty) {
        this.ingredientQty = ingredientQty;
    }
}
