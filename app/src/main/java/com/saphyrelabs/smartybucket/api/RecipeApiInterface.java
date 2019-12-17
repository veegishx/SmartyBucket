package com.saphyrelabs.smartybucket.api;

import com.saphyrelabs.smartybucket.model.RecipeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApiInterface {
    @GET("api")
    Call<RecipeResponse> getRecipes(
            @Query("i") String ingredients
    );
}
