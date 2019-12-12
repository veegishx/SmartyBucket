package com.saphyrelabs.smartybucket;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saphyrelabs.smartybucket.adapter.RecipeAdapter;
import com.saphyrelabs.smartybucket.api.RecipeApiClient;
import com.saphyrelabs.smartybucket.api.RecipeApiInterface;
import com.saphyrelabs.smartybucket.model.Recipe;
import com.saphyrelabs.smartybucket.model.RecipeList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DisplayRecipes extends AppCompatActivity {
    private static final String apiUrl = "http://www.recipepuppy.com/api/?i=";
    String apiParameters = "";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Recipe> recipes = new ArrayList<>();
    private RecipeAdapter recipeAdapter;
    private String TAG = DisplayRecipes.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipes);
        Intent intent = getIntent();
        ArrayList<String> ingredients = intent.getStringArrayListExtra("ingredients");

        for (int i = 0; i < ingredients.size(); i++) {
            apiParameters = apiParameters + "," + ingredients.get(i);
        }

        System.out.println(apiParameters);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(DisplayRecipes.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        System.out.println("REEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
        loadJson();
    }

    public void loadJson() {
        RecipeApiInterface apiInterface = RecipeApiClient.getRecipeApi().create(RecipeApiInterface.class);
        Call<RecipeList> call;
        call = apiInterface.getRecipes(apiParameters);

        call.enqueue(new Callback<RecipeList>() {
            @Override
            public void onResponse(Call<RecipeList> call, Response<RecipeList> response) {
                if (response.isSuccessful()) {
                    System.out.println("SUUUUUUUUUUUUUUUUUUUUUUUCCCCCCCCCCCCCCCCCCCCCCCC");
                    System.out.println(response.body().getResults());
                    if (!recipes.isEmpty()) {
                        recipes.clear();
                    }

                    recipes = response.body().getResults();
                    recipeAdapter = new RecipeAdapter(recipes, DisplayRecipes.this);
                    recyclerView.setAdapter(recipeAdapter);
                    recipeAdapter.notifyDataSetChanged();
                    Toast.makeText(DisplayRecipes.this, "Recipes Fetched", Toast.LENGTH_LONG);
                } else {
                    System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
                    System.out.println(response);
                    Toast.makeText(DisplayRecipes.this, "No Result", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<RecipeList> call, Throwable t) {
                System.out.println("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRREREREREREREREERE");
                t.printStackTrace();
            }
        });
    }
}
