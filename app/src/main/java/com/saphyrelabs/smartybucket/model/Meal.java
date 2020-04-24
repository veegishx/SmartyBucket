package com.saphyrelabs.smartybucket.model;

import java.util.List;


/**
 * Data Model for Meal
 * This data model contains all the properties and getters/setters to allow this application to store and retrieve meals chosen by a user.
 */

public class Meal {
    private String mealName;
    private double mealPrice;
    private List<String> ingredientLines;

    public Meal(String mealName, double mealPrice, List<String> ingredientLines) {
        this.mealName = mealName;
        this.mealPrice = mealPrice;
        this.ingredientLines = ingredientLines;
    }

    public Meal() {}

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public double getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(double mealPrice) {
        this.mealPrice = mealPrice;
    }

    public List<String> getIngredientLines() {
        return ingredientLines;
    }

    public void setIngredientLines(List<String> ingredientLines) {
        this.ingredientLines = ingredientLines;
    }
}

