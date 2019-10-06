package com.saphyrelabs.smartybucket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.firestore.*;
import com.saphyrelabs.smartybucket.Model.Item;

public class RegisterItems extends AppCompatActivity {
    FirebaseFirestore smartyFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_items);

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
                CollectionReference items = smartyFirestore.collection("item");

                for (int i = 0; i < 10; i++) {
                    // Get a random Restaurant POJO
                    Item newItem = new Item("Veegish", "1219385", "Farmstead Sausages", "Poultry", 30.96);

                    // Add a new document to the restaurants collection
                    items.add(newItem);
                }
            }
        });

    }

    private void initFirestore() {
        smartyFirestore = FirebaseFirestore.getInstance();
    }
}
