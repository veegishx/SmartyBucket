package com.saphyrelabs.smartybucket.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> {

    private List<Recipe> recipes;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public RecipeAdapter(List<Recipe> recipe, Context context) {
        this.recipes = recipe;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(view, onItemClickListener);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holders, int position) {
        final MyViewHolder holder = holders;
        Recipe model = recipes.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(model.getUrlToImage())
                .apply(requestOptions)
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

        holder.title.setText(model.getTitle());
        holder.ingredients.setText(model.getIngredients());
        holder.source.setText(model.getLink());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       TextView title, ingredients, source;
       ImageView thumbnail;
       ProgressBar progressBar;
       OnItemClickListener onItemClickListener;
       public MyViewHolder(View itemView, OnItemClickListener onItemClickListener) {

           super(itemView);

           itemView.setOnClickListener(this);
           title = itemView.findViewById(R.id.recipe_title);
           ingredients = itemView.findViewById(R.id.recipe_ingredients);
           source = itemView.findViewById(R.id.recipe_href);
           thumbnail = itemView.findViewById(R.id.recipe_thumbnail);
           progressBar = itemView.findViewById(R.id.progress_load_photo);

           this.onItemClickListener = onItemClickListener;
       }

       @Override
        public void onClick(View v) {
           onItemClickListener.onItemClick(v, getAdapterPosition());
       }
    }
}
