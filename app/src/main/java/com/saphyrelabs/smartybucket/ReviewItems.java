package com.saphyrelabs.smartybucket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.saphyrelabs.smartybucket.adapter.ReviewIngredientAdapter;
import com.saphyrelabs.smartybucket.model.Ingredient;

import java.io.Serializable;
import java.util.ArrayList;

public class ReviewItems extends AppCompatActivity {
    private RecyclerView listItemsRecyclerView;
    private Button searchRecipes;
    private ArrayList<Ingredient> reviewedIngredients;
    private EditText ingredientTitle;
    private FrameLayout ingredients_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_items);

        searchRecipes = findViewById(R.id.searchRecipes);
        ingredientTitle = findViewById(R.id.ingredientTitle);
        ingredients_layout = findViewById(R.id.ingredients_layout);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        reviewedIngredients= (ArrayList<Ingredient>) args.getSerializable("ingredients");

        System.out.println("LIST DEBUG");
        for (int i = 0; i < reviewedIngredients.size(); i++) {
            System.out.println("DEBUG ITEM(" + i  + "): " + reviewedIngredients.get(i));
        }

        listItemsRecyclerView = (RecyclerView) findViewById(R.id.listItemsRecyclerView);
        listItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ReviewIngredientAdapter reviewIngredientAdapter = new ReviewIngredientAdapter(reviewedIngredients, R.layout.ingredient, getApplicationContext());
        listItemsRecyclerView.setAdapter(reviewIngredientAdapter);

        searchRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayRecipeActivity = new Intent(ReviewItems.this, DisplayRecipes.class);
                Bundle args = new Bundle();
                args.putSerializable("ingredients",(Serializable) reviewIngredientAdapter.getIngredients());
                displayRecipeActivity.putExtra("BUNDLE",args);
                startActivity(displayRecipeActivity);
            }
        });

    }
}
