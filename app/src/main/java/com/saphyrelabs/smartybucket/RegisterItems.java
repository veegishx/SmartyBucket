package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.*;
import com.saphyrelabs.smartybucket.adapter.ItemAdapter;
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
                writeItemsToFirestore();
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
        String itemNameVal = itemNameText.getText().toString();

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
                        Toast.makeText(RegisterItems.this, "New Item Added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterItems.this, "ERROR" + e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });
    }
}
