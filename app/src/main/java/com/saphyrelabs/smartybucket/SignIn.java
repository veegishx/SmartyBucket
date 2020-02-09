package com.saphyrelabs.smartybucket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class SignIn extends AppCompatActivity {
    private CallbackManager mCallbackManager;
    private static final String TAG = "FACEBOOKLOG";
    private FirebaseAuth mAuth;
    private Button facebookLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        facebookLoginBtn = findViewById(R.id.loginWithFacebook);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        facebookLoginBtn.setOnClickListener(view -> {
            LoginManager.getInstance().logInWithReadPermissions(SignIn.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(
                    mCallbackManager, new FacebookCallback<LoginResult>() {
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
                    }
            );
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null) {
            updateUI();
        }
    }

    public void updateUI() {
        Intent mainActivity  = new Intent(SignIn.this, MainActivity.class);
        startActivity(mainActivity);
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
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        SharedPreferences sharedPreferencesMyAccount = getApplicationContext().getSharedPreferences("userConfigurations", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferencesMyAccount.edit();
                        editor.putString("facebookEmail", user.getEmail());
                        editor.putString("facebookName", user.getDisplayName());
                        editor.putString("facebookUid", user.getUid());
                        editor.apply();
                        updateUI();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(SignIn.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                    // ...
                });
    }
}
