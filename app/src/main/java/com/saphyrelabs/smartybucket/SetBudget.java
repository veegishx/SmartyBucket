package com.saphyrelabs.smartybucket;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class SetBudget extends AppCompatDialogFragment {
    private EditText userBudgetInput;
    private SetBudgetListenerInterface listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_set_budget, null);

        userBudgetInput = (EditText) view.findViewById(R.id.userBudgetInput);

        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Pass data to setData in MainActivity
                        String budgetString = userBudgetInput.getText().toString();
                        float budget = Float.valueOf(budgetString);
                        listener.setData(budget);
                    }
                });

                return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SetBudgetListenerInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement SetBudgetListenerInterface");
        }
    }

    public interface SetBudgetListenerInterface {
        void setData(float userBudget);
    }
}
