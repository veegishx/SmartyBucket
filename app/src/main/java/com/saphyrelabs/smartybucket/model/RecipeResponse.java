package com.saphyrelabs.smartybucket.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeResponse {
    @SerializedName("q")
    @Expose
    private String q;

    @SerializedName("from")
    @Expose
    private double from;

    @SerializedName("to")
    @Expose
    private String to;

    @SerializedName("more")
    @Expose
    private String more;

    @SerializedName("count")
    @Expose
    private String count;

    @SerializedName("hits")
    @Expose
    private List<Hits> hits;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public double getFrom() {
        return from;
    }

    public void setFrom(double from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMore() {
        return more;
    }

    public void setMore(String more) {
        this.more = more;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<Hits> getHits() {
        return hits;
    }

    public void setHits(List<Hits> hits) {
        this.hits = hits;
    }
}