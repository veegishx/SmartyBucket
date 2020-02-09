package com.saphyrelabs.smartybucket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

public class setPreferences extends AppCompatDialogFragment {
    private setPreferences.SetPreferencesListenerInterface listener;
    private CheckBox balancedDiet, highProteinDiet, sugarDiet, vegetarianDiet, veganDiet, lowFatDiet;
    private HashMap<String, Boolean> userMealPreferences = new HashMap<String, Boolean>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_set_budget, null);

        balancedDiet = (CheckBox) view.findViewById(R.id.balancedDiet);
        highProteinDiet = (CheckBox) view.findViewById(R.id.highProteinDiet);
        sugarDiet = (CheckBox) view.findViewById(R.id.sugarDiet);
        vegetarianDiet = (CheckBox) view.findViewById(R.id.vegetarianDiet);
        veganDiet = (CheckBox) view.findViewById(R.id.veganDiet);
        lowFatDiet = (CheckBox) view.findViewById(R.id.lowFatDiet);

        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Pass data to setData in MainActivity

                        if (balancedDiet.isChecked()) {
                            userMealPreferences.put("balancedDiet", true);
                        } else {
                            userMealPreferences.put("balancedDiet", false);
                        }

                        if (highProteinDiet.isChecked()) {
                            userMealPreferences.put("highProteinDiet", true);
                        } else {
                            userMealPreferences.put("highProteinDiet", false);
                        }

                        if (sugarDiet.isChecked()) {
                            userMealPreferences.put("sugarDiet", true);
                        } else {
                            userMealPreferences.put("sugarDiet", false);
                        }

                        if (vegetarianDiet.isChecked()) {
                            userMealPreferences.put("vegetarianDiet", true);
                        } else {
                            userMealPreferences.put("vegetarianDiet", false);
                        }

                        if (veganDiet.isChecked()) {
                            userMealPreferences.put("veganDiet", true);
                        } else {
                            userMealPreferences.put("veganDiet", false);
                        }

                        if (lowFatDiet.isChecked()) {
                            userMealPreferences.put("lowFatDiet", true);
                        } else {
                            userMealPreferences.put("lowFatDiet", false);
                        }

                        listener.setData(userMealPreferences);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (setPreferences.SetPreferencesListenerInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement SetPreferencesListenerInterface");
        }
    }

    public interface SetPreferencesListenerInterface {
        void setData(HashMap<String, Boolean> userMealPrefereces);
    }
}
