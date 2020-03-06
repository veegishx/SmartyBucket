package com.saphyrelabs.smartybucket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfile extends AppCompatActivity {
    private TextView facebookAccountName, accountId;
    private Button logoutBtn;
    private FirebaseAuth mAuth;


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