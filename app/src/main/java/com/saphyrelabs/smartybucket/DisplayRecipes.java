package com.saphyrelabs.smartybucket;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saphyrelabs.smartybucket.adapter.RecipeAdapter;
import com.saphyrelabs.smartybucket.api.RecipeApiClient;
import com.saphyrelabs.smartybucket.api.RecipeApiInterface;
import com.saphyrelabs.smartybucket.model.Recipe;
import com.saphyrelabs.smartybucket.model.RecipeResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayRecipes extends AppCompatActivity {
    private static final String apiUrl = "http://www.recipepuppy.com/api/";
    private static final String TAG = DisplayRecipes.class.getSimpleName();
    String apiParameters = "";

    private RecipeAdapter recipeAdapter;
    private RecyclerView recyclerView;
    private List<Recipe> recipes;
    private TextView totalRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipes);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        totalRecipes = (TextView) findViewById(R.id.totalRecipes);

        Intent intent = getIntent();
        ArrayList<String> ingredients = intent.getStringArrayListExtra("ingredients");

        for (int i = 0; i < ingredients.size(); i++) {
            apiParameters = apiParameters + "," + ingredients.get(i);
        }

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        System.out.println(apiParameters);

        RecipeApiInterface apiInterface = RecipeApiClient.getRecipeApi().create(RecipeApiInterface.class);
        Call<RecipeResponse> call = apiInterface.getRecipes(apiParameters);
        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse>call, Response<RecipeResponse> response) {
                int statusCode = response.code();
                List<Recipe> recipes = response.body().getResults();
                String totalRecipesString = recipes.size() + " Recipes Found";
                totalRecipes.setText(totalRecipesString);
                recyclerView.setAdapter(new RecipeAdapter(recipes, R.layout.item, getApplicationContext()));
            }

            @Override
            public void onFailure(Call<RecipeResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });

    }

//    public void loadJson() {
//        RecipeApiInterface apiInterface = RecipeApiClient.getRecipeApi().create(RecipeApiInterface.class);
//        Call<RecipeResponse> call;
//        call = apiInterface.getRecipes(apiParameters);
//
//        call.enqueue(new Callback<RecipeResponse>() {
//            @Override
//            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
//                if (response.isSuccessful() && response.body().getResults() != null) {
//                    System.out.println("-------------- DEBUG: SUCCESSFUL API CALL WITH RESULTS --------------");
//                    System.out.println(response.body().getResults());
//
//                    List<Recipe> list= response.body().getResults();
//                    recipeAdapter.setData(list);
//
//                    recipes = response.body().getResults();
//                    RecipeAdapter recipeAdapter = new RecipeAdapter(DisplayRecipes.this);
//                    recyclerView.setAdapter(recipeAdapter);
//                    recipeAdapter.notifyDataSetChanged();
//                    Toast.makeText(DisplayRecipes.this, "Recipes Fetched", Toast.LENGTH_LONG);
//                } else {
//                    System.out.println("-------------- DEBUG: SUCCESSFUL API CALL WITH NO RESULT --------------");
//                    System.out.println(response);
//                    Toast.makeText(DisplayRecipes.this, "No Result", Toast.LENGTH_LONG);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RecipeResponse> call, Throwable t) {
//                System.out.println("-------------- DEBUG: FAILED API CALL --------------");
//                t.printStackTrace();
//            }
//        });
//    }
}
