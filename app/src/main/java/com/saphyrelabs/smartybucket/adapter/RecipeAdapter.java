package com.saphyrelabs.smartybucket.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
import com.saphyrelabs.smartybucket.model.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final String ingredientParameters;
    private List<Recipe> recipes;
    private Context context;
    private int rowLayout;

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView title, ingredients, source;
        ImageView thumbnail;
        ProgressBar progressBar;
        FrameLayout recipesLayout;
        Button viewRecipeBtn, addToBudgetBtn;

        public RecipeViewHolder(View v) {

            super(v);
            recipesLayout = (FrameLayout) v.findViewById(R.id.recipes_layout);
            title = (TextView) v.findViewById(R.id.recipe_title);
            ingredients = (TextView) v.findViewById(R.id.recipe_ingredients);
            thumbnail = (ImageView) v.findViewById(R.id.recipe_thumbnail);
            progressBar =(ProgressBar) v.findViewById(R.id.progress_load_photo);
            viewRecipeBtn = (Button) v.findViewById(R.id.viewRecipeBtn);
            addToBudgetBtn = (Button) v.findViewById(R.id.addToBudgetBtn);
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
        return new RecipeViewHolder(view);
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

        Map<String, String> ingredientMap = new HashMap<String, String>();

        /*
        Here's the problem we are trying to solve:
        The API returns very specific ingredients.
         */

        // Get the ingredient labels without any numeric value. Eg: "2 Tomatoes, 3 Onions becomes "Tomatoes, Onions"
        String [] ingredientParamsLabels = ingredientParameters.replaceAll("\\d", "").split(",");

        // Get the numeric values without any ingredient labels. Eg: "2 Tomatoes, 3 Onions" becomes "2, 3"
        String [] ingredientParamsQuantity = ingredientParameters.replaceAll("[^\\d.]", "").split(",");

        /*
        Map the ingredient labels and numeric values together
        "Tomato" => "2"
        "Onions" => "3"
         */
//        for (int i = 0; i < ingredientParamsLabels.length; i++) {
//            ingredientMap.put(ingredientParamsLabels[i], ingredientParamsQuantity[i]);
//        }

        System.out.println("DEBUG");
        System.out.println("Ingredients Labels: " + ingredientParamsLabels);
        System.out.println("Ingredients Labels: " + ingredientParamsQuantity);

//       List<String> ingredientsRetrieved = new ArrayList<String>();
//       for (int i = 0; i < recipes.get(position).getIncredientLines().size(); i++) {
//           ingredientsRetrieved.add(recipes.get(position).getIncredientLines().get(i));
//       }
//
//       for (int i = 0; i < ingredientsRetrieved.size(); i++) {
//           if (ingredientsRetrieved.get(i).contains());
//       }

        int ingredients = recipes.get(position).getIncredientLines().size();

        String totalIngredients = ingredients + " ingredients";
        String recipeLink = recipes.get(position).getUrl();

        holder.title.setText(recipes.get(position).getLabel());
        holder.ingredients.setText(totalIngredients);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
