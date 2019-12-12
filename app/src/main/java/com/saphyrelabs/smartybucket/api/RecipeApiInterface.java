package com.saphyrelabs.smartybucket.api;

import com.saphyrelabs.smartybucket.model.Recipe;
import com.saphyrelabs.smartybucket.model.RecipeList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApiInterface {
    @GET("api")
    Call<RecipeList> getRecipes(
            @Query("i") String ingredients
    );
}
