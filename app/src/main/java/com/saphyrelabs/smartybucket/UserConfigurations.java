package com.saphyrelabs.smartybucket;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserConfigurations extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Switch modelTypeSwitch = findViewById(R.id.modelSwitch);

        SharedPreferences myPreferences = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        String modelType = myPreferences.getString("modelType","float");

        try {
            if (modelType.equals("float")) {
                modelTypeSwitch.setChecked(true);
            } else  {
                modelTypeSwitch.setChecked(false);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        modelTypeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("userConfigurations", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("modelType", "float");
                editor.apply();
                Toast.makeText(UserConfigurations.this, "Float Model will be used.", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("userConfigurations", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("modelType", "quantized");
                editor.apply();
                Toast.makeText(UserConfigurations.this, "Quantized Model will be used.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
