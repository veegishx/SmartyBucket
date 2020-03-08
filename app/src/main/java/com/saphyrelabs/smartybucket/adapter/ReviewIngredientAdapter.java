package com.saphyrelabs.smartybucket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.saphyrelabs.smartybucket.R;
import com.saphyrelabs.smartybucket.model.Ingredient;

import java.util.ArrayList;


public class ReviewIngredientAdapter extends RecyclerView.Adapter<ReviewIngredientAdapter.ReviewIngredientViewHolder> {

    private ArrayList<Ingredient> ingredients;
    private Context context;
    private int rowLayout;



    public class ReviewIngredientViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        FrameLayout recipesLayout;

        public ReviewIngredientViewHolder(View v) {

            super(v);
            recipesLayout = (FrameLayout) v.findViewById(R.id.ingredients_layout);
            title = (TextView) v.findViewById(R.id.ingredientTitle);
        }
    }

    public ReviewIngredientAdapter(ArrayList<Ingredient> ingredients, int rowLayout, Context context) {
        this.ingredients = ingredients;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public ReviewIngredientAdapter.ReviewIngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ReviewIngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewIngredientViewHolder holder, final int position) {
        String ingredientTitle = ingredients.get(position).getIngredientName();
        holder.title.setText(ingredientTitle);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}