package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginAccount extends AppCompatActivity {
    EditText emailInput, passwordInput;
    Button buttonEmailLogin;
    TextView loginInput;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_account);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        buttonEmailLogin = findViewById(R.id.buttonEmailLogin);
        loginInput = findViewById(R.id.loginInput);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        loginInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginAccount.this, CreateAccount.class));
            }
        });

        buttonEmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailInput.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    emailInput.setError("Password is required");
                    return;
                }

                if (password.length() < 8) {
                    passwordInput.setError("Password must be at least 8 characters!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            SharedPreferences sharedPreferencesMyAccount = getApplicationContext().getSharedPreferences("userConfigurations", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferencesMyAccount.edit();
                            editor.putString("email", email);
                            editor.putString("userUid", fAuth.getCurrentUser().getUid());
                            editor.apply();

                            Toast.makeText(LoginAccount.this, "Logged in Successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(LoginAccount.this, "Error! Please check your email and password, then try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
