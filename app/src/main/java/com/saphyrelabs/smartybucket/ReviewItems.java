package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
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
    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_items);

        searchRecipes = findViewById(R.id.searchRecipes);
        ingredientTitle = findViewById(R.id.ingredientTitle);
        ingredients_layout = findViewById(R.id.ingredients_layout);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        reviewedIngredients = (ArrayList<Ingredient>) args.getSerializable("ingredients");

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

        // Initialize BottomAppBar
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        // Handle onClick event
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.homeNav:
                        Intent home = new Intent(ReviewItems.this, MainActivity.class);
                        startActivity(home);
                        break;
                    case R.id.scanNav:
                        Intent scanType = new Intent(ReviewItems.this, ScanType.class);
                        startActivity(scanType);
                        break;
                    case R.id.expense:
                        Intent expenses = new Intent(ReviewItems.this, ViewExpenses.class);
                        startActivity(expenses);
                        break;
                    case R.id.account:
                        Intent account = new Intent(ReviewItems.this, UserProfile.class);
                        startActivity(account);
                }
                return false;
            }
        });

    }
}
