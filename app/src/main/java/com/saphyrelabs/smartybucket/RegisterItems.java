package com.saphyrelabs.smartybucket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.*;
import com.saphyrelabs.smartybucket.adapter.ItemAdapter;
import com.saphyrelabs.smartybucket.model.Item;

public class RegisterItems extends AppCompatActivity implements View.OnClickListener, ItemAdapter.onItemSelectedListener {
    private static final String TAG = "";
    private FirebaseFirestore smartyFirestore;
    private Query readItemsQuery;
    private ItemAdapter itmAdapter;
    private RecyclerView itemRecycler;
    private ViewGroup itemEmptyView;
    private static final int LIMIT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_items);

        itemRecycler = findViewById(R.id.itemRecycler);
        itemEmptyView = findViewById(R.id.view_empty);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        initFirestore();
        initRecyclerView();

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
                CollectionReference items = smartyFirestore.collection("item");

                for (int i = 0; i < 5; i++) {
                    Item newItem = new Item("Veegish", "1219385", "Farmstead Sausages", "Poultry", 30.96);
                    // Add a new document to the items collection
                    items.add(newItem);
                }
            }
        });

    }

    private void initFirestore() {
        smartyFirestore = FirebaseFirestore.getInstance();
        readItemsQuery = smartyFirestore.collection("item")
                .orderBy("itemCategory", Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (readItemsQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        itmAdapter = new ItemAdapter(readItemsQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    itemRecycler.setVisibility(View.GONE);
                    itemEmptyView.setVisibility(View.VISIBLE);
                } else {
                    itemRecycler.setVisibility(View.VISIBLE);
                    itemEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        itemRecycler.setLayoutManager(new LinearLayoutManager(this));
        itemRecycler.setAdapter(itmAdapter);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(DocumentSnapshot item) {

    }
}
