package com.saphyrelabs.smartybucket.api;

import com.saphyrelabs.smartybucket.model.RecipeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApiInterface {
    @GET("search")
    Call<RecipeResponse> getRecipes(
            @Query("q") String ingredients,
            @Query("app_id") String apiKey,
            @Query("app_key") String appId
    );
}
