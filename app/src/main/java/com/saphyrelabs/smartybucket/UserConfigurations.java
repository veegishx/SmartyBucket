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
        Switch modelTypeSwitch = (Switch) findViewById(R.id.modelSwitch);

        SharedPreferences myPreferences = getSharedPreferences("myPreferences", MODE_PRIVATE);
        String modelType = myPreferences.getString("modelType",null);

        if (modelType.equals("float")) {
            modelTypeSwitch.setChecked(true);
        } else  {
            modelTypeSwitch.setChecked(false);
        }
;

        modelTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("modelType", "float");
                    editor.commit();
                    Toast.makeText(UserConfigurations.this, "Float Model will be used.", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("modelType", "quantized");
                    editor.commit();
                    Toast.makeText(UserConfigurations.this, "Quantized Model will be used.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
