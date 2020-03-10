package com.saphyrelabs.smartybucket.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.saphyrelabs.smartybucket.R;
import com.saphyrelabs.smartybucket.RecipeDetails;
import com.saphyrelabs.smartybucket.model.Recipe;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final String ingredientParameters;
    private List<Recipe> recipes;
    private Context context;
    private int rowLayout;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, ingredients, source;
        ImageView thumbnail;
        ProgressBar progressBar;
        OnItemClickListener onItemClickListener;
        FrameLayout recipesLayout;
        Button viewRecipeBtn, addToBudgetBtn;
        CardView recipeCard;

        public RecipeViewHolder(View v, OnItemClickListener onItemClickListener) {

            super(v);
            recipeCard = (CardView) v.findViewById(R.id.recipeCard);
            recipesLayout = (FrameLayout) v.findViewById(R.id.recipes_layout);
            title = (TextView) v.findViewById(R.id.recipe_title);
            ingredients = (TextView) v.findViewById(R.id.recipe_ingredients);
            thumbnail = (ImageView) v.findViewById(R.id.recipe_thumbnail);
            progressBar =(ProgressBar) v.findViewById(R.id.progress_load_photo);
            viewRecipeBtn = (Button) v.findViewById(R.id.viewRecipeBtn);
            addToBudgetBtn = (Button) v.findViewById(R.id.addToBudgetBtn);

            recipeCard.setOnClickListener(this);

            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public RecipeAdapter(String ingredientParameters, List<Recipe> recipes, int rowLayout, Context context) {
        this.ingredientParameters = ingredientParameters;
        this.recipes = recipes;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new RecipeViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, final int position) {
        Recipe model = recipes.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(model.getImage())
                .apply(requestOptions)
                .placeholder(R.drawable.ic_local_dining_black_24dp)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.thumbnail);


        int ingredients = recipes.get(position).getIncredientLines().size();

        String totalIngredients = ingredients + " ingredients";

        holder.title.setText(recipes.get(position).getLabel());
        holder.ingredients.setText(totalIngredients);

        holder.viewRecipeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecipeDetails.class);

            Recipe recipe = recipes.get(position);
            intent.putExtra("url", recipe.getUrl());
            intent.putExtra("label", recipe.getLabel());
            intent.putExtra("img", recipe.getImage());
            intent.putExtra("source", recipe.getSource());

            v.getContext().startActivity(intent);
        });

        holder.recipeCard.setOnClickListener(v -> {
            System.out.println("Tapped!");
            System.out.println(ingredientParameters);
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.IngredientSummary);
            builder.setTitle("Ingredient Summary");
            // Hardcoding values for now
            builder.setMessage("You are missing the following ingredients: Onions, Black Pepper, Tomatoes");
            builder.setPositiveButton("OK", null);
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
 }
