package com.saphyrelabs.smartybucket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class MyProfile extends AppCompatActivity {
    private TextView facebookAccountName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        SharedPreferences myAccount = getSharedPreferences("myAccount", MODE_PRIVATE);
        String facebookName = myAccount.getString("facebookName",null);

        facebookAccountName = (TextView) findViewById(R.id.accountName);
        facebookAccountName.setText(facebookName);
    }
}
