package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.saphyrelabs.smartybucket.model.Item;

import java.util.UUID;

public class RegisterItems extends AppCompatActivity {
    private static final String TAG = "";
    private FirebaseFirestore smartyFirestore;
    private Query readItemsQuery;
    private static final int LIMIT = 10;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_items);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        initFirestore();

        // Define & initialize spinner for item categories
        Spinner spinner = findViewById(R.id.items_category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.item_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Defining buttons & event listeners
        Button addNewItem = findViewById(R.id.add_new_item);
        addNewItem.setOnClickListener(V -> {
            if(!validateItemCategory() | !validateItemName()) {
                return;
            } else {
                writeItemsToFirestore();
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
                        Intent home = new Intent(RegisterItems.this, MainActivity.class);
                        startActivity(home);
                        break;
                    case R.id.scanNav:
                        Intent scanType = new Intent(RegisterItems.this, ScanType.class);
                        startActivity(scanType);
                        break;
                }
                return false;
            }
        });
    }

    private void initFirestore() {
        smartyFirestore = FirebaseFirestore.getInstance();
    }

    private void writeItemsToFirestore() {
        // Get values from user input
        // itemCategory
        Spinner spinner = findViewById(R.id.items_category_spinner);
        String itemCategoryVal  = spinner.getSelectedItem().toString();

        // itemName
        EditText itemNameText = findViewById(R.id.item_name_value);
        final String itemNameVal = itemNameText.getText().toString();

        // itemPrice
        EditText itemPriceText = findViewById(R.id.item_price_value);
        double itemPriceVal = Double.parseDouble(itemPriceText.getText().toString());

        // itemId
        UUID uuid = UUID.randomUUID();
        String itemId = uuid.toString();

        // user
        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        String userId = userConfigurations.getString("userUid","0");

        Item newItem = new Item(userId, itemId, itemNameVal, itemCategoryVal, itemPriceVal);

        smartyFirestore.collection("items").document(itemNameVal.toLowerCase().replaceAll("//s+", "")).set(newItem)
                .addOnSuccessListener(aVoid -> {
                    View contextView = findViewById(R.id.frameLayout);
                    Snackbar.make(contextView, "Added " + itemNameVal + " to bucket list!", Snackbar.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    View contextView = findViewById(R.id.frameLayout);
                    Snackbar.make(contextView, "ERROR: " + e.toString(), Snackbar.LENGTH_LONG).show();
                    Log.d("TAG", e.toString());
                });
    }

    private boolean validateItemName() {
        EditText itemNameText = findViewById(R.id.item_name_value);
        String itemNameVal = itemNameText.getText().toString().trim();

        if(itemNameVal.isEmpty()) {
            itemNameText.setError("Please enter item name.");
            return false;
        } else {
            itemNameText.setError(null);
            return true;
        }
    }

    private boolean validateItemCategory() {
        EditText itemPriceText = findViewById(R.id.item_price_value);
        String itemPriceVal = itemPriceText.getText().toString().trim();

        if(itemPriceVal.isEmpty()) {
            itemPriceText.setError("Please enter item name.");
            return false;
        } else {
            itemPriceText.setError(null);
            return true;
        }
    }
}
