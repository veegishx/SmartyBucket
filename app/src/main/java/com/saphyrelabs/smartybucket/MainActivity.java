package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saphyrelabs.smartybucket.model.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SetBudget.SetBudgetListenerInterface, SetPreferences.SetPreferencesListenerInterface{
    BottomAppBar bab;
    Button test1;
    private BottomSheetDialog bottomSheetDialog;
    private TextView monthlyBudgetValue;
    private FirebaseFirestore smartyFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        initFirestore();

        // Retrieving persistent data
        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);

        test1 = findViewById(R.id.test1);
        monthlyBudgetValue = findViewById(R.id.monthlyBudgetValue);

        test1.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, ReviewListImageItems.class);
            startActivity(i);
        });

        String modelType = userConfigurations.getString("modelType",null);
        String facebookEmail = userConfigurations.getString("facebookEmail",null);
        String facebookUid = userConfigurations.getString("facebookUid",null);
        float budget = userConfigurations.getFloat("budget",0);
        boolean userMealPreferencesStatus = userConfigurations.getBoolean("userMealPreferencesStatus", false);

        Thread t1 = new Thread(() -> {
            if (modelType == null) {
                // Default User Settings
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("userConfigurations", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("modelType", "float");
                editor.apply();
            }


            if (budget == 0) {
                // New user is prompted to enter budget
                callSetBudgetPrompt();
            } else {
                String budgetString = Float.toString(budget);
                monthlyBudgetValue.setText(budgetString);
                System.out.println("Budget is set to: " + userConfigurations.getFloat("budget", 0));
            }

            if (!userMealPreferencesStatus) {
                callSetUserMealPreferencesPrompt();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println();
        System.out.println("---------------------- DEBUG INFO ----------------------");
        System.out.println("ModelType: " + modelType);
        System.out.println("FacebookEmail: " + facebookEmail);
        System.out.println("FacebookUserId: " + facebookUid);
        System.out.println("--------------------------------------------------------");

        RelativeLayout addItemBanner = findViewById(R.id.add_more_items_banner);
        addItemBanner.setOnClickListener(V -> {
            Intent registerItems = new Intent(MainActivity.this, RegisterItems.class);
            startActivity(registerItems);
        });


        // Initialize BottomAppBar
        bab = findViewById(R.id.bottom_app_bar);

        // Append menu items to BottomAppBar
        bab.replaceMenu(R.menu.menu_items);

        // Handle onClick event
        bab.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            switch (id){
                case R.id.app_bar_inventory:
                    Toast.makeText(MainActivity.this,"Inventory Clicked",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.app_bar_search:
                    Toast.makeText(MainActivity.this,"Search Clicked",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.app_bar_settings:
                    Intent myPreferencesIntent = new Intent(MainActivity.this, UserConfigurations.class);
                    startActivity(myPreferencesIntent);
                    break;
            }
            return false;
        });

        // Register click event for BottomAppBar drawer icon
        bab.setNavigationOnClickListener(v -> openNavigationMenu());

        // Register click event for BottomAppBar FloatingActionButton icon
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MainActivity.this, ScanType.class);
            startActivity(cameraIntent);
        });
    }

    private void openNavigationMenu() {

        //this will get the menu layout
        final View bottomAppBarDrawer = getLayoutInflater().inflate(R.layout.fragment_bottomsheet,null);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(bottomAppBarDrawer);
        bottomSheetDialog.show();

        //this will find NavigationView from id
        NavigationView navigationView = bottomAppBarDrawer.findViewById(R.id.fragment_bottomsheet);

        //This will handle the onClick Action for the menu item
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id){
                case R.id.nav1:
                    Intent profileIntent = new Intent(MainActivity.this, MyProfile.class);
                    startActivity(profileIntent);
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.nav2:
                    Toast.makeText(MainActivity.this,"Item 2 Clicked",Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.nav3:
                    Toast.makeText(MainActivity.this,"Item 3 Clicked",Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                    break;
            }
            return MainActivity.super.onOptionsItemSelected(menuItem);
        });
    }

    private void initFirestore() {
        smartyFirestore = FirebaseFirestore.getInstance();
    }

    public void callSetBudgetPrompt() {
        SetBudget budgetPrompt = new SetBudget();
        budgetPrompt.show(getSupportFragmentManager(), "Set Budget Dialog");
    }

    public void callSetUserMealPreferencesPrompt() {
        SetPreferences userMealPreferencesPrompt = new SetPreferences();
        userMealPreferencesPrompt.show(getSupportFragmentManager(), "Set User Meal UserConfigurations Dialog");
    }

    @Override
    public void setData(float userBudget) {
        // Code to set budget data in firestore
        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        SharedPreferences.Editor editor = userConfigurations.edit();
        editor.putFloat("budget", userBudget);
        editor.apply();
        String budgetString = Float.toString(userBudget);
        monthlyBudgetValue.setText(budgetString);
        System.out.println("Budget is set to: " + userConfigurations.getFloat("budget", 0));
    }

    @Override
    public void setData(HashMap<String, Boolean> userMealPreferences) {
        // Code to set user meal preferences in firestore
        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        SharedPreferences.Editor editor = userConfigurations.edit();
        editor.putBoolean("userMealPreferencesStatus", true);

        // Converting userMealPreferences into a JSON string to be stored in SharedPreferences
        // JSONObject jsonObject = new JSONObject(userMealPreferences);
        // String jsonString = jsonObject.toString();
        // editor.putString("userMealPreferences", jsonString);
        editor.apply();

        sendDataToFirestore(userMealPreferences);
    }

    /**
     * This function converts a JSON string into a HashMap
     * @return Map<String, Boolean>
     *
     */
    private Map<String,Boolean> loadMap(){
        Map<String,Boolean> outputMap = new HashMap<String,Boolean>();
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("userConfigurations", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("userMealPreferences", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    Boolean value = (Boolean) jsonObject.get(key);
                    System.out.println(value);
                    outputMap.put(key, value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }

    /**
     * This function uploads data to Cloud Firestore
     * @params HashMap<String, Boolean>
     *
     */
    public void sendDataToFirestore(HashMap<String, Boolean> userMealPreferences) {
        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        String userId = userConfigurations.getString("facebookUid","0");
        String userName = userConfigurations.getString("facebookName","0");
        String userEmail = userConfigurations.getString("facebookEmail","0");

        System.out.println("MEAL: " + userMealPreferences);

        User newUser = new User(userId, userName, userEmail, userMealPreferences);

        smartyFirestore.collection("users").document(userId).set(newUser)
                .addOnSuccessListener(new OnSuccessListener< Void >() {
                    public void onSuccess(Void aVoid) {
                        View contextView = findViewById(R.id.scrollView);
                        Snackbar.make(contextView, "You are all set! ", Snackbar.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception e) {
                View contextView = findViewById(R.id.scrollView);
                Snackbar.make(contextView, "ERROR: " + e.toString(), Snackbar.LENGTH_LONG).show();
                Log.d("TAG", e.toString());
            }
        });
    }
}
