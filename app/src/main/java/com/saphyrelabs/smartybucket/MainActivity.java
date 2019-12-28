package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    BottomAppBar bab;
    private BottomSheetDialog bottomSheetDialog;
    private CallbackManager mCallbackManager;
    private static final String TAG = "FACEBOOKLOG";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.buttonFacebookLogin);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null) {
            init();
            updateUI();
        }
    }

    public void updateUI() {
        Toast.makeText(this, "You have been logged in via Facebook", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            SharedPreferences sharedPreferencesMyAccount = getApplicationContext().getSharedPreferences("myAccount", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferencesMyAccount.edit();
                            editor.putString("facebookEmail", user.getEmail());
                            editor.commit();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void init() {
        setContentView(R.layout.activity_main);

        // Retrieving persistent data
        SharedPreferences myPreferences = getSharedPreferences("myPreferences", MODE_PRIVATE);
        String modelType = myPreferences.getString("modelType",null);

        if (modelType == null) {
            // Default User Settings
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("modelType", "float");
            editor.commit();
        }

        SharedPreferences myAccount = getSharedPreferences("myAccount", MODE_PRIVATE);
        String facebookEmail = myAccount.getString("facebookEmail",null);

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
                        Intent myPreferencesIntent = new Intent(MainActivity.this, Preferences.class);
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
