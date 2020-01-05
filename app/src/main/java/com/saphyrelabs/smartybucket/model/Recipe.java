package com.saphyrelabs.smartybucket.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe {
    @SerializedName("uri")
    @Expose
    private String uri;

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("source")
    @Expose
    private String source;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("shareAs")
    @Expose
    private String shareAs;

    @SerializedName("yield")
    @Expose
    private int yield;

    @SerializedName("dietLabels")
    @Expose
    private List<String> dietLabels;

    @SerializedName("healthLabels")
    @Expose
    private List<String> healthLabels;

    @SerializedName("cautions")
    @Expose
    private List<String> cautions;

    @SerializedName("ingredientLines")
    @Expose
    private List<String> incredientLines;

    @SerializedName("calories")
    @Expose
    private double calories;

    @SerializedName("totalWeight")
    @Expose
    private double totalWeight;

    @SerializedName("totalTime")
    @Expose
    private double totalTime;

    public Recipe() {}

    public Recipe(String uri, String label, String image, String source, String url, String shareAs, int yield, List<String> dietLabels, List<String> healthLabels, List<String> cautions, List<String> incredientLines, double calories, double totalWeight, double totalTime) {
        this.uri = uri;
        this.label = label;
        this.image = image;
        this.source = source;
        this.url = url;
        this.shareAs = shareAs;
        this.yield = yield;
        this.dietLabels = dietLabels;
        this.healthLabels = healthLabels;
        this.cautions = cautions;
        this.incredientLines = incredientLines;
        this.calories = calories;
        this.totalWeight = totalWeight;
        this.totalTime = totalTime;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShareAs() {
        return shareAs;
    }

    public void setShareAs(String shareAs) {
        this.shareAs = shareAs;
    }

    public int getYield() {
        return yield;
    }

    public void setYield(int yield) {
        this.yield = yield;
    }

    public List<String> getDietLabels() {
        return dietLabels;
    }

    public void setDietLabels(List<String> dietLabels) {
        this.dietLabels = dietLabels;
    }

    public List<String> getHealthLabels() {
        return healthLabels;
    }

    public void setHealthLabels(List<String> healthLabels) {
        this.healthLabels = healthLabels;
    }

    public List<String> getCautions() {
        return cautions;
    }

    public void setCautions(List<String> cautions) {
        this.cautions = cautions;
    }

    public List<String> getIncredientLines() {
        return incredientLines;
    }

    public void setIncredientLines(List<String> incredientLines) {
        this.incredientLines = incredientLines;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }
}
