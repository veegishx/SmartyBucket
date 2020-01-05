package com.saphyrelabs.smartybucket.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Hits {
    @SerializedName("recipe")
    @Expose
    private Recipe recipe;

    @SerializedName("bookmarked")
    @Expose
    private boolean bookmarked;

    @SerializedName("bought")
    @Expose
    private boolean bought;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public Hits() {}

    public Hits(Recipe recipe, boolean bookmarked, boolean bought) {
        this.recipe = recipe;
        this.bookmarked = bookmarked;
        this.bought = bought;
    }
}
