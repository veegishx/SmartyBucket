package com.saphyrelabs.smartybucket.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.saphyrelabs.smartybucket.R;
import com.saphyrelabs.smartybucket.model.Ingredient;

import java.util.ArrayList;
import java.util.List;


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
            title.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                public void afterTextChanged(Editable editable) {}
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(title.getTag()!=null){
                        ingredients.set((int)title.getTag(), new Ingredient(charSequence.toString()));
                    }
                }
            });
        }
    }

    public void setNewIngredientName(int position, String title) {
        ingredients.get(position).setIngredientName(title);
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
        holder.title.setTag(position);
        holder.title.setText(ingredients.get(position).getIngredientName());
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }
}