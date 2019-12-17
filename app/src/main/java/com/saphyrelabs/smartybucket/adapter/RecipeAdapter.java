package com.saphyrelabs.smartybucket.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.saphyrelabs.smartybucket.R;
import com.saphyrelabs.smartybucket.model.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;
    private Context context;
    private int rowLayout;

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView title, ingredients, source;
        ImageView thumbnail;
        ProgressBar progressBar;
        FrameLayout recipesLayout;

        public RecipeViewHolder(View v) {

            super(v);
            recipesLayout = (FrameLayout) v.findViewById(R.id.recipes_layout);
            title = (TextView) v.findViewById(R.id.recipe_title);
            ingredients = (TextView) v.findViewById(R.id.recipe_ingredients);
            source = (TextView) v.findViewById(R.id.recipe_href);
            thumbnail = (ImageView) v.findViewById(R.id.recipe_thumbnail);
            progressBar =(ProgressBar) v.findViewById(R.id.progress_load_photo);
        }
    }

    public RecipeAdapter(List<Recipe> recipes, int rowLayout, Context context) {
        this.recipes = recipes;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, final int position) {
        holder.title.setText(recipes.get(position).getTitle());
        holder.ingredients.setText(recipes.get(position).getIngredients());
        holder.source.setText(recipes.get(position).getLink());
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
