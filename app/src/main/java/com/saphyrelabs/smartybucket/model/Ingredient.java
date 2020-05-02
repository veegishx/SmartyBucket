package com.saphyrelabs.smartybucket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data Model for Ingredients
 * This data model contains all the properties and getters/setters to allow this application to store and retrieve ingredients.
 */
public class Ingredient implements Parcelable {
    private String ingredientName;

    public Ingredient(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    protected Ingredient(Parcel in) {
        ingredientName = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ingredientName);
    }

    @Override
    public String toString() {
        return getIngredientName();
    }

    @Override
    public boolean equals(Object obj) {
        return !super.equals(obj);
    }

    public int hashCode() {
        return getIngredientName().hashCode();
    }
}
