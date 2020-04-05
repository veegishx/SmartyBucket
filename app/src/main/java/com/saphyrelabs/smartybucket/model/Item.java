package com.saphyrelabs.smartybucket.model;
import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Data Model for Items
 * This data model contains all the properties and getters/setters to allow this application to store and retrieve user items.
 * The items are physical ingredients that a users usually buy and use in their kitchens.
 */

// @IgnoreExtraProperties prevents data from being serialized and sent to FireStore when using the @Exclude annotation.
@IgnoreExtraProperties
public class Item {
    private String addedBy;
    private String itemId;
    private String itemName;
    private String itemCategory;
    private String itemImageUrl;
    private double itemPrice;

    public Item(String addedBy, String itemId, String itemName, String itemCategory, String itemImageUrl, double itemPrice) {
        this.addedBy = addedBy;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemImageUrl = itemImageUrl;
        this.itemPrice = itemPrice;
    }

    public Item(String addedBy, String itemId, String itemName, String itemCategory, double itemPrice) {
        this.addedBy = addedBy;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemPrice = itemPrice;
    }

    public Item () {}

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }
}
