package com.saphyrelabs.smartybucket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyProfile extends AppCompatActivity {
    private TextView facebookAccountName;
    private Button logoutBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        SharedPreferences myAccount = getSharedPreferences("myAccount", MODE_PRIVATE);
        String facebookName = myAccount.getString("facebookName",null);

        facebookAccountName = (TextView) findViewById(R.id.accountName);
        facebookAccountName.setText(facebookName);

        logoutBtn = (Button) findViewById(R.id.logoutBtn);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                updateUI();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser ==null) {
            updateUI();
        }
    }

    public void updateUI() {
        Intent mainActivity  = new Intent(MyProfile.this, SignIn.class);
        startActivity(mainActivity);
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_LONG).show();
    }
}
