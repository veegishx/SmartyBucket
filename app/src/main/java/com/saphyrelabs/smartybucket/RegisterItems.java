package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_items);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        initFirestore();

        // Define & initialize spinner for item categories
        Spinner spinner = (Spinner) findViewById(R.id.items_category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.item_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Defining buttons & event listeners
        Button addNewItem = (Button)findViewById(R.id.add_new_item);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if(!validateItemCategory() | !validateItemName()) {
                    return;
                } else {
                    writeItemsToFirestore();
                }
            }
        });

    }

    private void initFirestore() {
        smartyFirestore = FirebaseFirestore.getInstance();
    }

    private void writeItemsToFirestore() {
        // Get values from user input
        // itemCategory
        Spinner spinner = (Spinner)findViewById(R.id.items_category_spinner);
        String itemCategoryVal  = spinner.getSelectedItem().toString();

        // itemName
        EditText itemNameText = (EditText)findViewById(R.id.item_name_value);
        final String itemNameVal = itemNameText.getText().toString();

        // itemPrice
        EditText itemPriceText = (EditText)findViewById(R.id.item_price_value);
        double itemPriceVal = Double.parseDouble(itemPriceText.getText().toString());

        // itemId
        UUID uuid = UUID.randomUUID();
        String itemId = uuid.toString();

        // user
        String user = "Veegish Ramdani";

        Item newItem = new Item(user, itemId, itemNameVal, itemCategoryVal, itemPriceVal);

        smartyFirestore.collection("items").document(itemCategoryVal.toLowerCase() + "-item-" + itemId).set(newItem)
                .addOnSuccessListener(new OnSuccessListener< Void >() {
                    public void onSuccess(Void aVoid) {
                        View contextView = findViewById(R.id.coordinatorLayout);
                        Snackbar.make(contextView, "Added " + itemNameVal + " to bucket list!", Snackbar.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception e) {
                View contextView = findViewById(R.id.coordinatorLayout);
                Snackbar.make(contextView, "ERROR: " + e.toString(), Snackbar.LENGTH_LONG).show();
                Log.d("TAG", e.toString());
            }
        });
    }

    private boolean validateItemName() {
        EditText itemNameText = (EditText)findViewById(R.id.item_name_value);
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
        EditText itemPriceText = (EditText)findViewById(R.id.item_price_value);
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
