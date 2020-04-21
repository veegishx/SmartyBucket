package com.saphyrelabs.smartybucket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.saphyrelabs.smartybucket.adapter.RecipeAdapter;
import com.saphyrelabs.smartybucket.api.RecipeApiClient;
import com.saphyrelabs.smartybucket.api.RecipeApiInterface;
import com.saphyrelabs.smartybucket.model.Hits;
import com.saphyrelabs.smartybucket.model.Ingredient;
import com.saphyrelabs.smartybucket.model.Recipe;
import com.saphyrelabs.smartybucket.model.RecipeResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayRecipes extends AppCompatActivity {
    private static final String apiUrl = "https://api.edamam.com/search/";
    private static final String apiKeyParameter = BuildConfig.EDAMAM_API_KEY;
    private static final String appIdParameter = BuildConfig.EDAMAM_APP_ID;

    private static final String TAG = DisplayRecipes.class.getSimpleName();
    String ingredientsParameter = "";

    private RecipeAdapter recipeAdapter;
    private RecyclerView recyclerView;
    private List<Recipe> recipes;
    private TextView totalRecipes;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipes);
        totalRecipes = (TextView) findViewById(R.id.totalRecipes);

        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        String userId = userConfigurations.getString("facebookUid","0");

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<Ingredient> ingredients = (ArrayList<Ingredient>) args.getSerializable("ingredients");
        for (int i = 0; i < ingredients.size(); i++) {
            ingredientsParameter = ingredientsParameter + "," + ingredients.get(i).getIngredientName();
        }

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        System.out.println(ingredientsParameter);

        RecipeApiInterface apiInterface = RecipeApiClient.getRecipeApi().create(RecipeApiInterface.class);
        Call<RecipeResponse> call = apiInterface.getRecipes(ingredientsParameter, appIdParameter, apiKeyParameter);
        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse>call, Response<RecipeResponse> response) {
                System.out.println("-------------- EDAMAM API DEBUG INFO --------------");
                System.out.println(response.body().getHits());
                int statusCode = response.code();
                System.out.println(call.request().url());
                System.out.println(response.code());
                List<Hits> hits = response.body().getHits();

                List<Recipe> recipes = new ArrayList<>();
                for (int i = 0; i < hits.size(); i++) {
                    recipes.add(response.body().getHits().get(i).getRecipe());
                }

                String totalRecipesString = response.body().getCount() + " Recipes Found";
                totalRecipes.setText(totalRecipesString);
                recyclerView.setAdapter(new RecipeAdapter(userId, ingredientsParameter, recipes, R.layout.item, getApplicationContext()));
            }

            @Override
            public void onFailure(Call<RecipeResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });

        // Initialize BottomAppBar
        bottomNav = findViewById(R.id.bottom_navigation);

        // Handle onClick event
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.homeNav:
                        Intent home = new Intent(DisplayRecipes.this, MainActivity.class);
                        startActivity(home);
                        break;
                    case R.id.scanNav:
                        Intent scanType = new Intent(DisplayRecipes.this, ScanType.class);
                        startActivity(scanType);
                        break;
                }
                return false;
            }
        });

    }

//    public void loadJson() {
//        RecipeApiInterface apiInterface = RecipeApiClient.getRecipeApi().create(RecipeApiInterface.class);
//        Call<RecipeResponse> call;
//        call = apiInterface.getRecipes(ingredientsParameter);
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
