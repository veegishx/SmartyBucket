package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SetBudget.SetBudgetListenerInterface, SetPreferences.SetPreferencesListenerInterface{
    BottomAppBar bab;
    Button test1;
    private BottomSheetDialog bottomSheetDialog;
    private TextView monthlyBudgetValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieving persistent data
        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);

        test1 = (Button) findViewById(R.id.test1);
        monthlyBudgetValue = (TextView) findViewById(R.id.monthlyBudgetValue);

        test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ReviewListImageItems.class);
                startActivity(i);
            }
        });

        String modelType = userConfigurations.getString("modelType",null);
        String facebookEmail = userConfigurations.getString("facebookEmail",null);
        float budget = userConfigurations.getFloat("budget",0);
        boolean userMealPreferencesStatus = userConfigurations.getBoolean("userMealPreferencesStatus", false);

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                if (modelType == null) {
                    // Default User Settings
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("modelType", "float");
                    editor.commit();
                }


                if (budget == 0) {
                    // New user is prompted to enter budget
                    callSetBudgetPrompt();
                } else {
                    String budgetString = Float.toString(budget);
                    monthlyBudgetValue.setText(budgetString);
                    System.out.println("Budget is set to: " + userConfigurations.getFloat("budget", 0));
                }

                if (userMealPreferencesStatus == false) {
                    callSetUserMealPreferencesPrompt();
                }
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
        System.out.println("--------------------------------------------------------");

        RelativeLayout addItemBanner = findViewById(R.id.add_more_items_banner);
        addItemBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Intent registerItems = new Intent(MainActivity.this, RegisterItems.class);
                startActivity(registerItems);
            }
        });


        // Initialize BottomAppBar
        bab = findViewById(R.id.bottom_app_bar);

        // Append menu items to BottomAppBar
        bab.replaceMenu(R.menu.menu_items);

        // Handle onClick event
        bab.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
            }
        });

        // Register click event for BottomAppBar drawer icon
        bab.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNavigationMenu();
            }
        });

        // Register click event for BottomAppBar FloatingActionButton icon
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MainActivity.this, ScanType.class);
                startActivity(cameraIntent);
            }
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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
            }
        });
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
        editor.commit();
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
        editor.commit();
    }
}
