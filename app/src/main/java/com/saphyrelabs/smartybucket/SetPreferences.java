package com.saphyrelabs.smartybucket;

import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.HashMap;

public class SetPreferences extends AppCompatDialogFragment {
    private SetPreferences.SetPreferencesListenerInterface listener;
    private RadioGroup radioGroup;
    private RadioButton balancedDiet, highProteinDiet, lowFatDiet, selectedRadioButton;
    private HashMap<String, Boolean> userMealPreferences = new HashMap<String, Boolean>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_set_preferences, null);

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        balancedDiet = (RadioButton) view.findViewById(R.id.balanced);
        highProteinDiet = (RadioButton) view.findViewById(R.id.high_protein);
        lowFatDiet = (RadioButton) view.findViewById(R.id.low_fat);

        userMealPreferences.put("balanced", false);
        userMealPreferences.put("high-protein", false);
        userMealPreferences.put("low-fat", false);
        userMealPreferences.put("low-carb", false);

        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Pass data to setData in MainActivity

                        if (radioGroup.getCheckedRadioButtonId() != -1) {
                            int selectedId = radioGroup.getCheckedRadioButtonId();
                            selectedRadioButton = (RadioButton) view.findViewById(selectedId);
//
                            if (selectedRadioButton == balancedDiet) {
                                userMealPreferences.put("balanced", true);
                            } else if (selectedRadioButton == highProteinDiet) {
                                userMealPreferences.put("high-protein", true);
                            } else if (selectedRadioButton == lowFatDiet) {
                                userMealPreferences.put("low-fat", true);
                            } else {
                                userMealPreferences.put("low-carb", true);
                            }
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
            listener = (SetPreferences.SetPreferencesListenerInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement SetPreferencesListenerInterface");
        }
    }

    public interface SetPreferencesListenerInterface {
        void setData(HashMap<String, Boolean> userMealPrefereces);
    }
}
