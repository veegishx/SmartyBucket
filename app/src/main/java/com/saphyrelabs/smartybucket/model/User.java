package com.saphyrelabs.smartybucket.model;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Model for User
 * This data model contains all the properties and getters/setters to allow this application to store and retrieve user data.
 */

// @IgnoreExtraProperties prevents data from being serialized and sent to FireStore when using the @Exclude annotation.
@IgnoreExtraProperties
public class User {
    private String userId;
    private String userName;
    private String userEmail;
    private HashMap<String, Boolean> userMealPreferences;
    private String userMealPreferencesString;
    private float budget;
    private Map<String, String> expenses;
    private ArrayList<Meal> meals;

    public User(String userId, String userName, String userEmail, HashMap<String, Boolean> userMealPreferences, float budget, Map<String, String> expenses) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userMealPreferences = userMealPreferences;
        this.budget = budget;
        this.expenses = expenses;
    }

    public User(String userId, String userName, String userEmail, String userMealPreferencesString) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userMealPreferencesString = userMealPreferencesString;
    }

    public User(String userId, String userName, String userEmail, HashMap<String, Boolean> userMealPreferences, float budget) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userMealPreferences = userMealPreferences;
        this.budget = budget;
    }

    public User(String userId, Map<String, String> expenses) {
        this.userId = userId;
        this.expenses = expenses;
    }

    public User() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public HashMap<String, Boolean> getUserMealPreferences() {
        return userMealPreferences;
    }

    public void setUserMealPreferences(HashMap<String, Boolean> userMealPrefereces) {
        this.userMealPreferences = userMealPrefereces;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public Map<String, String> getExpenses() {
        return expenses;
    }

    public void setExpenses(Map<String, String> expenses) {
        this.expenses = expenses;
    }

    public ArrayList<Meal> getMeals() {
        return meals;
    }

    public void setMeals(ArrayList<Meal> meals) {
        this.meals = meals;
    }

    public void addMeals(Meal meal) {
        meals.add(meal);
    }
}
