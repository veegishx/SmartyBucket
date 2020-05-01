package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfile extends AppCompatActivity {
    private TextView facebookAccountName, accountId;
    private Button logoutBtn;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        String facebookName = userConfigurations.getString("facebookName",null);
        String facebookAccountId = userConfigurations.getString("facebookUid",null);

        facebookAccountName = findViewById(R.id.accountName);
        facebookAccountName.setText(facebookName);

        accountId = findViewById(R.id.accountId);
        accountId.setText(facebookAccountId);

        logoutBtn = findViewById(R.id.logoutBtn);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        logoutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            updateUI();
        });

        // Initialize BottomAppBar
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        // Handle onClick event
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.homeNav:
                        Intent home = new Intent(UserProfile.this, MainActivity.class);
                        startActivity(home);
                        break;
                    case R.id.scanNav:
                        Intent scanType = new Intent(UserProfile.this, ScanType.class);
                        startActivity(scanType);
                        break;
                    case R.id.expense:
                        Intent expenses = new Intent(UserProfile.this, ViewExpenses.class);
                        startActivity(expenses);
                        break;
                    case R.id.account:
                        Intent account = new Intent(UserProfile.this, UserProfile.class);
                        startActivity(account);
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            updateUI();
        }
    }

    public void updateUI() {
        Intent mainActivity = new Intent(UserProfile.this, SignIn.class);
        startActivity(mainActivity);
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_LONG).show();
    }
}
