package com.saphyrelabs.smartybucket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.saphyrelabs.smartybucket.adapter.ReviewIngredientAdapter;

import java.util.ArrayList;

public class ReviewListImageItems extends AppCompatActivity {
    private RecyclerView listItemsRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list_image_items);

        Intent intent = getIntent();
        ArrayList<String> ingredients = intent.getStringArrayListExtra("ingredients");

        System.out.println("LIST DEBUG");
        for (int i = 0; i < ingredients.size(); i++) {
            System.out.println("DEBUG ITEM(" + i  + "): " + ingredients.get(i));
        }

        listItemsRecyclerView = (RecyclerView) findViewById(R.id.listItemsRecyclerView);
        listItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItemsRecyclerView.setAdapter(new ReviewIngredientAdapter(ingredients, R.layout.ingredient, getApplicationContext()));

    }
}
