package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    BottomAppBar bab;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        Toast.makeText(MainActivity.this,"Settings Clicked",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this,"Floating Action Button Clicked",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this,"Item 1 Clicked",Toast.LENGTH_SHORT).show();
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
}
