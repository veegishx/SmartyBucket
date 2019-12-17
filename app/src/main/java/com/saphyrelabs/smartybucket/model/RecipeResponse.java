package com.saphyrelabs.smartybucket.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeResponse {
    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("version")
    @Expose
    private double version;

    @SerializedName("href")
    @Expose
    private String href;

    @SerializedName("results")
    @Expose
    private List<Recipe> results;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<Recipe> getResults() {
        return results;
    }

    public void setResults(List<Recipe> results) {
        this.results = results;
    }
}
