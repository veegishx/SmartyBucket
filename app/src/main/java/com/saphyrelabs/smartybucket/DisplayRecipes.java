package com.saphyrelabs.smartybucket;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

import retrofit2.Retrofit;

public class DisplayRecipes extends AppCompatActivity {
    private static final String apiUrl = "http://www.recipepuppy.com/api/?i=";
    String apiParameters = "";
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
    }
}
