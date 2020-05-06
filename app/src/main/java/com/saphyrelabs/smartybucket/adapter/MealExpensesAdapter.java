package com.saphyrelabs.smartybucket.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.saphyrelabs.smartybucket.R;
import com.saphyrelabs.smartybucket.RecipeDetails;
import com.saphyrelabs.smartybucket.model.Ingredient;
import com.saphyrelabs.smartybucket.model.Meal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MealExpensesAdapter extends RecyclerView.Adapter<MealExpensesAdapter.MealExpensesAdapterViewHolder> {
    private ArrayList<Meal> meals;
    private Context context;
    private int rowLayout;



    public class MealExpensesAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView expenseDate, expenseTotal, expenseIngredient, expenseName;
        Button viewOnline;

        public MealExpensesAdapterViewHolder(View v) {
            super(v);
            expenseDate = (TextView) v.findViewById(R.id.expenseDate);
            expenseTotal = (TextView) v.findViewById(R.id.expenseTotal);
            expenseIngredient = (TextView) v.findViewById(R.id.expenseIngredient);
            expenseName = (TextView) v.findViewById(R.id.expenseName);
            viewOnline = (Button) v.findViewById(R.id.viewOnline);
        }
    }

    public MealExpensesAdapter(ArrayList<Meal> meals, int rowLayout, Context context) {
        this.meals = meals;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public MealExpensesAdapter.MealExpensesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new MealExpensesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MealExpensesAdapterViewHolder holder, final int position) {
        double mealPrice = meals.get(position).getMealPrice();
        String dateString = meals.get(position).getDate();
        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat targetFormat = new SimpleDateFormat("dd MMMM yyyy");

        String formattedDate = targetFormat.format(date);

        holder.expenseDate.setText(formattedDate);
        for (int i = 0; i < meals.get(position).getIngredientLines().size(); i++) {
            holder.expenseIngredient.append(meals.get(position).getIngredientLines().get(i) + "\n");
        }


        if (String.valueOf(mealPrice).length() > 4) {
            holder.expenseTotal.setText(String.valueOf(mealPrice).substring(0, 4));
        } else {
            holder.expenseTotal.setText(String.valueOf(mealPrice));
        }

        holder.expenseName.setText(meals.get(position).getMealName());

        holder.viewOnline.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecipeDetails.class);

            intent.putExtra("url", meals.get(position).getUrl());
            intent.putExtra("label", meals.get(position).getMealName());
            intent.putExtra("source", meals.get(position).getMealName());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }
}