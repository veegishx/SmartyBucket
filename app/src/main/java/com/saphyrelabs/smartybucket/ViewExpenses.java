package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saphyrelabs.smartybucket.adapter.MealExpensesAdapter;
import com.saphyrelabs.smartybucket.model.Meal;
import com.saphyrelabs.smartybucket.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ViewExpenses extends AppCompatActivity {
    private FirebaseFirestore smartyFirestore;
    private static final String TAG = "ViewExpenses";
    private void initFirestore() {
        smartyFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);

        initFirestore();

        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        String userId = userConfigurations.getString("facebookUid","0");

        final RecyclerView expensesView = (RecyclerView) findViewById(R.id.expenseRecyclerView);


//        expensesView.setLayoutManager(new LinearLayoutManager(this));
//        MealExpensesAdapter mealExpensesAdapter = new MealExpensesAdapter(meals, R.layout.expense, getApplicationContext());
//        expensesView.setAdapter(mealExpensesAdapter);

        DocumentReference docRef = smartyFirestore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    User user = document.toObject(User.class);

                    ArrayList<Meal> meals = user.getMeals();
                    expensesView.setLayoutManager(new LinearLayoutManager(ViewExpenses.this.getApplicationContext()));
                    MealExpensesAdapter mealExpensesAdapter = new MealExpensesAdapter(meals, R.layout.expense, ViewExpenses.this.getApplicationContext());
                    expensesView.setAdapter(mealExpensesAdapter);

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
