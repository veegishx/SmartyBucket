package com.saphyrelabs.smartybucket.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Recipe {
    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("ingredients")
    @Expose
    private String ingredients;

    Recipe() {}
    Recipe(String link, String thumbnail, String title, String ingredients) {
        this.link = link;
        this.thumbnail = thumbnail;
        this.title = title;
        this.ingredients = ingredients;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

}
